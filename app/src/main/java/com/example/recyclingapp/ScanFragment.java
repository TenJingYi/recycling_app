package com.example.recyclingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.recyclingapp.databinding.FragmentScanBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    // Note: In a production app, keep your API keys in a local.properties file for security!
    private final String API_KEY = "AIzaSyA56B1kUIVpsagJr1tXCGYyK8icMN87LQ4";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        binding.sendPromptButton.setOnClickListener(v -> {
            String userText = binding.QueryEditText.getText().toString().trim();
            Bitmap bitmap = binding.previewView.getBitmap();

            if (!userText.isEmpty()) {
                // CHOICE 1: User typed something.
                // We pass 'null' for the bitmap so the AI only analyzes the text.
                callGeminiAI(null, userText);

                // Clear the text field after sending for better UX
                binding.QueryEditText.setText("");
                binding.QueryEditText.clearFocus();
            } else if (bitmap != null) {
                // CHOICE 2: Text is empty, so we use the Camera Image.
                callGeminiAI(bitmap, "Act as a recycling expert. What is this item and which bin does it go in?");
            } else {
                Toast.makeText(getContext(), "Please type an item or point camera at one", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callGeminiAI(Bitmap bitmap, String prompt) {
        binding.sendPromptProgressBar.setVisibility(View.VISIBLE);
        binding.responseTextView.setText("Analyzing...");

        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content.Builder contentBuilder = new Content.Builder();
        if (bitmap != null) {
            contentBuilder.addImage(bitmap);
        }
        contentBuilder.addText(prompt);

        Content content = contentBuilder.build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.sendPromptProgressBar.setVisibility(View.GONE);
                        binding.responseTextView.setText(result.getText());

                        // AWARD XP ON SUCCESSFUL AI RESPONSE
                        updateXP(10);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.sendPromptProgressBar.setVisibility(View.GONE);
                        binding.responseTextView.setText("Error: " + t.getMessage());
                    });
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void updateXP(int pointsToAdd) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        userRef.update("xp", FieldValue.increment(pointsToAdd))
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Recycling point added! +" + pointsToAdd + " XP", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Create document if it doesn't exist
                    Map<String, Object> user = new HashMap<>();
                    user.put("xp", pointsToAdd);
                    userRef.set(user);
                });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}