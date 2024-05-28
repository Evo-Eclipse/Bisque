package com.example.bisque.ui.recipe;

import android.app.Notification;
import android.app.NotificationManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bisque.MyApp;
import com.example.bisque.R;
import com.example.bisque.db.Recipe;
import com.example.bisque.ui.graph.GraphView;

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
    private static final int VIEW_TYPE_GRAPH = 6;

    private final Recipe recipe;
    private final List<Object> items;

    public RecipeAdapter(Recipe recipe) {
        this.recipe = recipe;
        this.items = new ArrayList<>();
        prepareItems();
    }

    private void prepareItems() {
        // Add description
        items.add(recipe.getDescription());

        // Add ingredients
        items.addAll(recipe.getIngredients());

        // Add graph placeholder
        items.add(new Object()); // Placeholder for the graph

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
        } else if (item instanceof Object) {
            return VIEW_TYPE_GRAPH;
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
                return new TimerViewHolder(view);
            case VIEW_TYPE_GRAPH:
                view = inflater.inflate(R.layout.item_graph_view, parent, false);
                return new GraphViewHolder(view);
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
            ((TimerViewHolder) holder).bind(stepTimer.stepDuration);
        } else if (holder instanceof GraphViewHolder) {
            ((GraphViewHolder) holder).bind(recipe);
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

    static class TimerViewHolder extends RecyclerView.ViewHolder {
        private final TextView timerText;
        private final Button startButton;
        private final Button pauseButton;
        private final Button stopButton;

        private Handler handler;
        private Runnable runnable;
        private int initSeconds;
        private int seconds;
        private boolean isRunning;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            timerText = itemView.findViewById(R.id.text_timer);
            startButton = itemView.findViewById(R.id.button_start);
            pauseButton = itemView.findViewById(R.id.button_pause);
            stopButton = itemView.findViewById(R.id.button_stop);
            handler = new Handler(Looper.getMainLooper());
        }

        public void bind(int stepDuration) {
            initSeconds = seconds = stepDuration * 60; // Convert minutes to seconds
            updateTimerText();

            startButton.setOnClickListener(v -> startTimer());
            pauseButton.setOnClickListener(v -> pauseTimer());
            stopButton.setOnClickListener(v -> stopTimer());
        }

        private void startTimer() {
            if (!isRunning) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (seconds > 0) {
                            seconds--;
                            updateTimerText();
                            handler.postDelayed(this, 1000);
                        } else {
                            handler.removeCallbacks(runnable);
                            triggerNotificationAndToast();
                        }
                    }
                };
                handler.post(runnable);
                isRunning = true;
            }
        }

        private void pauseTimer() {
            if (isRunning) {
                handler.removeCallbacks(runnable);
                isRunning = false;
            }
        }

        private void stopTimer() {
            handler.removeCallbacks(runnable);
            seconds = initSeconds;
            updateTimerText();
            isRunning = false;
        }

        private void updateTimerText() {
            int minutes = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", minutes, secs));
        }

        private void triggerNotificationAndToast() {
            Context context = itemView.getContext();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications_timer_24dp) // Replace with your own icon
                    .setContentTitle("Timer Finished")
                    .setContentText("Ding! Ding! Step's done, onto the next one!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();

            if (notificationManager != null) {
                notificationManager.notify(getAdapterPosition(), notification);
            }

            // Toast message
            Toast.makeText(context, "Ding! Ding! Step's done, onto the next one!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    static class GraphViewHolder extends RecyclerView.ViewHolder {
        private final GraphView graphView;

        public GraphViewHolder(@NonNull View itemView) {
            super(itemView);
            graphView = itemView.findViewById(R.id.graph_view);
        }

        public void bind(Recipe recipe) {
            graphView.setSteps(recipe.getSteps());
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
