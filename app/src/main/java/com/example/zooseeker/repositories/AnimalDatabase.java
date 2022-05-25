package com.example.zooseeker.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zooseeker.models.db.Animal;
import com.example.zooseeker.models.db.AnimalTag;

import java.util.concurrent.Executors;

@Database(entities = {Animal.class, AnimalTag.class}, version = 2)
public abstract class AnimalDatabase extends RoomDatabase {
    private static AnimalDatabase singleton = null;

    public abstract AnimalItemDao animalItemDao();
    public abstract AnimalTagDao animalTagDao();

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
                            var animals = Animal.loadJSON(context, "sample_node_info.json");

                            singleton.animalItemDao().insert(animals);
                            for (Animal animal : animals) {
                                for (String tag : animal.tags) {
                                    AnimalTag animalTag = new AnimalTag(animal.id, tag);
                                    singleton.animalTagDao().insert(animalTag);
                                }
                            }
                        });

                    }
                })
                .build();
    }

    // test by alfred
    @VisibleForTesting
    public static void injectTestDatabase(AnimalDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }
}
