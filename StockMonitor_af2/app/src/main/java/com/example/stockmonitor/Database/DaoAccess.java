package com.example.stockmonitor.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.stockmonitor.Bookmodel_for_service.ListModel;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insert(ListModel listModel);

    @Query("DELETE FROM ListModel_table")
    void deleteAll();

    @Update
    void update(ListModel listModel);

    @Delete
    void delete(ListModel listModel);

    @Query("SELECT * from listmodel_table ORDER BY name ASC")
    List<ListModel> getAllListModels();

    @Update
    void updateAll(ListModel... ListModels);

}
