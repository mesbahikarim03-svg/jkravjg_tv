package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView result;
    String currentInput = "";
    String operator = "";
    double firstNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
    }

    public void onNumberClick(View view) {
        Button button = (Button) view;
        currentInput += button.getText().toString();
        result.setText(currentInput);
    }

    public void onOperatorClick(View view) {
        Button button = (Button) view;
        firstNumber = Double.parseDouble(currentInput);
        operator = button.getText().toString();
        currentInput = "";
    }

    public void onEqualClick(View view) {
        double secondNumber = Double.parseDouble(currentInput);
        double finalResult = 0;

        switch (operator) {
            case "+":
                finalResult = firstNumber + secondNumber;
                break;
            case "-":
                finalResult = firstNumber - secondNumber;
                break;
            case "×":
                finalResult = firstNumber * secondNumber;
                break;
            case "÷":
                finalResult = firstNumber / secondNumber;
                break;
        }

        result.setText(String.valueOf(finalResult));
        currentInput = String.valueOf(finalResult);
    }

    public void onClearClick(View view) {
        currentInput = "";
        operator = "";
        firstNumber = 0;
        result.setText("0");
    }
}