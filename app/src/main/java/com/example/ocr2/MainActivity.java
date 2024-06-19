package com.example.ocr2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
   // private DatabaseHelper dbHelper;
    private HistoryDatabase db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private ImageView imageView;
    private EditText editTextResult;

    private EditText editResult;
    private Button captureBtn;
    private Bitmap imgP;
    private String txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        editTextResult = findViewById(R.id.editTextResult);
        editResult = findViewById(R.id.editResult);
        Button buttonCapture = findViewById(R.id.buttonCapture);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        Button buttonAIsolve  = findViewById(R.id.solveWithAI);
        //FloatingActionButton buttonHistory = findViewById(R.id.historyButton);
        Button buttonSolve  = findViewById(R.id.solve);
        //ImageButton buttonCalculator = findViewById(R.id.buttonCalculator);
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
        buttonCapture.setOnClickListener(v -> dispatchTakePictureIntent());
//        buttonCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        });
        buttonUpload.setOnClickListener(v -> dispatchPickImageIntent());

        buttonSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtP = editTextResult.getText().toString();
                if (txtP != null && !txtP.isEmpty()) {
                    MathSolver solver = new MathSolver();
                    String result = solver.mathSolving(txtP);
                    editTextResult.setText(result);
                    //saveToDatabase(txtP, result);
                    new Thread(() -> {
                        db.historyDao().insert(new HistoryItem(txtP, result));
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Không có bài toán", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAIsolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgP != null) {
                    hoiGemini(imgP);
                    //saveToDatabase(editTextResult.getText().toString(), editResult.getText().toString());
                    new Thread(() -> {
                        db.historyDao().insert(new HistoryItem(editTextResult.getText().toString(), editResult.getText().toString()));
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Không có ảnh đầu vào", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//    private void saveToDatabase(String problem, String result) {
//        boolean isInserted = dbHelper.addData(problem, result);
//        if (isInserted) {
//            Toast.makeText(MainActivity.this, "Đã lưu vào lịch sử", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(MainActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
//        }
//    }
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
                Bundle extras = data.getExtras();
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
                    //editTextResult.setText(resultText);
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

    private void hoiGemini(Bitmap img) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDh0zhgkKH4xpH1prw1rDrI7N1O0FR1EF4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("Chỉ trả lời kết quả của bài toán trong ảnh, nếu có biến cần tìm (ví dụ x,y,z t) thì đưa ra x = , không cần giải thích")
                .addImage(img)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    editResult.setText(resultText);
                }
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
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
                    editTextResult.setText(resultText);
                }
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }
}
