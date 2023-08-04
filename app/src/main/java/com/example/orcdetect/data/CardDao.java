package com.example.orcdetect.data;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.orcdetect.model.CardModel;

import java.util.List;
@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CardModel card);
    @NonNull
    @Query("Select * from Cards order by id")
    List<CardModel> getCards();
    @Delete
    void deleteItem(CardModel card);
    @Query("SELECT * FROM Cards WHERE id = :itemId")
    CardModel getItemById(int itemId);

}
