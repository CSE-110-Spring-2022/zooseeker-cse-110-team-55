package com.example.zooseeker.repositories;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zooseeker.models.db.AnimalTag;

@Dao
public interface AnimalTagDao {
    @Insert
    long insert(AnimalTag tag);

    @Query("SELECT * FROM `animal_tags` WHERE `id`=:tagId")
    AnimalTag get(int tagId);

    @Update
    int update(AnimalTag animalTag);

    @Delete
    int delete(AnimalTag animalTag);
}
