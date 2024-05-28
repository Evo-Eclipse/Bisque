package com.example.bisque.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bisque.MyApp;
import com.example.bisque.db.AppDatabase;
import com.example.bisque.db.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private final AppDatabase database;
    private final ExecutorService executorService;

    public HomeViewModel(Application application) {
        super(application);

        database = MyApp.getDatabase();
        executorService = Executors.newSingleThreadExecutor();
        loadRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<List<Recipe>> loadRecipesByCategory(String category) {
        executorService.execute(() -> {
            List<Recipe> recipeList;
            if (category.equals("All")) {
                recipeList = database.recipeDao().getAllRecipes();
            } else {
                recipeList = database.recipeDao().getRecipesByCategory(category);
            }
            recipes.postValue(recipeList);
        });
        return recipes;
    }

    private void loadRecipes() {
        executorService.execute(() -> {
            List<Recipe> recipeList = database.recipeDao().getAllRecipes();
            recipes.postValue(recipeList);
        });
    }
}
