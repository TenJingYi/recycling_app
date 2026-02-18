package com.example.recyclingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        TextView tvEmail = view.findViewById(R.id.tvUserEmail);
        TextView tvName = view.findViewById(R.id.tvUserName);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // 1. Display Real User Data
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            // Since we haven't set up a database for 'Names' yet,
            // we'll show the part of the email before the '@' as a placeholder name
            String email = currentUser.getEmail();
            if (email != null && email.contains("@")) {
                tvName.setText(email.split("@")[0].toUpperCase());
            }
        }

        // 2. Real Logout Logic
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Navigate back to Login and clear the activity history
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}