package com.example.zooseeker.repositories;

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

    @Query("SELECT id, name FROM (SELECT a.id, a.name FROM animal_items AS a WHERE a.name LIKE '%' || :query || '%'" +
            " UNION " +
            "SELECT a.id, a.name FROM animal_items AS a, animal_tags AS t JOIN animal_tags ON t.animalId = a.id WHERE t.tag LIKE '%' || :query || '%') " +
            "ORDER BY CASE WHEN name LIKE :query || '%' THEN 1 " +
            "ELSE 2 END, name ASC")
    List<Animal> get(String query);

    @Query("SELECT * FROM animal_items WHERE id = :id")
    Animal getById(String id);

    @Update
    int update(Animal animal);

    @Delete
    int delete(Animal animal);

    @Insert
    List<Long> insertAll(List<Animal> animals);
}
