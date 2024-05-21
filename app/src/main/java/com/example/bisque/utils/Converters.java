package com.example.bisque.utils;

import androidx.room.TypeConverter;

import com.example.bisque.db.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Recipe.Step> fromStepString(String value) {
        Type listType = new TypeToken<List<Recipe.Step>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStepList(List<Recipe.Step> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
