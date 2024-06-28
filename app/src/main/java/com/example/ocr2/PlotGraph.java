package com.example.ocr2;

import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PlotGraph extends AppCompatActivity {

    private LinearLayout inputBox;
    private GraphView graphView;
    private int equationType;
    private TextView equationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        inputBox = findViewById(R.id.Input);
        graphView = findViewById(R.id.graphView);
        equationLabel = findViewById(R.id.Rec);
        Button btnPlot = findViewById(R.id.buttonPlot);

        equationType = getIntent().getIntExtra("equationType", equationType);
        setEquationLabel(equationType);
        inputFields(equationType);

        btnPlot.setOnClickListener(v -> plotGraph());
    }

    private void setEquationLabel(int type) {
        String label = "";
        switch (type) {
            case 1:
                label = "Phương trình bậc 1: ax + b = 0";
                break;
            case 2:
                label = "Phương trình bậc 2: ax<sup><small>2</small></sup> + bx + c = 0";
                break;
            case 3:
                label = "Phương trình bậc 3: ax<sup><small>3</small></sup> + bx<sup><small>2</small></sup> + cx + d = 0";
                break;
            case 4:
                label = "Phương trình bậc 4: ax<sup><small>4</small></sup> + bx<sup><small>3</small></sup> + cx<sup><small>2</small></sup> + dx + e = 0";
                break;
            case 5:
                label = "Đồ thị hàm số: y = sin(x)";
                break;
            case 6:
                label = "Đồ thị hàm số: y = cos(x)";
                break;
            case 7:
                label = "Đồ thị hàm số: y = log(x)";
                break;
        }
        equationLabel.setText(Html.fromHtml(label));
    }

    private void inputFields(int type) {
        inputBox.removeAllViews();
        if (type >= 1 && type <= 4) {
            for (char coeff = 'a'; coeff <= 'a' + type; coeff++) {
                addInput("Hệ số " + coeff, "Nhập hệ số " + coeff);
            }
        } else if (type == 7) {
            addInput("Nhập giá trị x", "Nhập x");
        }
    }


    private void addInput(String labelText, String hint) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 16, 0, 16);

        TextView label = new TextView(this);
        label.setText(labelText + ": ");
        label.setTextSize(16);

        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        layout.addView(label);
        layout.addView(editText);

        inputBox.addView(layout);
    }


    private void plotGraph() {
        double[] coefficients = new double[equationType + 1];
        double xStart = -20;
        double xEnd = 20;

        if (equationType >= 1 && equationType <= 4) {
            for (int i = 0; i < inputBox.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) inputBox.getChildAt(i);
                EditText editText = (EditText) layout.getChildAt(1);
                String input = editText.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ hệ số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    coefficients[i] = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Vui lòng nhập giá trị số hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if (equationType == 5 || equationType == 6 || equationType == 7) {
            for (int i = 0; i < inputBox.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) inputBox.getChildAt(i);
                EditText editText = (EditText) layout.getChildAt(1);
                String input = editText.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ giá trị x", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (i == 0) {
                        xStart = Double.parseDouble(input);
                    } else {
                        xEnd = Double.parseDouble(input);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Vui lòng nhập giá trị số hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (double x = xStart; x <= xEnd; x += 0.1) {
            double y = calculateY(equationType, coefficients, x);
            if (equationType == 7 && x <= 0) {
                continue;
            }
            series.appendData(new DataPoint(x, y), true, 400);
        }
        graphView.addSeries(series);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(xStart);
        graphView.getViewport().setMaxX(xEnd);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-10);
        graphView.getViewport().setMaxY(10);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("X");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Y");
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter());

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        graphView.getGridLabelRenderer().setHorizontalAxisTitle("X");
        graphView.getGridLabelRenderer().setNumHorizontalLabels(21);

        graphView.getGridLabelRenderer().setVerticalAxisTitle("Y");
        graphView.getGridLabelRenderer().setNumVerticalLabels(21);
    }



    private double calculateY(int type, double[] coefficients, double x) {
        double y = 0;
        switch (type) {
            case 1:
                y = coefficients[0] * x + coefficients[1];
                break;
            case 2:
                y = coefficients[0] * x * x + coefficients[1] * x + coefficients[2];
                break;
            case 3:
                y = coefficients[0] * x * x * x + coefficients[1] * x * x + coefficients[2] * x + coefficients[3];
                break;
            case 4:
                y = coefficients[0] * x * x * x * x + coefficients[1] * x * x * x + coefficients[2] * x * x + coefficients[3] * x + coefficients[4];
                break;
            case 5:
                y = Math.sin(Math.toRadians(x));
                break;
            case 6:
                y = Math.cos(Math.toRadians(x));
                break;
            case 7:
                y = Math.log(x);
                break;
        }
        return y;
    }

}
