package com.example.bisque.ui.recipe;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bisque.MyApp;
import com.example.bisque.db.AppDatabase;
import com.example.bisque.db.Recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeViewModel extends AndroidViewModel {
    private final MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, Integer>> timers = new MutableLiveData<>(new HashMap<>());
    private final AppDatabase database;
    private final ExecutorService executorService;

    public RecipeViewModel(Application application) {
        super(application);
        database = MyApp.getDatabase();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void setSelectedRecipeId(int recipeId) {
        executorService.execute(() -> {
            Recipe recipe = database.recipeDao().getRecipeById(recipeId);
            selectedRecipe.postValue(recipe);
        });
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return selectedRecipe;
    }

    public void insertRecipe(Recipe recipe) {
        executorService.execute(() -> database.recipeDao().insert(recipe));
    }
}
