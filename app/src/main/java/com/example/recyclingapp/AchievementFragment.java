package com.example.recyclingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AchievementFragment extends Fragment {

    private ProgressBar pbLevel;
    private TextView tvLevel, tvXpDetails;

    public AchievementFragment() { super(R.layout.fragment_achievement); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pbLevel = view.findViewById(R.id.pbLevel);
        tvLevel = view.findViewById(R.id.tvCurrentLevel);
        tvXpDetails = view.findViewById(R.id.tvXpDetails); // Add this ID to your XML

        // Inside AchievementFragment.java onViewCreated
        Button btnOption1 = view.findViewById(R.id.btnOption1);
        Button btnOption2 = view.findViewById(R.id.btnOption2);

        btnOption1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wrong! Grease ruins paper recycling.", Toast.LENGTH_SHORT).show();
        });

        btnOption2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Correct! +20 XP", Toast.LENGTH_SHORT).show();
            updateUI(20); // Reuse your updateXP method from before
            v.setEnabled(false); // Prevent multiple clicks
            btnOption1.setEnabled(false);
        });

        loadUserProgress();
    }

    private void loadUserProgress() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        // Real-time listener: Updates UI automatically when DB changes!
        userRef.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                Long xpLong = snapshot.getLong("xp");
                int currentXp = (xpLong != null) ? xpLong.intValue() : 0;

                updateUI(currentXp);
            }
        });
    }

    private void updateUI(int xp) {
        // Simple Engineering Logic: 100 XP per level
        int level = (xp / 100) + 1;
        int progressInsideLevel = xp % 100;

        tvLevel.setText("Level " + level + ": Eco-Warrior");
        pbLevel.setProgress(progressInsideLevel);
        tvXpDetails.setText(progressInsideLevel + " / 100 XP to next level");

        // Logic for unlocking badges could go here!
    }


}