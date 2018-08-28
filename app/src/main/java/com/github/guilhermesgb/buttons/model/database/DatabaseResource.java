package com.github.guilhermesgb.buttons.model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.github.guilhermesgb.buttons.model.Button;

@TypeConverters({
    Button.class
})
@Database(
    version = 1,
    exportSchema = false,
    entities = {
        Button.class
    }
)
public abstract class DatabaseResource extends RoomDatabase {

    private static DatabaseResource instance;

    public static DatabaseResource getInstance(Context context) {
        if (instance == null && context != null) {
            instance = Room.databaseBuilder(context,
                DatabaseResource.class, "buttons-db")
                    .build();
        }
        return instance;
    }

    public abstract ButtonDao buttonDao();

}
