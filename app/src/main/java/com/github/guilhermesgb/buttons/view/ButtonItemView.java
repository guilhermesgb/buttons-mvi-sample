package com.github.guilhermesgb.buttons.view;

import com.github.guilhermesgb.buttons.model.Button;
import com.github.guilhermesgb.buttons.view.utils.RendererItemView;

public class ButtonItemView implements RendererItemView {

    private final Button button;
    private final ButtonClickedCallback callback;

    public interface ButtonClickedCallback {

        void onButtonClicked(Button button);

    }

    ButtonItemView(Button button, ButtonClickedCallback callback) {
        this.button = button;
        this.callback = callback;
    }

    public Button getButton() {
        return button;
    }

    public ButtonClickedCallback getCallback() {
        return callback;
    }

    @Override
    public int getItemViewCode() {
        return button.getType().hashCode();
    }

}
