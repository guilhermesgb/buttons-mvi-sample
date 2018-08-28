package com.github.guilhermesgb.buttons.view.renderers;

import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.guilhermesgb.buttons.R;
import com.github.guilhermesgb.buttons.model.Button;
import com.github.guilhermesgb.buttons.view.ButtonItemView;
import com.pedrogomez.renderers.Renderer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A "basic button" renderer is a renderer expecting a simple layout with
 *  an AppCompatButton of id R.id.buttonView and a TextView of id R.id.nameView;
 */
@SuppressWarnings("WeakerAccess")
public abstract class BasicButtonRenderer extends Renderer<ButtonItemView> {

    @BindView(R.id.buttonView) AppCompatButton buttonView;
    @BindView(R.id.nameView) TextView nameView;

    @Override
    protected void setUpView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void hookListeners(View rootView) {}

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(defineLayoutResource(), parent, false);
    }

    @Override
    public void render() {
        Button button = getContent().getButton();
        nameView.setText(button.getName());
        ButtonItemView.ButtonClickedCallback callback = getContent().getCallback();
        buttonView.setOnClickListener(view -> callback.onButtonClicked(button));
    }

    abstract int defineLayoutResource();

}
