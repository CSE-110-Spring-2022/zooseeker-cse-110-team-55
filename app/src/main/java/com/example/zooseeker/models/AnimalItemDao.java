package com.example.zooseeker.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zooseeker.models.Animal;

import java.util.List;

@Dao
public interface AnimalItemDao {

    @Insert
    long insert(Animal animal);

    @Query("SELECT * FROM `animal_items` WHERE `name` LIKE '%' || :query || '%'")
    List<Animal> get(String query);

    @Update
    int update(Animal animal);

    @Delete
    int delete(Animal animal);

    @Insert
    List<Long> insertAll(List<Animal> animals);
}
