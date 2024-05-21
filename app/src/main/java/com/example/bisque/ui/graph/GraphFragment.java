package com.example.bisque.ui.graph;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bisque.R;
import com.example.bisque.databinding.FragmentGraphBinding;
import com.example.bisque.db.Recipe;
import com.example.bisque.ui.recipe.RecipeViewModel;

public class GraphFragment extends Fragment {

    private FragmentGraphBinding binding;
    private RecipeViewModel recipeViewModel;
    private GraphView graphView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        binding = FragmentGraphBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        graphView = binding.graphView;

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), this::handleSelectedRecipe);

        return root;
    }

    private void handleSelectedRecipe(Recipe recipe) {
        if (recipe != null) {
            buildRecipeGraph(recipe);
            binding.textGraph.setVisibility(View.GONE);
        } else {
            binding.textGraph.setVisibility(View.VISIBLE);
        }
    }

    private void buildRecipeGraph(Recipe recipe) {
        if (recipe.getSteps() != null && !recipe.getSteps().isEmpty()) {
            graphView.setSteps(recipe.getSteps());
            binding.textGraph.setVisibility(View.GONE); // Hide the TextView when there are steps
        } else {
            binding.textGraph.setText("No steps found");
            binding.textGraph.setVisibility(View.VISIBLE); // Show the TextView when there are no steps
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}