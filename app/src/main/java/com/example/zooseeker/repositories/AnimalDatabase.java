package com.example.zooseeker.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalItemDao;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Animal.class}, version=1)
public abstract class AnimalDatabase extends RoomDatabase {

    private static AnimalDatabase singleton = null;

    public abstract AnimalItemDao animalItemDao();

    public synchronized static AnimalDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = AnimalDatabase.makeDatabase(context);

        }
        return singleton;
    }

    private static AnimalDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, AnimalDatabase.class, "animal_app.db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<Animal> animals = Animal.loadJSON(context, "sample_animals.json");
                            getSingleton(context).animalItemDao().insertAll(animals);
                        });

                    }
                })
                .build();

    }


}
