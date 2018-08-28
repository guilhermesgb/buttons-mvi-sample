package com.github.guilhermesgb.buttons.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ButtonsViewState implements Serializable, Parcelable {

    private StateType type;
    private List<Button> buttons = new LinkedList<>();
    private Throwable throwable = null;

    public enum StateType {
        LOADING, LOADING_SUCCESS, LOADING_FAILURE
    }

    public ButtonsViewState() {
        this.type = StateType.LOADING;
    }

    public ButtonsViewState(List<Button> buttons) {
        this.type = StateType.LOADING_SUCCESS;
        this.buttons = buttons;
    }

    public ButtonsViewState(Throwable throwable) {
        this.type = StateType.LOADING_FAILURE;
        this.throwable = throwable;
    }

    private ButtonsViewState(Parcel in) {
        type = (StateType) in.readSerializable();
        buttons = in.createTypedArrayList(Button.CREATOR);
        throwable = (Throwable) in.readSerializable();
    }

    public StateType getType() {
        return type;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public ButtonsViewState setButtons(List<Button> buttons) {
        this.buttons = buttons;
        return this;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static final Creator<ButtonsViewState> CREATOR = new Creator<ButtonsViewState>() {
        @Override
        public ButtonsViewState createFromParcel(Parcel in) {
            return new ButtonsViewState(in);
        }

        @Override
        public ButtonsViewState[] newArray(int size) {
            return new ButtonsViewState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(type);
        parcel.writeTypedList(buttons);
        parcel.writeSerializable(throwable);
    }

}
