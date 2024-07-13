package com.example.ocr2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Calculator extends AppCompatActivity {

    private TextView ndScreen;
    private TextView stScreen;
    private Button btnAC, btnC, btnOpen, btnClose, btnSin, btnCos, btnTan, btnLog, btnLn,
            btnFact, btnSquare, btnSqrt, btnIn, b0, b9, b8, b7, b6, b5, b4, b3, b2, b1,
            btnPi, btnMul, btnMinus, btnPlus, btnEqual, btnDot, btnDiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_calculator);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_calculator) {
                return true;
            } else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(Calculator.this, History.class));
                return true;
            }  else if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(Calculator.this, MainActivity.class));
                return true;
            }  else if (item.getItemId() == R.id.navigation_graph) {
                startActivity(new Intent(Calculator.this, GraphOptions.class));
                return true;
            } else if (item.getItemId() == R.id.navigation_forum) {
                startActivity(new Intent(Calculator.this, Forum.class));
                overridePendingTransition(0, 0);
                return true;
            } else
                return false;
        });
        ndScreen = findViewById(R.id.secondSrc);
        stScreen = findViewById(R.id.firstScreen);
        btnAC = findViewById(R.id.buttonAC);
        btnC = findViewById(R.id.buttonC);
        btnOpen = findViewById(R.id.buttonOpen);
        btnClose = findViewById(R.id.buttonClose);
        btnSin = findViewById(R.id.buttonSin);
        btnCos = findViewById(R.id.buttonCos);
        btnTan = findViewById(R.id.buttonTan);
        btnLog = findViewById(R.id.buttonLog);
        btnLn = findViewById(R.id.buttonLn);
        btnFact = findViewById(R.id.buttonFactorial);
        btnSquare = findViewById(R.id.buttonSquare);
        btnSqrt = findViewById(R.id.buttonSqrt);
        btnIn = findViewById(R.id.buttonMod);
        b0 = findViewById(R.id.button0);
        b9 = findViewById(R.id.button9);
        b8 = findViewById(R.id.button8);
        b7 = findViewById(R.id.button7);
        b6 = findViewById(R.id.button6);
        b5 = findViewById(R.id.button5);
        b4 = findViewById(R.id.button4);
        b3 = findViewById(R.id.button3);
        b2 = findViewById(R.id.button2);
        b1 = findViewById(R.id.button1);
        btnPi = findViewById(R.id.buttonPi);
        btnMul = findViewById(R.id.buttonMultiply);
        btnMinus = findViewById(R.id.buttonMinus);
        btnPlus = findViewById(R.id.buttonPlus);
        btnEqual = findViewById(R.id.buttonEqual);
        btnDot = findViewById(R.id.buttonDot);
        btnDiv = findViewById(R.id.buttonDiv);
        b1.setOnClickListener(v -> toScr("1"));
        b2.setOnClickListener(v -> toScr("2"));
        b3.setOnClickListener(v -> toScr("3"));
        b4.setOnClickListener(v -> toScr("4"));
        b5.setOnClickListener(v -> toScr("5"));
        b6.setOnClickListener(v -> toScr("6"));
        b7.setOnClickListener(v -> toScr("7"));
        b8.setOnClickListener(v -> toScr("8"));
        b9.setOnClickListener(v -> toScr("9"));
        b0.setOnClickListener(v -> toScr("0"));
        btnDot.setOnClickListener(v -> toScr("."));
        btnPlus.setOnClickListener(v -> toScr("+"));
        btnDiv.setOnClickListener(v -> toScr("/"));
        btnOpen.setOnClickListener(v -> toScr("("));
        btnClose.setOnClickListener(v -> toScr(")"));
        btnPi.setOnClickListener(v -> {
            toScr("3.14159");
            ndScreen.setText(btnPi.getText().toString());
        });
        btnSin.setOnClickListener(v -> toScr("sin"));
        btnCos.setOnClickListener(v -> toScr("cos"));
        btnTan.setOnClickListener(v -> toScr("tan"));
        btnIn.setOnClickListener(v -> toScr("^(-1)"));
        btnLn.setOnClickListener(v -> toScr("ln"));
        btnLog.setOnClickListener(v -> toScr("log"));
        btnMinus.setOnClickListener(v -> {
            String str = stScreen.getText().toString();
            if (!str.endsWith("-")) {
                toScr("-");
            }
        });
        btnMul.setOnClickListener(v -> {
            String str = stScreen.getText().toString();
            if (!str.endsWith("*")) {
                toScr("*");
            }
        });
        btnSqrt.setOnClickListener(v -> {
            if (stScreen.getText().toString().isEmpty()) {
                showToast("Không có chữ số trên màn hình");
            } else {
                try {
                    String str = stScreen.getText().toString();
                    double r = Math.sqrt(Double.parseDouble(str));
                    stScreen.setText(String.valueOf(r));
                } catch (NumberFormatException e) {
                    showToast("Đầu vào không hợp lệ");
                }
            }
        });
        btnEqual.setOnClickListener(v -> {
            String str = stScreen.getText().toString();
            try {
                double result = evaluate(str);
                stScreen.setText(String.valueOf(result));
                ndScreen.setText(str);
            } catch (Exception e) {
                showToast("Biểu thức không hợp lệ");
                stScreen.setText("");
            }
        });
        btnAC.setOnClickListener(v -> {
            stScreen.setText("");
            ndScreen.setText("");
        });
        btnC.setOnClickListener(v -> {
            String str = stScreen.getText().toString();
            if (!str.isEmpty()) {
                str = str.substring(0, str.length() - 1);
                stScreen.setText(str);
            }
        });
        btnSquare.setOnClickListener(v -> {
            if (stScreen.getText().toString().isEmpty()) {
                showToast("Vui lòng nhập một số hợp lệ.");
            } else {
                try {
                    double d = Double.parseDouble(stScreen.getText().toString());
                    double square = d * d;
                    stScreen.setText(String.valueOf(square));
                    ndScreen.setText(d + "²");
                } catch (NumberFormatException e) {
                    showToast("Đầu vào không hợp lệ");
                }
            }
        });
        btnFact.setOnClickListener(v -> {
            if (stScreen.getText().toString().isEmpty()) {
                showToast("Vui lòng nhập một số hợp lệ.");
            } else {
                try {
                    int value = Integer.parseInt(stScreen.getText().toString());
                    int fact = factorial(value);
                    stScreen.setText(String.valueOf(fact));
                    ndScreen.setText(value + "!");
                } catch (NumberFormatException e) {
                    showToast("Đầu vào không hợp lệ");
                } catch (IllegalArgumentException e) {
                    showToast(e.getMessage());
                }
            }
        });
    }

    private void toScr(String str) {
        stScreen.setText(stScreen.getText().toString() + str);
    }

    private void showToast(String message) {
        Toast.makeText(Calculator.this, message, Toast.LENGTH_SHORT).show();
    }

    private int factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Số không được âm");
        }
        return (n == 1 || n == 0) ? 1 : n * factorial(n - 1);
    }

    private double evaluate(String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Không mong đợi: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Thiếu dấu ngoặc đóng");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else if (func.equals("ln")) x = Math.log(x);
                    else if (func.equals("log")) x = Math.log10(x);
                    else throw new RuntimeException("Không rõ " + func);
                } else {
                    throw new RuntimeException("Không có kết quả: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }
}
