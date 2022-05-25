package com.example.zooseeker.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class SavedExhibit {
    public String id;
    public String group_id;

    public static List<SavedExhibit> fromJson(Reader reader) {
        var gson = new Gson();
        var type = new TypeToken<List<SavedExhibit>>() {}.getType();
        return gson.fromJson(reader, type);
    }

    public static void toJson(List<SavedExhibit> savedExhibits, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<SavedExhibit>>() {}.getType();
        gson.toJson(savedExhibits, type, writer);
        writer.flush();
        writer.close();
    }
}
