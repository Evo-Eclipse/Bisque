package com.example.bisque.rv.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.bisque.R;
import com.example.bisque.db.Recipe;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private OnItemClickListener listener;

    public HomeAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView preview;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            preview = itemView.findViewById(R.id.preview);
        }

        public void bind(final Recipe recipe, final OnItemClickListener listener) {
            title.setText(recipe.getTitle());
            loadImage(preview.getContext(), preview, recipe.getPreview());
            itemView.setOnClickListener(v -> listener.onItemClick(recipe));
        }

        private void loadImage(Context context, ImageView imageView, String url) {
            if (url.startsWith("http")) {
                Glide.with(context).load(url).into(imageView);
            } else {
                try {
                    InputStream inputStream = context.getAssets().open(url);
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    imageView.setImageDrawable(drawable);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}