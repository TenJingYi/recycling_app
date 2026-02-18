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

import java.util.concurrent.ExecutionException;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    private final String API_KEY = "AIzaSyA56B1kUIVpsagJr1tXCGYyK8icMN87LQ4";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- NEW: Start Camera Preview ---
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        // --- MODIFIED: Scan Button Logic ---
        binding.sendPromptButton.setOnClickListener(v -> {
            // Grab the current frame from the live camera preview
            Bitmap bitmap = binding.previewView.getBitmap();

            if (bitmap != null) {
                // Pass the image to Gemini
                callGeminiAI(bitmap);
            } else {
                Toast.makeText(getContext(), "Camera not ready", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- NEW: CameraX Implementation ---
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

    // --- MODIFIED: Now accepts a Bitmap instead of a String ---
    private void callGeminiAI(Bitmap bitmap) {
        binding.sendPromptProgressBar.setVisibility(View.VISIBLE);
        binding.responseTextView.setText("Analyzing image...");

        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Create multimodal content (Image + Text)
        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText("Act as a recycling expert. What is this item and which bin does it go in?")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.sendPromptProgressBar.setVisibility(View.GONE);
                        binding.responseTextView.setText(result.getText());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}