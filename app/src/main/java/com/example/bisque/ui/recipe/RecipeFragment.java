package com.example.bisque.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bisque.databinding.FragmentRecipeBinding;
import com.example.bisque.rv.recipe.RecipeAdapter;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecipeViewModel recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            binding.title.setText(recipe.getTitle());

            RecyclerView recyclerView = binding.rvRecipe;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            RecipeAdapter recipeAdapter = new RecipeAdapter(recipe);
            recyclerView.setAdapter(recipeAdapter);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this).navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
