package com.example.bisque.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT id, title, preview FROM recipe_table")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipe_table WHERE id = :id")
    Recipe getRecipeById(int id);

    @Insert
    void insert(Recipe recipe);
}
