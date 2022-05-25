package com.example.zooseeker.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.zooseeker.util.Constant;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(tableName = Constant.ANIMAL_TABLE)
public class Animal {
    public static enum Kind {
        // The SerializedName annotation tells GSON how to convert
        // from the strings in our JSON to this Enum.
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("exhibit_group") EXHIBIT_GROUP,
        @SerializedName("intersection") INTERSECTION
    }

    @Ignore
    public Animal(@NonNull String id,
                   @Nullable String groupId,
                   @NonNull Kind kind,
                   @NonNull String name,
                   @NonNull List<String> tags,
                   @Nullable Double lat,
                   @Nullable Double lng) {
        this.id = id;
        this.groupId = groupId;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;

        if (!this.hasGroup() && (lat == null || lng == null)) {
            throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
        }
    }

    @VisibleForTesting
    public Animal(String id, String name, Kind kind) {
        this.id = id;
        this.name = name;

        groupId = null;
        this.kind = kind;
        lat = 0d;
        lng = 0d;
        tags = new ArrayList<>();
    }

    @Ignore
    public Animal(@NonNull String id,
                  @Nullable String groupId,
                  @NonNull Kind kind,
                  @NonNull String name,
                  @Nullable Double lat,
                  @Nullable Double lng) {
        this.id = id;
        this.groupId = groupId;
        this.kind = kind;
        this.name = name;
        this.tags = new ArrayList<>();
        this.lat = lat;
        this.lng = lng;

        if (!this.hasGroup() && (lat == null || lng == null)) {
            throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
        }
    }

    public static List<Animal> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Animal>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @NonNull
    public final String id;

    @ColumnInfo(name = "group_id")
    @SerializedName("group_id")
    @Nullable
    public final String groupId;

    @ColumnInfo(name = "kind")
    @SerializedName("kind")
    @NonNull
    public final Kind kind;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @NonNull
    public final String name;

    @NonNull
    @Ignore
    public final List<String> tags;

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    public final Double lat;

    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    public final Double lng;

    public boolean hasGroup() { return groupId != null; }

    public static class AnimalDisplay {
        public String id;
        public String name;
        @SerializedName("group_id")
        @ColumnInfo(name = "group_id")
        @Nullable
        public String groupId;
    }
}