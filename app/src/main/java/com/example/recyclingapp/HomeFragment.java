package com.example.recyclingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.recyclingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    // 1. Declare the binding variable
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 2. Initialize the binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 3. Set up your button clicks
        setupButtons();

        return binding.getRoot();
    }

    private void setupButtons() {
        // Clicking the "AI Scan" card
        binding.cardBtnScan.setOnClickListener(v -> {
            replaceFragment(new ScanFragment());
        });

        // Clicking the "Centers" card
        binding.cardBtnMap.setOnClickListener(v -> {
            replaceFragment(new MapFragment());
        });
    }

    // Helper method to switch fragments from inside the HomeFragment
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.addToBackStack(null); // This lets you go "Back" to home
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. Clean up to prevent memory leaks
        binding = null;
    }
}