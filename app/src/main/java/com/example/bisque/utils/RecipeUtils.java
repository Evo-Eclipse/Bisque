package com.example.bisque.utils;

import com.example.bisque.db.Recipe;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class RecipeUtils {

    private static final Gson gson = new Gson();

    public static String stepsToJson(List<Recipe.Step> steps) {
        return gson.toJson(steps);
    }

    public static List<Recipe.Step> jsonToSteps(String json) {
        Type listType = new TypeToken<List<Recipe.Step>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
