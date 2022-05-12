package com.example.zooseeker.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalTag;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.NodeInfo;

import java.util.Map;
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
                            Map<String, NodeInfo> map;
                            map = Graph.loadNodeInfo(context, "sample_node_info.json");

                            for (Map.Entry<String, NodeInfo> entry : map.entrySet()) {
                                // Insert exhibits into db
                                NodeInfo node = entry.getValue();
                                if (node.kind == NodeInfo.Kind.EXHIBIT) {
                                    Animal animal = new Animal(node.name, entry.getKey());
                                    getSingleton(context).animalItemDao().insert(animal);

                                    // Insert tags into db
                                    for (String tag : node.tags) {
                                        AnimalTag animalTag = new AnimalTag(animal.id, tag);
                                        getSingleton(context).animalTagDao().insert(animalTag);
                                    }
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
