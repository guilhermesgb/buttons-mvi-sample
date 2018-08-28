package com.github.guilhermesgb.buttons.model.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.github.guilhermesgb.buttons.model.Button;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ButtonDao {

    @Query("SELECT * FROM button")
    Single<List<Button>> findAll();

    @Query("DELETE FROM button")
    void deleteAll();

    @Insert
    void insertAll(List<Button> buttons);

}
