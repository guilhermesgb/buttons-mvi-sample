package com.github.guilhermesgb.buttons.model;

import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;
import com.github.guilhermesgb.buttons.model.utils.UseCase;
import com.github.guilhermesgb.buttons.view.FetchButtonsAction;

import java.util.LinkedList;

import javax.inject.Inject;

import io.reactivex.Observable;

public class FetchButtonsUseCase extends UseCase {

    @Inject
    public FetchButtonsUseCase(ApiEndpoints apiEndpoints, DatabaseResource databaseResource) {
        super(apiEndpoints, databaseResource);
    }

    public Observable<ButtonsViewState> fetchButtons(@SuppressWarnings("unused") FetchButtonsAction action) {
        //First of all, since the action that triggers this behavior has no parameters,
        // it won't impact it in any way whatsoever, so it is not even used.
        // This is just to show how one could further extend this use case's behavior.

        //This stream below gets remote buttons by making an HTTP request,
        // and mapping the received results into their appropriate states.
        Observable<ButtonsViewState> fetchRemoteButtons = getApi().getButtons()
            .toObservable()
            .map(ButtonsViewState::new)
            .onErrorReturn(ButtonsViewState::new);

        //This stream below gets buttons stored in our local database, returning an
        // empty list if this database operation fails, since findAll's Single return
        // value makes Android Room raise an exception if no rows are found - we don't
        // want to be returning our error state in this case...
        Observable<ButtonsViewState> fetchLocalButtons = getDatabase().buttonDao().findAll()
            .toObservable()
            .map(ButtonsViewState::new)
            .onErrorReturn(throwable -> new ButtonsViewState(new LinkedList<>()));

        //Then below we are declaring a new stream which is a merge of the latest
        // states obtained in both remote and local streams above.
        return Observable.combineLatest(fetchLocalButtons, fetchRemoteButtons.cache(),
            (localState, remoteState) -> {
                switch (localState.getType()) {
                    default:
                    case LOADING_SUCCESS:
                        switch (remoteState.getType()) {
                            default:
                            case LOADING_SUCCESS:
                                //This is a side-effect of the "fetching buttons" use case:
                                //In case we have remote buttons, we discard local state
                                // in favor of remote state, persisting merged state in
                                // the local database.
                                try {
                                    getDatabase().beginTransaction();
                                    getDatabase().buttonDao().deleteAll();
                                    getDatabase().buttonDao().insertAll(remoteState.getButtons());
                                    getDatabase().setTransactionSuccessful();
                                } finally {
                                    getDatabase().endTransaction();
                                }
                                return remoteState;
                            case LOADING_FAILURE:
                                return remoteState.setButtons(localState.getButtons());
                        }
                    case LOADING_FAILURE:
                        return remoteState;
                }
            })
            .publish(mergedButtonsStream ->
                //We publish the merged states stream and combine it with the
                // local states stream to make sure local states are pushed through
                // as soon as possible (so that the user will get something to see
                // if there's persisted data available and then eventually get
                // most up-to-date data from some remote source.
                Observable.concat(fetchLocalButtons, mergedButtonsStream)
                    .startWith(new ButtonsViewState()) //The "loading" state.
            );
    }

}
