package com.example.bisque.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bisque.utils.Converters;

import java.util.List;

@Entity(tableName = "recipe_table")
@TypeConverters({Converters.class})
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String preview;
    private String category;
    private String description;
    private List<String> ingredients;
    private List<Step> steps;

    public static class Step {
        private String description;
        private String warning;
        private String image;
        private int stepDuration;

        // Getters and Setters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getWarning() { return warning; }
        public void setWarning(String warning) { this.warning = warning; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public int getStepDuration() { return stepDuration; }
        public void setStepDuration(int stepDuration) { this.stepDuration = stepDuration; }
    }

    // Constructor
    public Recipe(String title, String preview, String category, String description, List<String> ingredients, List<Step> steps) {
        this.title = title;
        this.preview = preview;
        this.category = category;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) { this.steps = steps; }
}
