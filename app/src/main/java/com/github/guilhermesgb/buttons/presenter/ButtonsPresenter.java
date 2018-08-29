package com.github.guilhermesgb.buttons.presenter;

import com.github.guilhermesgb.buttons.model.ButtonsViewState;
import com.github.guilhermesgb.buttons.model.FetchButtonsUseCase;
import com.github.guilhermesgb.buttons.view.ButtonsView;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ButtonsPresenter extends MviBasePresenter<ButtonsView, ButtonsViewState> {

    private final FetchButtonsUseCase fetchButtonsUseCase;

    @Inject
    public ButtonsPresenter(FetchButtonsUseCase fetchButtonsUseCase) {
        this.fetchButtonsUseCase = fetchButtonsUseCase;
    }

    @Override
    protected void bindIntents() {
        //In MVI the presenter doesn't talk directly to the view.
        //It merely listens for intents coming from it (the 'fetchButtonsIntent' stream)
        // and connects that stream to the model's underlying business logic,
        // which in turn forward responses back to the view, in an unidirectional fashion.
        //View -> Presenter -> Model -> View -> Presenter -> Model etc......

        //This is great because then we don't have to have a presenter managing view state
        // and business logic state. The business logic is the Single Source of Truth.
        // Thus we can deal with complexity in a more comprehensive manner as our app evolves.

        //Please notice that with aid from the Mosby MVI library (originally created for MVP),
        // we barely have to write code to bind these components of our app and close the loop
        // in order to create our cycle with unidirectional data flow.
        //All we have to do is call the `intent` and `subscribeViewState` methods, passing
        //around our intents and mapping them to actions which trigger some business logic.
        //With aid of RxJava we also make sure the appropriate work happens in the appropriate thread.

        Observable<ButtonsViewState> fetchButtonsIntent
            = intent(ButtonsView::fetchButtonsIntent)
                .switchMap(action -> fetchButtonsUseCase
                    .fetchButtons(action).subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread());

        subscribeViewState(fetchButtonsIntent, ButtonsView::render);
    }

}
