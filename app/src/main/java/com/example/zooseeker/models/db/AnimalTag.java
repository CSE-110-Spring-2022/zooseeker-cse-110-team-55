package com.example.zooseeker.models.db;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "animal_tags",
        foreignKeys = @ForeignKey(entity = Animal.class,
                          parentColumns = "id",
                          childColumns = "animalId",
                          onDelete=CASCADE),
indices = @Index(value="animalId"))
public class AnimalTag {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String animalId;

    @NonNull
    public String tag;

    public AnimalTag(String animalId, String tag) {
        this.animalId = animalId;
        this.tag = tag;
    }
}
