package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private String currentInput = ""; // لتخزين المدخلات الحالية
    private String currentOperator = ""; // لتخزين العملية الحالية
    private double firstOperand = 0; // لتخزين الرقم الأول للعملية

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        // ربط جميع الأزرار وتعيين OnClickListener لها
        // الأرقام
        findViewById(R.id.button0).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button1).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button2).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button3).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button4).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button5).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button6).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button7).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button8).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button9).setOnClickListener(this::onNumberClick);

        // العمليات
        findViewById(R.id.buttonAdd).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonSubtract).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonMultiply).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonDivide).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonPercentage).setOnClickListener(this::onOperatorClick); // قد نحتاج لمعالجة خاصة لها

        // الأزرار الخاصة
        findViewById(R.id.buttonDecimal).setOnClickListener(this::onDecimalClick);
        findViewById(R.id.buttonEquals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.buttonClear).setOnClickListener(this::onClearClick);
        findViewById(R.id.buttonDelete).setOnClickListener(this::onDeleteClick);
    }

    // معالج النقر للأرقام
    private void onNumberClick(View view) {
        Button button = (Button) view;
        String number = button.getText().toString();

        if (currentInput.equals("0") && !number.equals(".")) { // لمنع 0000 أو 0123
            currentInput = number;
        } else {
            currentInput += number;
        }
        resultTextView.setText(currentInput);
    }

    // معالج النقر للنقطة العشرية
    private void onDecimalClick(View view) {
        if (!currentInput.contains(".")) {
            currentInput += ".";
            resultTextView.setText(currentInput);
        }
    }

    // معالج النقر للعمليات
    private void onOperatorClick(View view) {
        Button button = (Button) view;
        String operator = button.getText().toString();

        if (!currentInput.isEmpty()) {
            if (!currentOperator.isEmpty()) { // إذا كان هناك عملية سابقة، قم بحسابها أولاً
                calculateResult();
            }
            firstOperand = Double.parseDouble(currentInput);
            currentOperator = operator;
            currentInput = ""; // مسح المدخلات الحالية استعداداً للرقم الثاني
        }
    }

    // معالج النقر للمساواة
    private void onEqualsClick(View view) {
        if (!currentInput.isEmpty() && !currentOperator.isEmpty()) {
            calculateResult();
            currentOperator = ""; // مسح العملية بعد الحساب
        }
    }

    // معالج النقر للمسح الكلي
    private void onClearClick(View view) {
        currentInput = "";
        currentOperator = "";
        firstOperand = 0;
        resultTextView.setText("0");
    }

    // معالج النقر للحذف
    private void onDeleteClick(View view) {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.isEmpty()) {
                resultTextView.setText("0");
            } else {
                resultTextView.setText(currentInput);
            }
        } else {
            resultTextView.setText("0"); // إذا كانت المدخلات فارغة، اعرض 0
        }
    }

    // دالة لحساب النتيجة
    private void calculateResult() {
        if (currentInput.isEmpty()) return;

        double secondOperand = Double.parseDouble(currentInput);
        double result = 0;

        switch (currentOperator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand != 0) {
                    result = firstOperand / secondOperand;
                } else {
                    resultTextView.setText("Error");
                    currentInput = "";
                    currentOperator = "";
                    firstOperand = 0;
                    return;
                }
                break;
            case "%":
                result = firstOperand % secondOperand;
                break;
        }

        currentInput = String.valueOf(result);
        resultTextView.setText(formatResult(result)); // تنسيق النتيجة
    }

    // دالة لتنسيق النتيجة (إزالة .0 إذا كانت النتيجة عددًا صحيحًا)
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.valueOf(result);
        }
    }
}