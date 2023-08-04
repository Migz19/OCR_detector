package com.example.orcdetect.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.orcdetect.model.CardModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CardModel.class}, version =1, exportSchema = false)
public abstract class CardDB extends RoomDatabase {
    public abstract CardDao cardsDao();
    private static volatile CardDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized CardDB getDatabase(final Context context) {

        if (INSTANCE == null) {

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CardDB.class, "Cards")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }


        return INSTANCE;
    }
}