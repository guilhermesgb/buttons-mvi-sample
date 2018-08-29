package com.github.guilhermesgb.buttons.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.guilhermesgb.buttons.R;
import com.github.guilhermesgb.buttons.model.Button;
import com.github.guilhermesgb.buttons.model.ButtonsViewState;
import com.github.guilhermesgb.buttons.model.dagger.DaggerDependencies;
import com.github.guilhermesgb.buttons.model.dagger.DatabaseModule;
import com.github.guilhermesgb.buttons.model.dagger.NetworkModule;
import com.github.guilhermesgb.buttons.presenter.ButtonsPresenter;
import com.github.guilhermesgb.buttons.view.renderers.ButtonToBottomRenderer;
import com.github.guilhermesgb.buttons.view.renderers.ButtonToLeftRenderer;
import com.github.guilhermesgb.buttons.view.renderers.ButtonToRightRenderer;
import com.github.guilhermesgb.buttons.view.utils.RendererBuilderFactory;
import com.github.guilhermesgb.buttons.view.utils.RendererItemView;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.github.guilhermesgb.buttons.model.network.ApiResource.WILL_USE_REAL_API;

public class MainActivity extends MviActivity<ButtonsView, ButtonsPresenter> implements ButtonsView {

    @BindView(R.id.rootView) ViewGroup rootView;
    @BindView(R.id.loadingView) View loadingView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.errorView) TextView errorView;

    private RVRendererAdapter<RendererItemView> adapter;
    private ListAdapteeCollection<RendererItemView> items = new ListAdapteeCollection<>();

    PublishSubject<FetchButtonsAction> fetchButtonsIntent = PublishSubject.create();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RendererBuilder<RendererItemView> rendererBuilder = new RendererBuilderFactory<>()
            .bind(Button.ButtonType.TO_LEFT.hashCode(), new ButtonToLeftRenderer())
            .bind(Button.ButtonType.TO_BOTTOM.hashCode(), new ButtonToBottomRenderer())
            .bind(Button.ButtonType.TO_RIGHT.hashCode(), new ButtonToRightRenderer())
            .build();
        adapter = new RVRendererAdapter<>(rendererBuilder, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public ButtonsPresenter createPresenter() {
        return DaggerDependencies.builder()
            .databaseModule(new DatabaseModule(getApplicationContext()))
            .networkModule(new NetworkModule(WILL_USE_REAL_API))
            .build().buttonsPresenter();
    }

    @NonNull
    @Override
    public Observable<FetchButtonsAction> fetchButtonsIntent() {
        return fetchButtonsIntent;
    }

    /**
     * Setting up intention of fetching buttons (from both local and remote sources)
     *   as soon as this activity starts (or restarts).
     */
    @Override
    public void onStart() {
        super.onStart();
        fetchButtonsIntent.onNext(new FetchButtonsAction());
    }

    @Override
    public void render(ButtonsViewState state) {
        switch (state.getType()) {
            case LOADING:
                renderLoadingState();
                break;
            case LOADING_SUCCESS:
                renderSuccessState(state.getButtons());
                break;
            case LOADING_FAILURE:
                renderFailureState(state.getThrowable());
                break;
        }
    }

    private void renderLoadingState() {
        TransitionManager.beginDelayedTransition(rootView);
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void renderSuccessState(List<Button> buttons) {
        TransitionManager.beginDelayedTransition(rootView);
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        adapter.clear();
        for (Button button : buttons) {
            items.add(new ButtonItemView(button, clicked -> {
                String toastMessage = getString(
                    R.string.toast_button_just_clicked_format,
                    clicked.getName()
                );
                Toast.makeText(
                    getApplicationContext(),
                    toastMessage,
                    Toast.LENGTH_LONG
                ).show();
            }));
        }
        adapter.notifyDataSetChanged();
    }

    private void renderFailureState(Throwable throwable) {
        TransitionManager.beginDelayedTransition(rootView);
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        if (throwable instanceof IOException) {
            errorView.setText(getString(
                R.string.error_network_loading_buttons
            ));
        } else {
            errorView.setText(getString(
                R.string.error_loading_buttons
            ));
        }
    }

}
