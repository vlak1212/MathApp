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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.zanvent.mathview.MathView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private HistoryDatabase db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imageView;
    private MathView mathViewProblem;
    private TextView txtViewProblem;
    private MathView mathViewResult;

    private Button buttonSolution;
    private Button buttonSolveWithAI;

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
        mathViewProblem = findViewById(R.id.Problems);
        txtViewProblem = findViewById(R.id.Problems1);
        txtViewProblem.setVisibility(View.GONE);
        //mathViewResult = findViewById(R.id.Result);
        Button buttonCapture = findViewById(R.id.buttonCapture);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        buttonSolveWithAI = findViewById(R.id.solveWithAI);
        //buttonSolution = findViewById(R.id.buttonSolution);
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

        buttonSolveWithAI.setOnClickListener(v -> {
            if (mathViewProblem.getText() != null) {
                hoiGemini(mathViewProblem.getText().toString());
            } else {
                Toast.makeText(MainActivity.this, "Không có bài toán", Toast.LENGTH_SHORT).show();
            }
        });
        mathViewProblem.setOnClickListener(v -> switchToTextView());

        txtViewProblem.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                switchToMathView();
            }
        });

        txtViewProblem.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int[] location = new int[2];
                txtViewProblem.getLocationOnScreen(location);
                float x = event.getRawX() + txtViewProblem.getLeft() - location[0];
                float y = event.getRawY() + txtViewProblem.getTop() - location[1];
                if (x < 0 || x > txtViewProblem.getRight() - txtViewProblem.getLeft() || y < 0 || y > txtViewProblem.getBottom() - txtViewProblem.getTop()) {
                    switchToMathView();
                }
            }
            return false;
        });
        ConstraintLayout rootLayout = findViewById(R.id.view);
        rootLayout.setOnTouchListener((v, event) -> {
            if (txtViewProblem.getVisibility() == View.VISIBLE) {
                switchToMathView();
            }
            return false;
        });
    }

    private void switchToTextView() {
        txtViewProblem.setVisibility(View.VISIBLE);
        mathViewProblem.setVisibility(View.GONE);
        txtViewProblem.setText(mathViewProblem.getText());
    }

    private void switchToMathView() {
        mathViewProblem.setVisibility(View.VISIBLE);
        txtViewProblem.setVisibility(View.GONE);
        mathViewProblem.setText(convertToJqMath(txtViewProblem.getText().toString()));
    }



//        buttonSolution.setOnClickListener(v -> {
//            if (!mathViewProblems.getText().toString().isEmpty()) {
//                layLoiGiai(mathViewProblems.getText().toString());
//            }
//        });




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
                .addText("Luôn luôn giải chi tiết bài toán sau bằng các bước (Bước 1, Bước 2, Bước 3,.. Kết quả ở đầu dòng), Kết quả ở dòng cuối, đầu dòng cuối là 'Kết quả: '")
                .addText(problemText)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String solutionText = result.getText();
                    showSolutionDialog(convertToJqMath(solutionText));
                    String finalResultText = getResult(solutionText);
                    saveToDatabase(problemText, finalResultText);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Lỗi khi lấy kết quả từ mô hình AI", Toast.LENGTH_SHORT).show();
                }
            }, this.getMainExecutor());
        }
    }

    private String getResult(String solutionText) {
        String keyword = "Kết quả: ";
        String keyword1 = "Kết quả";
        int index = solutionText.indexOf(keyword);
        int index1 = solutionText.indexOf(keyword1);
        if (index != -1 && index1 != -1) {
            return solutionText.substring(index + keyword.length()).trim();
        } else if (index != -1 && index1 == -1) {
            return solutionText.substring(index + keyword.length()).trim();
        } else if (index == -1 && index1 != -1) {
            return solutionText.substring(index1 + keyword.length()).trim();
        } else {
            return solutionText;
        }
    }

//    private void hoiWolframAlpha(String problemText) {
//        new Thread(() -> {
//            try {
//                String query = "https://api.wolframalpha.com/v1/result?i=" + URLEncoder.encode(convertToText(problemText), "UTF-8") + "&appid=477HYG-LU8K72998V";
//                URL url = new URL(query);
//
//                // Open connection
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                // Get the response
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//
//                reader.close();
//                connection.disconnect();
//                String resultText = response.toString();
//
//                // Update UI with the result
//                runOnUiThread(() -> {
//                    mathViewResult.setText(resultText);
//                    saveToDatabase(problemText, resultText);
//                    buttonSolution.setVisibility(View.VISIBLE);
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi khi lấy kết quả từ Wolfram Alpha", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }

    private String convertToText(String problem) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("Chuyển text dạng mathView trên về dạng text thường")
                .addText(problem)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String txt = String.valueOf(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, MainActivity.this.getMainExecutor());
        }
        return txt;
    }

    public static String convertToJqMath(String plainText) {
        String jqMathText = plainText;

        jqMathText = jqMathText.replaceAll("\\^(\\d+)", "^{ $1 }");

        jqMathText = jqMathText.replaceAll("_([a-zA-Z0-9]+)", "_{ $1 }");

        jqMathText = jqMathText.replace("\\$", "$");
        jqMathText = jqMathText.replace("\\\\", "\\");

        jqMathText = jqMathText.replace("{", "\\{");
        jqMathText = jqMathText.replace("}", "\\}");

        jqMathText = jqMathText.replaceAll("\\\\text\\{(.*?)\\}", "$1");
        jqMathText = jqMathText.replaceAll("\\\\html\\{(.*?)\\}", "$1");

        jqMathText = jqMathText.replace("\\bo", "\\mathbf");
        jqMathText = jqMathText.replace("\\it", "\\textit");
        jqMathText = jqMathText.replace("\\bi", "\\textbf{\\textit");

        jqMathText = jqMathText.replace("\\sc", "\\mathscr");
        jqMathText = jqMathText.replace("\\fr", "\\mathfrak");

        jqMathText = jqMathText.replace("\\ov", "\\overline");

        jqMathText = jqMathText.replaceAll("\\\\table\\{(.*?)\\}", "\\begin{matrix} $1 \\end{matrix}");

        jqMathText = jqMathText.replaceAll("\\$\\$(.*?)\\$\\$", "\\[ $1 \\]");
        jqMathText = jqMathText.replaceAll("\\$(.*?)\\$", "\\( $1 \\)");

        jqMathText = jqMathText.replace("↖", "\\overset");
        jqMathText = jqMathText.replace("↙", "\\underset");

        jqMathText = jqMathText.replaceAll("Bước", "\\\\Bước");
        jqMathText = jqMathText.replaceAll("Kết quả", "\\\\Kết quả");


        return jqMathText;
    }
    private void anhGemini(Bitmap img) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("In phép toán hoặc bài toán trong ảnh trên thành text")
                .addImage(img)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    mathViewProblem.setText(convertToJqMath(resultText));
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

        MathView solutionMathView = dialogView.findViewById(R.id.solutionText);
        Button buttonClose = dialogView.findViewById(R.id.buttonClose);
        solutionText = solutionText.replaceAll("\\\\", "<br/>");
        solutionMathView.setText(solutionText);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonClose.setOnClickListener(v -> dialog.dismiss());
    }
//
//    private void layLoiGiai(String problem) {
//        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
//                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//        Content content = new Content.Builder()
//                .addText("Giải chi tiết bài toán sau thành dạng text")
//                .addText(problem)
//                .build();
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//                @Override
//                public void onSuccess(GenerateContentResponse result) {
//                    String solutionText = result.getText();
//                    showSolutionDialog(convertToJqMath(solutionText));
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    t.printStackTrace();
//                }
//            }, MainActivity.this.getMainExecutor());
//        }
//    }

    private void saveToDatabase(String problem, String result) {
        new Thread(() -> {
            db.historyDao().insert(new HistoryItem(problem, result));
        }).start();
    }
}
