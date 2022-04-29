package com.example.zooseeker.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalItemDao;
import com.example.zooseeker.models.ZooData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Database(entities = {Animal.class}, version = 1)
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
                            List<Animal> animals = new ArrayList<>();
                            Map<String, ZooData.VertexInfo> map = null;
                            try {
                                map = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (Map.Entry<String, ZooData.VertexInfo> entry : map.entrySet()) {
                                if (entry.getValue().kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                                    Animal animal = new Animal(entry.getValue().name, entry.getKey());
                                    animals.add(animal);
                                }
                            }

                            getSingleton(context).animalItemDao().insertAll(animals);
                        });

                    }
                })
                .build();

    }


}
