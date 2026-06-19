package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import androidx.core.content.ContextCompat; // For getting colors from resources
import android.content.res.ColorStateList; // For setting tint programmatically

import java.text.DecimalFormat; // For precise number formatting

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private TextView historyTextView;

    private String currentInput = ""; // لتخزين المدخلات الرقمية الحالية
    private String currentOperator = ""; // لتخزين العملية الحالية (+, -, *, /)
    private double firstOperand = 0; // لتخزين الرقم الأول للعملية
    private boolean operatorPressed = false; // لتتبع ما إذا تم الضغط على عامل تشغيل
    private boolean equalsPressed = false; // لتتبع ما إذا تم الضغط على "="

    // New: For operator highlighting
    private Button activeOperatorButton = null;
    private int defaultOperatorButtonColorResId;
    private int highlightedOperatorButtonColorResId;

    // Constants for state saving
    private static final String KEY_CURRENT_INPUT = "current_input";
    private static final String KEY_CURRENT_OPERATOR = "current_operator";
    private static final String KEY_FIRST_OPERAND = "first_operand";
    private static final String KEY_OPERATOR_PRESSED = "operator_pressed";
    private static final String KEY_EQUALS_PRESSED = "equals_pressed";
    private static final String KEY_HISTORY_TEXT = "history_text";

    // Max digits to display/input
    private static final int MAX_DIGITS = 12; // Example limit for integer/total digits

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        historyTextView = findViewById(R.id.historyTextView);

        // Initialize colors from resources
        defaultOperatorButtonColorResId = R.color.calc_operator_button_default;
        highlightedOperatorButtonColorResId = R.color.calc_operator_button_highlight;

        // ربط جميع الأزرار وتعيين OnClickListener لها
        int[] numberButtonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                                 R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                                 R.id.button8, R.id.button9};
        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        int[] operatorButtonIds = {R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
                                   R.id.buttonDivide, R.id.buttonPercentage}; // Percentage is handled differently
        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(this::onOperatorClick);
        }

        findViewById(R.id.buttonDecimal).setOnClickListener(this::onDecimalClick);
        findViewById(R.id.buttonEquals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.buttonClear).setOnClickListener(this::onClearClick);
        findViewById(R.id.buttonDelete).setOnClickListener(this::onDeleteClick);

        if (savedInstanceState != null) {
            currentInput = savedInstanceState.getString(KEY_CURRENT_INPUT, "");
            currentOperator = savedInstanceState.getString(KEY_CURRENT_OPERATOR, "");
            firstOperand = savedInstanceState.getDouble(KEY_FIRST_OPERAND, 0);
            operatorPressed = savedInstanceState.getBoolean(KEY_OPERATOR_PRESSED, false);
            equalsPressed = savedInstanceState.getBoolean(KEY_EQUALS_PRESSED, false);
            historyTextView.setText(savedInstanceState.getString(KEY_HISTORY_TEXT, ""));

            resultTextView.setText(currentInput.isEmpty() ? "0" : currentInput);

            // Re-highlight operator if applicable
            if (!currentOperator.isEmpty() && operatorPressed) {
                Button opButton = getButtonForOperator(currentOperator);
                if (opButton != null) {
                    highlightOperator(opButton);
                }
            }
        } else {
            resultTextView.setText("0");
            historyTextView.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_INPUT, currentInput);
        outState.putString(KEY_CURRENT_OPERATOR, currentOperator);
        outState.putDouble(KEY_FIRST_OPERAND, firstOperand);
        outState.putBoolean(KEY_OPERATOR_PRESSED, operatorPressed);
        outState.putBoolean(KEY_EQUALS_PRESSED, equalsPressed);
        outState.putString(KEY_HISTORY_TEXT, historyTextView.getText().toString());
    }

    // Helper to find operator button by its symbol
    private Button getButtonForOperator(String operator) {
        switch (operator) {
            case "+": return findViewById(R.id.buttonAdd);
            case "-": return findViewById(R.id.buttonSubtract);
            case "*": return findViewById(R.id.buttonMultiply);
            case "/": return findViewById(R.id.buttonDivide);
            case "%": return findViewById(R.id.buttonPercentage);
            default: return null;
        }
    }

    // Helper to highlight a specific operator button
    private void highlightOperator(Button button) {
        clearActiveOperatorHighlight(); // Clear previous highlight
        if (button != null) {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, highlightedOperatorButtonColorResId)));
            activeOperatorButton = button;
        }
    }

    // Helper to clear the currently active operator highlight
    private void clearActiveOperatorHighlight() {
        if (activeOperatorButton != null) {
            activeOperatorButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, defaultOperatorButtonColorResId)));
            activeOperatorButton = null;
        }
    }

    // معالج النقر للأرقام
    private void onNumberClick(View view) {
        Button button = (Button) view;
        String number = button.getText().toString();

        clearActiveOperatorHighlight(); // Clear operator highlight when number is pressed

        if (equalsPressed) {
            onClearClick(null); // Clear all to start a new calculation
        }

        if (operatorPressed) { // If an operator was pressed, start a new number
            currentInput = "";
            operatorPressed = false;
        }

        // Input length limit (excluding decimal point from count)
        if (currentInput.replace(".", "").length() >= MAX_DIGITS && !currentInput.contains(".")) {
             // If MAX_DIGITS reached AND no decimal, don't add more digits
            return;
        }
        if (currentInput.replace(".", "").length() >= MAX_DIGITS + 1 && currentInput.contains(".")) {
            // If MAX_DIGITS reached for integer part + decimal already exists, don't add more
            // This logic is simple, can be improved for more complex decimal limits
            return;
        }


        // Prevent leading zeros unless it's "0."
        if (currentInput.equals("0") && !number.equals(".")) {
            currentInput = number;
        } else {
            currentInput += number;
        }
        resultTextView.setText(currentInput);
    }

    // معالج النقر للنقطة العشرية
    private void onDecimalClick(View view) {
        clearActiveOperatorHighlight(); // Clear operator highlight when decimal is pressed

        if (equalsPressed) {
            onClearClick(null);
            currentInput = "0"; // Start with 0.
        }

        if (operatorPressed) {
            currentInput = "0"; // Start with 0.
            operatorPressed = false;
        }

        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) { // If screen is empty and user types ".", make it "0."
                currentInput = "0.";
            } else {
                currentInput += ".";
            }
            resultTextView.setText(currentInput);
        }
    }

    // معالج النقر للعمليات (%, /, *, -, +)
    private void onOperatorClick(View view) {
        Button button = (Button) view;
        String newOperator = button.getText().toString();

        // Special handling for percentage (%)
        if (newOperator.equals("%")) {
            if (currentInput.isEmpty()) { // Cannot apply % to nothing
                return;
            }
            double value = Double.parseDouble(currentInput);
            double calculatedValue;

            if (!currentOperator.isEmpty()) { // If an operator is pending (e.g., 50 + 10%)
                // X OP Y %
                // For * or /: Y% means Y/100. So 50 * (10/100)
                // For + or -: Y% means Y% of X. So 50 + (10% of 50)
                if (currentOperator.equals("*") || currentOperator.equals("/")) {
                    calculatedValue = value / 100.0;
                } else { // for + or -
                    calculatedValue = (firstOperand * value) / 100.0;
                }
                currentInput = String.valueOf(calculatedValue);
                historyTextView.append(" " + formatResult(value) + "%");
                resultTextView.setText(formatResult(calculatedValue));
                // Do NOT change currentOperator, it remains the original (+,-,*,/)
                // User then presses '=' to finalize, or another operator.
            } else { // Just a number then % (e.g., "50%")
                calculatedValue = value / 100.0;
                currentInput = String.valueOf(calculatedValue);
                historyTextView.setText(formatResult(value) + "%");
                resultTextView.setText(formatResult(calculatedValue));
            }
            // After applying %, it's like a number has been entered/modified, not an operator awaiting another number
            operatorPressed = false;
            equalsPressed = false; // Reset equals state
            clearActiveOperatorHighlight(); // % doesn't stay highlighted like +,-,*,/
            return; // Exit onOperatorClick, since % is handled
        }

        // --- Normal operator logic starts here (for +, -, *, /) ---
        highlightOperator(button); // Highlight the pressed operator

        if (currentInput.isEmpty() && firstOperand == 0 && currentOperator.isEmpty()) {
            // If nothing entered, but an operator is pressed, assume 0 as first operand.
            firstOperand = 0;
            currentInput = "0"; // To ensure 0 is displayed and can be used as first operand
            historyTextView.setText("0 " + newOperator);
        } else if (currentInput.isEmpty() && !currentOperator.isEmpty()) {
            // If an operator is pressed but current input is empty (e.g., 5 + *), means changing operator
            currentOperator = newOperator;
            historyTextView.setText(formatResult(firstOperand) + " " + newOperator);
            return; // Just change operator, don't calculate yet
        } else if (!currentInput.isEmpty() && !currentOperator.isEmpty() && !operatorPressed) {
            // If previous operator and current input available (e.g., 5 + 3, then press *)
            calculateResult(); // Calculate the previous operation
            firstOperand = Double.parseDouble(resultTextView.getText().toString());
            historyTextView.setText(formatResult(firstOperand) + " " + newOperator);
        } else if (!currentInput.isEmpty() && currentOperator.isEmpty()) {
            // First operator in a new sequence (e.g., 5 then press +)
            firstOperand = Double.parseDouble(currentInput);
            historyTextView.setText(formatResult(firstOperand) + " " + newOperator);
        } else { // This else might catch cases like consecutive operator presses without number change
            // Just update the operator in history if currentInput is empty after previous operation
            historyTextView.setText(formatResult(firstOperand) + " " + newOperator);
        }

        currentOperator = newOperator;
        operatorPressed = true;
        equalsPressed = false; // Reset equals state
        currentInput = ""; // Clear currentInput for the next number
    }

    // معالج النقر للمساواة
    private void onEqualsClick(View view) {
        clearActiveOperatorHighlight(); // Clear highlight when equals is pressed

        if (equalsPressed) { // If equals pressed consecutively, just keep showing result
            return;
        }

        if (currentInput.isEmpty() && !currentOperator.isEmpty()) {
            // Case: 5 + = (means 5 + 5 = 10). Use firstOperand as second operand.
            currentInput = String.valueOf(firstOperand);
            historyTextView.append(" " + formatResult(firstOperand) + " =");
        } else if (currentInput.isEmpty() && currentOperator.isEmpty()) {
            // If nothing meaningful to calculate, just show current result/input
            historyTextView.setText(formatResult(Double.parseDouble(currentInput.isEmpty() ? "0" : currentInput)) + " =");
            equalsPressed = true;
            return;
        } else {
            historyTextView.append(" " + currentInput + " =");
        }

        if (!currentInput.isEmpty() && !currentOperator.isEmpty()) {
            calculateResult();
            currentOperator = ""; // Clear operator after calculation
            equalsPressed = true;
            operatorPressed = false;
        } else if (currentInput.isEmpty() && currentOperator.isEmpty()) {
            // If only one number was entered and then equals, just display it (it's already displayed)
            resultTextView.setText(formatResult(Double.parseDouble(currentInput.isEmpty() ? "0" : currentInput)));
            equalsPressed = true;
        }
    }

    // معالج النقر للمسح الكلي
    private void onClearClick(View view) {
        currentInput = "";
        currentOperator = "";
        firstOperand = 0;
        operatorPressed = false;
        equalsPressed = false;
        resultTextView.setText("0");
        historyTextView.setText("");
        clearActiveOperatorHighlight(); // Clear any active operator highlight
    }

    // معالج النقر للحذف
    private void onDeleteClick(View view) {
        if (equalsPressed) { // Cannot delete after equals, must clear all or start new
            onClearClick(null);
            return;
        }
        // If an operator was just pressed and no second number, delete the operator
        if (operatorPressed && currentInput.isEmpty() && !currentOperator.isEmpty()) {
            currentOperator = "";
            operatorPressed = false;
            historyTextView.setText("");
            resultTextView.setText(formatResult(firstOperand)); // Show first operand again
            currentInput = String.valueOf(firstOperand); // Make first operand editable again
            firstOperand = 0; // Reset first operand as it's now current input
            clearActiveOperatorHighlight();
            return;
        }

        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.isEmpty() || (currentInput.equals("-") && currentInput.length() == 1)) { // If it becomes empty or just "-"
                resultTextView.setText("0");
                currentInput = "";
            } else {
                resultTextView.setText(currentInput);
            }
        } else {
            resultTextView.setText("0");
        }
    }

    // دالة لحساب النتيجة
    private void calculateResult() {
        if (currentInput.isEmpty() || currentOperator.isEmpty()) {
            return;
        }

        double secondOperand;
        try {
            secondOperand = Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            resultTextView.setText("Error");
            onClearClick(null);
            return;
        }

        double result = firstOperand;

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
                    onClearClick(null);
                    return;
                }
                break;
            // The '%' case is handled in onOperatorClick before reaching here
            default:
                break;
        }

        currentInput = String.valueOf(result); // النتيجة تصبح المدخلات الحالية لعمليات لاحقة
        resultTextView.setText(formatResult(result));
        firstOperand = result; // تحديث الرقم الأول للعمليات المتسلسلة
        // currentOperator is cleared in onEqualsClick, not here directly
    }

    // دالة لتنسيق النتيجة (إزالة .0 إذا كانت النتيجة عددًا صحيحًا، والحد من الأرقام العشرية)
    private String formatResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return "Error";
        }

        // Use DecimalFormat to avoid scientific notation for large numbers and limit decimal places
        // It also handles removing trailing zeros and .0 for integers
        DecimalFormat df = new DecimalFormat("#.##########"); // Up to 10 decimal places, remove trailing zeros
        String formatted = df.format(result);

        // Basic check to see if number exceeds typical display length, if so, allow scientific notation
        if (formatted.length() > MAX_DIGITS + 2 && !formatted.contains("E")) { // +2 for potential decimal point and sign
            df = new DecimalFormat("0.######E0"); // scientific notation
            return df.format(result);
        }
        return formatted;
    }
}