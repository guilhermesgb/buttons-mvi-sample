package com.github.guilhermesgb.buttons.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Entity(
    tableName = "button"
)
public class Button implements Serializable, Parcelable {

    @PrimaryKey @NonNull private String uuid;
    @NonNull private String name;
    @NonNull private ButtonType type;

    public enum ButtonType {
        TO_LEFT, TO_BOTTOM, TO_RIGHT
    }

    public Button(@NonNull String uuid, @NonNull String name, @NonNull ButtonType type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public ButtonType getType() {
        return type;
    }

    private Button(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        type = (ButtonType) in.readSerializable();
    }

    public static final Creator<Button> CREATOR = new Creator<Button>() {
        @Override
        public Button createFromParcel(Parcel in) {
            return new Button(in);
        }

        @Override
        public Button[] newArray(int size) {
            return new Button[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeSerializable(type);
    }

    public static Button dejsonizeFrom(JsonObject json) {
        return json == null ? null : new Button(
            getRequiredUuid(json),
            getOptionalButtonName(json),
            getOptionalButtonType(json)
        );
    }

    private static String getRequiredUuid(JsonObject json) {
        return json == null || !json.has("uuid")
            ? UUID.randomUUID().toString() : json.get("uuid").getAsString();
    }

    private static String getOptionalButtonName(JsonObject json) {
        return json == null || !json.has("name")
            ? null : json.get("name").getAsString();
    }

    private static ButtonType getOptionalButtonType(JsonObject json) {
        return json == null || !json.has("type")
            ? null : ButtonType.valueOf(json.get("type").getAsString().toUpperCase());
    }

    @TypeConverter
    public static ButtonType stringToButtonType(String buttonTypeRaw) {
        return ButtonType.valueOf(buttonTypeRaw.toUpperCase());
    }

    @TypeConverter
    public static String buttonTypeToString(ButtonType buttonType) {
        return buttonType.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Button button = (Button) other;
        return uuid.equals(button.uuid) && name.equals(button.name) && type == button.type;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { uuid, name, type });
    }

}
