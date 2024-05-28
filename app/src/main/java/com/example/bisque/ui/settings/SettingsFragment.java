package com.example.bisque.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bisque.databinding.FragmentSettingsBinding;
import com.example.bisque.db.Recipe;
import com.example.bisque.ui.recipe.RecipeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private RecipeViewModel recipeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        EditText etTitle = binding.etTitle;
        EditText etCategory = binding.etCategory;
        EditText etDescription = binding.etDescription;
        EditText etIngredients = binding.etIngredients;
        EditText etSteps = binding.etSteps;

        binding.btnSaveRecipe.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String ingredientsString = etIngredients.getText().toString().trim();
            String stepsString = etSteps.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(category) || TextUtils.isEmpty(description) ||
                    TextUtils.isEmpty(ingredientsString) || TextUtils.isEmpty(stepsString)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> ingredients = parseLines(ingredientsString);
            List<Recipe.Step> steps = parseSteps(stepsString);

            if (validateRecipe(title, category, description, ingredients, steps)) {
                Recipe recipe = new Recipe(title, "", category, description, ingredients, steps);
                recipeViewModel.insertRecipe(recipe);
                Toast.makeText(getContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();

                // Clear fields
                etTitle.setText("");
                etCategory.setText("");
                etDescription.setText("");
                etIngredients.setText("");
                etSteps.setText("");
            }
        });

        return root;
    }

    private List<String> parseLines(String input) {
        return new ArrayList<>(Arrays.asList(input.split("\\n")));
    }

    private List<Recipe.Step> parseSteps(String input) {
        List<Recipe.Step> steps = new ArrayList<>();
        String[] stepDescriptions = input.split("\\n");
        Pattern pattern = Pattern.compile("(.*?)(?:\\s+i='(.*?)')?(?:\\s+w='(.*?)')?(?:\\s+d=(\\d+))?$");
        for (String stepDescription : stepDescriptions) {
            Matcher matcher = pattern.matcher(stepDescription.trim());
            if (matcher.matches()) {
                Recipe.Step step = new Recipe.Step();
                step.setDescription(matcher.group(1));
                step.setImage(matcher.group(2) != null ? matcher.group(2) : "");
                step.setWarning(matcher.group(3) != null ? matcher.group(3) : "");
                step.setStepDuration(matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0);
                steps.add(step);
            }
        }
        return steps;
    }

    private boolean validateRecipe(String title, String category, String description, List<String> ingredients, List<Recipe.Step> steps) {
        if (title.length() < 3) {
            Toast.makeText(getContext(), "Title should be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (category.length() < 3) {
            Toast.makeText(getContext(), "Category should be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (description.length() < 10) {
            Toast.makeText(getContext(), "Description should be at least 10 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (ingredients.size() < 1) {
            Toast.makeText(getContext(), "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (steps.size() < 1) {
            Toast.makeText(getContext(), "Please add at least one step", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (String ingredient : ingredients) {
            if (ingredient.length() < 3) {
                Toast.makeText(getContext(), "Each ingredient should be at least 3 characters long", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        for (Recipe.Step step : steps) {
            if (step.getDescription().length() < 5) {
                Toast.makeText(getContext(), "Each step description should be at least 5 characters long", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!step.getImage().isEmpty() && !Patterns.WEB_URL.matcher(step.getImage()).matches()) {
                Toast.makeText(getContext(), "Invalid image URL in steps", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
