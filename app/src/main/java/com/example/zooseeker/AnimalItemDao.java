package com.example.zooseeker;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface AnimalItemDao {

    @Insert
    long insert(Animal animal);

    @Query("SELECT * FROM `animal_items` WHERE `id`=:id")
    Animal get(long id);

    @Query("SELECT * FROM `animal_items` WHERE `name` LIKE '%' || :query || '%'")
    List<Animal> get(String query);

    @Update
    int update(Animal animal);

    @Delete
    int delete(Animal animal);


    List<Long> insertAll(List<Animal> animals);
}
