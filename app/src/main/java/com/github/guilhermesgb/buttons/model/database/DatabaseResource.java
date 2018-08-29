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

    public abstract ButtonDao buttonDao();

    public static DatabaseResource createInstance(Context context) {
        return Room.databaseBuilder(context, DatabaseResource.class,
            "buttons-db").build();
    }

}
