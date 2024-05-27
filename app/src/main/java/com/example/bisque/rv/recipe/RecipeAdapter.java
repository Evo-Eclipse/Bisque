package com.example.bisque.rv.recipe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bisque.R;
import com.example.bisque.db.Recipe;
import com.example.bisque.ui.recipe.RecipeViewModel;
import com.example.bisque.utils.TimerService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DESCRIPTION = 0;
    private static final int VIEW_TYPE_INGREDIENT = 1;
    private static final int VIEW_TYPE_STEP = 2;
    private static final int VIEW_TYPE_WARNING = 3;
    private static final int VIEW_TYPE_IMAGE = 4;
    private static final int VIEW_TYPE_TIMER = 5;

    private final Recipe recipe;
    private final List<Object> items;
    private final TimerService timerService;
    private final RecipeViewModel recipeViewModel;

    public RecipeAdapter(Recipe recipe, TimerService timerService, RecipeViewModel recipeViewModel) {
        this.recipe = recipe;
        this.timerService = timerService;
        this.recipeViewModel = recipeViewModel;
        this.items = new ArrayList<>();
        prepareItems();
    }

    private void prepareItems() {
        // Add description
        items.add(recipe.getDescription());

        // Add ingredients
        items.addAll(recipe.getIngredients());

        // Add steps with possible additional items (warning, image, timer)
        for (Recipe.Step step : recipe.getSteps()) {
            items.add(step);
            if (step.getWarning() != null && !step.getWarning().isEmpty()) {
                items.add(new StepWarning(step.getWarning()));
            }
            if (step.getImage() != null && !step.getImage().isEmpty()) {
                items.add(new StepImage(step.getImage()));
            }
            if (step.getStepDuration() > 4) {
                items.add(new StepTimer(step.getStepDuration()));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            if (position == 0) {
                return VIEW_TYPE_DESCRIPTION;
            } else {
                return VIEW_TYPE_INGREDIENT;
            }
        } else if (item instanceof Recipe.Step) {
            return VIEW_TYPE_STEP;
        } else if (item instanceof StepWarning) {
            return VIEW_TYPE_WARNING;
        } else if (item instanceof StepImage) {
            return VIEW_TYPE_IMAGE;
        } else if (item instanceof StepTimer) {
            return VIEW_TYPE_TIMER;
        } else {
            throw new IllegalArgumentException("Invalid view type");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_DESCRIPTION:
                view = inflater.inflate(R.layout.item_recipe_description, parent, false);
                return new DescriptionViewHolder(view);
            case VIEW_TYPE_INGREDIENT:
                view = inflater.inflate(R.layout.item_recipe_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case VIEW_TYPE_STEP:
                view = inflater.inflate(R.layout.item_recipe_step, parent, false);
                return new StepViewHolder(view);
            case VIEW_TYPE_WARNING:
                view = inflater.inflate(R.layout.item_recipe_warning, parent, false);
                return new WarningViewHolder(view);
            case VIEW_TYPE_IMAGE:
                view = inflater.inflate(R.layout.item_recipe_image, parent, false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_TIMER:
                view = inflater.inflate(R.layout.item_recipe_timer, parent, false);
                return new TimerViewHolder(view, timerService, recipeViewModel);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof DescriptionViewHolder) {
            ((DescriptionViewHolder) holder).bind((String) item, recipe.getPreview());
        } else if (holder instanceof IngredientViewHolder) {
            ((IngredientViewHolder) holder).bind((String) item);
        } else if (holder instanceof StepViewHolder) {
            Recipe.Step step = (Recipe.Step) item;
            ((StepViewHolder) holder).bind(step.getDescription(), getStepNumber(position));
        } else if (holder instanceof WarningViewHolder) {
            StepWarning stepWarning = (StepWarning) item;
            ((WarningViewHolder) holder).bind(stepWarning.warningText);
        } else if (holder instanceof ImageViewHolder) {
            StepImage stepImage = (StepImage) item;
            ((ImageViewHolder) holder).bind(stepImage.imageUrl);
        } else if (holder instanceof TimerViewHolder) {
            StepTimer stepTimer = (StepTimer) item;
            ((TimerViewHolder) holder).bind(position, stepTimer.stepDuration);
        }
    }

    private int getStepNumber(int position) {
        int stepCount = 0;
        for (int i = 0; i < position; i++) {
            if (items.get(i) instanceof Recipe.Step) {
                stepCount++;
            }
        }
        return stepCount + 1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder classes

    static class DescriptionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView preview;
        private final TextView description;

        public DescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.image_preview);
            description = itemView.findViewById(R.id.text_description);
        }

        public void bind(String descriptionText, String previewUrl) {
            description.setText(descriptionText);
            loadImage(preview.getContext(), preview, previewUrl);
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

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView ingredient;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_ingredient);
            ingredient = itemView.findViewById(R.id.text_ingredient);
        }

        public void bind(String ingredientText) {
            ingredient.setText(ingredientText);
        }
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView stepNumber;
        private final TextView stepDescription;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.text_step_number);
            stepDescription = itemView.findViewById(R.id.text_step_description);
        }

        public void bind(String stepDescriptionText, int stepNumberValue) {
            stepNumber.setText(String.valueOf(stepNumberValue));
            stepDescription.setText(stepDescriptionText);
        }
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {
        private final ImageView warningIcon;
        private final TextView warningText;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            warningIcon = itemView.findViewById(R.id.icon_warning);
            warningText = itemView.findViewById(R.id.text_warning);
        }

        public void bind(String warningTextValue) {
            warningText.setText(warningTextValue);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageStep;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStep = itemView.findViewById(R.id.image_step);
        }

        public void bind(String imageUrl) {
            loadImage(imageStep.getContext(), imageStep, imageUrl);
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

    public class TimerViewHolder extends RecyclerView.ViewHolder {
        private final TextView timerText;
        private final Button startButton;
        private final Button pauseButton;
        private final Button stopButton;

        private TimerService timerService;
        private Handler handler = new Handler(Looper.getMainLooper());
        private RecipeViewModel recipeViewModel;
        private int position;

        public TimerViewHolder(@NonNull View itemView, TimerService timerService, RecipeViewModel recipeViewModel) {
            super(itemView);
            this.timerService = timerService;
            this.recipeViewModel = recipeViewModel;
            timerText = itemView.findViewById(R.id.text_timer);
            startButton = itemView.findViewById(R.id.button_start);
            pauseButton = itemView.findViewById(R.id.button_pause);
            stopButton = itemView.findViewById(R.id.button_stop);

            startButton.setOnClickListener(v -> startTimer());
            pauseButton.setOnClickListener(v -> pauseTimer());
            stopButton.setOnClickListener(v -> stopTimer());
        }

        public void bind(int position, int stepDuration) {
            this.position = position;

            // Set initial timer duration
            recipeViewModel.updateTimer(position, stepDuration * 60); // stepDuration is in minutes, convert to seconds

            // Observe the remaining time for this timer instance
            recipeViewModel.getTimers().observe((LifecycleOwner) itemView.getContext(), timers -> {
                Integer remainingTime = timers.get(position);
                updateTimerText(remainingTime != null ? remainingTime : stepDuration * 60);
            });
        }

        private void startTimer() {
            if (timerService != null) {
                timerService.startTimer(String.valueOf(position), recipeViewModel.getTimers().getValue().get(position));
                updateTimerUI();
            }
        }

        private void pauseTimer() {
            if (timerService != null) {
                timerService.pauseTimer(String.valueOf(position));
            }
        }

        private void stopTimer() {
            if (timerService != null) {
                timerService.stopTimer(String.valueOf(position));
                handler.removeCallbacksAndMessages(null);
                updateTimerText(0);
            }
        }

        private void updateTimerUI() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (timerService != null) {
                        int remainingSeconds = timerService.getRemainingSeconds(String.valueOf(position));
                        recipeViewModel.updateTimer(position, remainingSeconds);
                        if (remainingSeconds > 0) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                }
            });
        }

        private void updateTimerText(Integer seconds) {
            if (seconds == null) {
                seconds = 0;
            }
            int minutes = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", minutes, secs));
        }
    }

    // Classes to wrap additional items (warning, image, timer) under each step

    static class StepWarning {
        String warningText;

        StepWarning(String warningText) {
            this.warningText = warningText;
        }
    }

    static class StepImage {
        String imageUrl;

        StepImage(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    static class StepTimer {
        int stepDuration;

        StepTimer(int stepDuration) {
            this.stepDuration = stepDuration;
        }
    }
}
