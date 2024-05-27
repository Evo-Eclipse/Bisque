package com.example.bisque.ui.recipe;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.example.bisque.db.Recipe;
import com.example.bisque.rv.recipe.RecipeAdapter;
import com.example.bisque.utils.TimerService;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private TimerService timerService;
    private boolean isBound = false;
    private RecipeViewModel recipeViewModel;
    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TimerService.ACTION_TIMER_TICK.equals(intent.getAction())) {
                String timerId = intent.getStringExtra(TimerService.EXTRA_TIMER_ID);
                int remainingSeconds = intent.getIntExtra(TimerService.EXTRA_REMAINING_SECONDS, 0);
                recipeViewModel.updateTimer(Integer.parseInt(timerId), remainingSeconds);
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel.getSelectedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            binding.title.setText(recipe.getTitle());

            RecyclerView recyclerView = binding.rvRecipe;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            RecipeAdapter recipeAdapter = new RecipeAdapter(recipe, timerService, recipeViewModel);
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

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter(TimerService.ACTION_TIMER_TICK);
        getActivity().registerReceiver(timerReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
        getActivity().unregisterReceiver(timerReceiver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current recipe ID
        Recipe currentRecipe = recipeViewModel.getSelectedRecipe().getValue();
        if (currentRecipe != null) {
            outState.putInt("selectedRecipeId", currentRecipe.getId());
        }
    }
}
