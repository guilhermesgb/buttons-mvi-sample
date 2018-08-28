package com.github.guilhermesgb.buttons.view;

import android.support.annotation.NonNull;

import com.github.guilhermesgb.buttons.model.ButtonsViewState;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.Observable;

public interface ButtonsView extends MvpView {

    @NonNull
    Observable<FetchButtonsAction> fetchButtonsIntent();

    void render(ButtonsViewState state);

}
