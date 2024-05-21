package com.example.bisque.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bisque.databinding.FragmentHomeBinding;
import com.example.bisque.db.Recipe;
import com.example.bisque.rv.home.HomeAdapter;
import com.example.bisque.ui.recipe.RecipeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;

        // Home RecyclerView
        RecyclerView recyclerView = binding.rvHome;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        HomeAdapter homeAdapter = new HomeAdapter(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                // In honored path to GraphFragment
                recipeViewModel.setSelectedRecipeId(recipe.getId());

                Bundle bundle = new Bundle();
                bundle.putInt("recipeId", recipe.getId());
                Navigation.findNavController(root).navigate(
                        com.example.bisque.R.id.action_navigation_home_to_recipeFragment,
                        bundle
                );
            }
        });
        recyclerView.setAdapter(homeAdapter);

        homeViewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            homeAdapter.setRecipes(recipes);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
