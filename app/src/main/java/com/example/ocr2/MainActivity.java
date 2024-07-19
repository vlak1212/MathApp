package com.example.ocr2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private HistoryDatabase db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imageView;
    private EditText editTextResult;

    private Button buttonSolution;
    private EditText editResult;

    private Bitmap imgP;
    private String txt;

    private static final String PREFS_NAME = "app_prefs";
    private static final String CAMERA_PERMISSION_GRANTED = "camera_permission_granted";
    private static final String STORAGE_PERMISSION_GRANTED = "storage_permission_granted";
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        editTextResult = findViewById(R.id.Problems);
        editResult = findViewById(R.id.Result);
        Button buttonCapture = findViewById(R.id.buttonCapture);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        Button buttonAIsolve  = findViewById(R.id.solveWithAI);
        buttonSolution = findViewById(R.id.buttonSolution);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                return true;
            } else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(MainActivity.this, History.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_calculator) {
                startActivity(new Intent(MainActivity.this, Calculator.class));
                overridePendingTransition(0, 0);
                return true;
            }  else if (item.getItemId() == R.id.navigation_graph) {
                startActivity(new Intent(MainActivity.this, GraphOptions.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.navigation_forum) {
                startActivity(new Intent(MainActivity.this, Forum.class));
                overridePendingTransition(0, 0);
                return true;
            } else
            return false;
        });
        db = HistoryDatabase.getInstance(this);
        buttonCapture.setOnClickListener(v -> {
            if (!getCameraPermissionStatus()) {
                showCameraPermissionDialog();
            } else {
                dispatchTakePictureIntent();
            }
        });

        buttonUpload.setOnClickListener(v -> {
            if (!getStoragePermissionStatus()) {
                showStoragePermissionDialog();
            } else {
                dispatchPickImageIntent();
            }
        });



        buttonAIsolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextResult.getText() != null) {
                    hoiGemini(editTextResult.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Không có bài toán", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonSolution.setOnClickListener(v -> {
            if (!editTextResult.getText().toString().isEmpty()) {
                layLoiGiai(editTextResult.getText().toString());
            }
        });
    }
    private boolean getCameraPermissionStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(CAMERA_PERMISSION_GRANTED, false);
    }

    private boolean getStoragePermissionStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(STORAGE_PERMISSION_GRANTED, false);
    }
    private void showCameraPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu Quyền Sử dụng Camera")
                .setMessage("Ứng dụng cần quyền truy cập vào camera của bạn. Vui lòng cấp quyền để tiếp tục.")
                .setPositiveButton("Cho phép", (dialog, which) -> {
                    saveCameraPermissionStatus(true);
                    requestCameraPermission();
                })
                .setNegativeButton("Từ chối", null)
                .show();
    }

    private void showStoragePermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu Quyền Truy cập Bộ nhớ")
                .setMessage("Ứng dụng cần quyền truy cập vào bộ nhớ của bạn. Vui lòng cấp quyền để tiếp tục.")
                .setPositiveButton("Cho phép", (dialog, which) -> {
                    saveStoragePermissionStatus(true);
                    requestStoragePermission();
                })
                .setNegativeButton("Từ chối", null)
                .show();
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }
    private void saveCameraPermissionStatus(boolean granted) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(CAMERA_PERMISSION_GRANTED, granted).apply();
    }

    private void saveStoragePermissionStatus(boolean granted) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(STORAGE_PERMISSION_GRANTED, granted).apply();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void dispatchPickImageIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(imageBitmap);
                processImage(InputImage.fromBitmap(imageBitmap, 0));
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                Picasso.get().load(imageUri).into(imageView);
                try {
                    Bitmap imageBitmap = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), imageUri));
                    } else {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    }
                    processImage(InputImage.fromBitmap(imageBitmap, 0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processImage(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    String resultText = processTextRecognitionResult(result);
                    txt = resultText;
                    imgP = image.getBitmapInternal();
                    anhGemini(imgP);
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private String processTextRecognitionResult(Text text) {
        StringBuilder resultText = new StringBuilder();
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                resultText.append(line.getText()).append("\n");
            }
        }
        return resultText.toString();
    }

    private void hoiGemini(String problemText) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Phân tích rồi giải bài toán trên nhưng không in ra, chỉ in kết quả cuối cùng sau khi phân tích.")
                .addText(problemText)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();

                    editResult.setText(resultText);
                    saveToDatabase(problemText, resultText);

                    buttonSolution.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Lỗi khi lấy kết quả từ mô hình AI", Toast.LENGTH_SHORT).show();
                }
            }, this.getMainExecutor());
        }
    }
    private void anhGemini(Bitmap img) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("In phép toán hoặc bài toán trong ảnh trên thành text đại diện cho bài toán đó, sử dụng dấu ngoặc nếu cần thiết (phân số,..)")
                .addImage(img)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    editTextResult.setText(resultText);
                }
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }
    private void showSolutionDialog(String solutionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView solutionTextView = dialogView.findViewById(R.id.solutionText);
        Button buttonClose = dialogView.findViewById(R.id.buttonClose);

        solutionTextView.setText(solutionText);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonClose.setOnClickListener(v -> dialog.dismiss());
    }
    private  void layLoiGiai(String problem) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("Giải chi tiết bài toán sau")
                .addText(problem)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String solutionText = result.getText();
                    showSolutionDialog(solutionText);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, MainActivity.this.getMainExecutor());
        }
    }

    private void saveToDatabase(String problem, String result) {
        new Thread(() -> {
            db.historyDao().insert(new HistoryItem(problem, result));
        }).start();
    }
}
