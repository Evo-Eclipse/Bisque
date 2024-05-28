package com.example.bisque.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bisque.R;
import com.example.bisque.databinding.FragmentHomeBinding;
import com.example.bisque.db.Recipe;
import com.example.bisque.ui.recipe.RecipeViewModel;

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

        // Setup category spinner
        Spinner categorySpinner = binding.categorySpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Load recipes based on selected category
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                homeViewModel.loadRecipesByCategory(selectedCategory).observe(getViewLifecycleOwner(), homeAdapter::setRecipes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                homeViewModel.getRecipes().observe(getViewLifecycleOwner(), homeAdapter::setRecipes);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

//