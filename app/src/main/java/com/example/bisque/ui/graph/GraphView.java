package com.example.bisque.ui.graph;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.bisque.R;
import com.example.bisque.db.Recipe;

import java.util.List;

public class GraphView extends LinearLayout {

    public GraphView(Context context) {
        super(context);
        init(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_graph, this, true);
    }

    public void setSteps(List<Recipe.Step> steps) {
        LinearLayout stepsContainer = findViewById(R.id.steps_container);
        stepsContainer.removeAllViews();

        if (steps == null || steps.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Add the first step
        View firstStepView = inflater.inflate(R.layout.item_graph_step, stepsContainer, false);
        ((TextView) firstStepView.findViewById(R.id.step_description)).setText(steps.get(0).getDescription());
        stepsContainer.addView(firstStepView);

        // Add parallel steps
        for (int i = 1; i < steps.size() - 1; i++) {
            View parallelStepView = inflater.inflate(R.layout.item_graph_step, stepsContainer, false);
            ((TextView) parallelStepView.findViewById(R.id.step_description)).setText(steps.get(i).getDescription());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parallelStepView.getLayoutParams();
            params.setMarginStart(100);
            parallelStepView.setLayoutParams(params);
            stepsContainer.addView(parallelStepView);
        }

        // Add the last step
        View lastStepView = inflater.inflate(R.layout.item_graph_step, stepsContainer, false);
        ((TextView) lastStepView.findViewById(R.id.step_description)).setText(steps.get(steps.size() - 1).getDescription());
        stepsContainer.addView(lastStepView);
    }
}
