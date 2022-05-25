package com.example.zooseeker.repositories;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zooseeker.models.db.Animal;
import com.example.zooseeker.models.db.Animal.AnimalDisplay;

import java.util.List;

@Dao
public interface AnimalItemDao {

    @Insert
    long insert(Animal animal);

    @Insert
    void insert(List<Animal> animal);

    @Query("SELECT id, name, group_id FROM (SELECT a.id, a.name, a.group_id FROM animals AS a WHERE a.kind = 'EXHIBIT' AND a.name LIKE '%' || :query || '%'" +
            " UNION " +
            "SELECT a.id, a.name, a.group_id FROM animals AS a, animal_tags AS t JOIN animal_tags ON t.animalId = a.id WHERE t.tag LIKE '%' || :query || '%') " +
            "ORDER BY CASE WHEN name LIKE :query || '%' THEN 1 " +
            "ELSE 2 END, name ASC")
    List<AnimalDisplay> getDisplay(String query);

    @Query("SELECT id, name, kind FROM (SELECT a.id, a.name, a.kind FROM animals AS a WHERE a.kind = 'EXHIBIT' AND a.name LIKE '%' || :query || '%'" +
            " UNION " +
            "SELECT a.id, a.name, a.kind FROM animals AS a, animal_tags AS t JOIN animal_tags ON t.animalId = a.id WHERE t.tag LIKE '%' || :query || '%') " +
            "ORDER BY CASE WHEN name LIKE :query || '%' THEN 1 " +
            "ELSE 2 END, name ASC")
    List<Animal> get(String query);

    @Query("SELECT * FROM animals WHERE id = :id")
    AnimalDisplay getById(String id);

    @Update
    int update(Animal animal);

    @Delete
    int delete(Animal animal);

    @Insert
    List<Long> insertAll(List<Animal> animals);
}
