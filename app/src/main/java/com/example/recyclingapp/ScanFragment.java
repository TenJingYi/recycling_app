package com.example.recyclingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.recyclingapp.databinding.FragmentScanBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    private static final int CAMERA_PERMISSION_CODE = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);

        // Check for permission before starting camera
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        // 2. ADD THE SCAN BUTTON LOGIC HERE
        binding.btnScan.setOnClickListener(v -> {
            binding.tvResult.setText("Analyzing...");
            runObjectDetection();
        });

        return binding.getRoot();

//        binding.btnScan.setOnClickListener(v -> {
//            binding.tvResult.setText("Item Identified: Plastic Bottle");
//            // Next step: Integrate actual AI Model here
//        });
//
//        return binding.getRoot();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Bind the preview to our XML PreviewView
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                // Select the back camera
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind everything and bind this to the lifecycle
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void runObjectDetection() {
        // 1. Configure the detector (Single image, high accuracy)
        ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableClassification()  // This identifies WHAT the object is
                .build();

        ObjectDetector objectDetector = ObjectDetection.getClient(options);

        // 2. Get the bitmap from your PreviewView
        android.graphics.Bitmap bitmap = binding.previewView.getBitmap();

        if (bitmap != null) {
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            // 3. Process the image
            objectDetector.process(image)
                    .addOnSuccessListener(detectedObjects -> {
                        if (detectedObjects.isEmpty()) {
                            binding.tvResult.setText("No item detected. Try again!");
                        } else {
                            // Get the first detected object's label
                            String label = "Recyclable Item";
                            if (!detectedObjects.get(0).getLabels().isEmpty()) {
                                label = detectedObjects.get(0).getLabels().get(0).getText();
                            }
                            binding.tvResult.setText("Detected: " + label);

                            // Logic to award points
                            updatePoints();
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.tvResult.setText("Error analyzing image.");
                    });
        }
    }

    private void updatePoints() {
        // Here you would normally update a database or SharedPrefs
        Toast.makeText(getContext(), "+10 Green Points!", Toast.LENGTH_SHORT).show();
    }
}