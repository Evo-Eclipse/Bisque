package com.example.bisque.ui.recipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bisque.R;
import com.example.bisque.databinding.FragmentRecipeBinding;
import com.example.bisque.utils.BlurUtil;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private RecipeViewModel recipeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            binding.title.setText(recipe.getTitle());

            RecyclerView recyclerView = binding.rvRecipe;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            RecipeAdapter recipeAdapter = new RecipeAdapter(recipe);
            recyclerView.setAdapter(recipeAdapter);

            // Delaying the blur effect application to ensure the layout is drawn
            // root.post(() -> applyBlurEffectToBackground(root, binding.blurredBackground));
            applyBlurEffectToBackground(root, binding.blurredBackground);
        });

        return root;
    }

    private void applyBlurEffectToBackground(View root, ImageView blurredBackgroundView) {
        root.post(() -> {
            Bitmap bitmap = getBitmapFromView(root);
            if (bitmap != null) {
                Bitmap blurredBitmap = BlurUtil.blur(getContext(), bitmap, 25f);
                blurredBitmap = BlurUtil.applyOverlay(getContext(), blurredBitmap, getColor(getContext()), 128);
                blurredBackgroundView.setImageDrawable(new BitmapDrawable(getResources(), blurredBitmap));
                blurredBackgroundView.setVisibility(View.VISIBLE);
            }
        });
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private int getColor(Context context) {
        boolean isDarkTheme = (context.getResources().getConfiguration().uiMode &
                context.getResources().getConfiguration().UI_MODE_NIGHT_MASK) ==
                context.getResources().getConfiguration().UI_MODE_NIGHT_YES;
        return isDarkTheme ? context.getResources().getColor(R.color.dark_accent) :
                context.getResources().getColor(R.color.light_accent);
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
