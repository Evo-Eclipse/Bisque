package com.example.bisque.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bisque.R;
import com.example.bisque.databinding.FragmentHomeBinding;
import com.example.bisque.ui.recipe.RecipeViewModel;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecipeViewModel recipeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvHome;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        HomeAdapter homeAdapter = new HomeAdapter(recipe -> {
            recipeViewModel.setSelectedRecipeId(recipe.getId());

            Bundle bundle = new Bundle();
            bundle.putInt("recipeId", recipe.getId());

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();

            Navigation.findNavController(root).navigate(
                    R.id.action_navigation_home_to_recipeFragment,
                    bundle,
                    navOptions
            );
        });
        recyclerView.setAdapter(homeAdapter);

        // Setup category RecyclerView
        RecyclerView categoryRecyclerView = binding.categoryRecyclerView;
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, selectedCategory -> {
            homeViewModel.loadRecipesByCategory(selectedCategory).observe(getViewLifecycleOwner(), homeAdapter::setRecipes);
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Load all recipes initially
        homeViewModel.getRecipes().observe(getViewLifecycleOwner(), homeAdapter::setRecipes);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
