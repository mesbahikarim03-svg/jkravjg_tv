package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private TextView historyTextView; // لعرض العمليات السابقة

    private String currentInput = ""; // لتخزين المدخلات الرقمية الحالية
    private String currentOperator = ""; // لتخزين العملية الحالية (+, -, *, /)
    private double firstOperand = 0; // لتخزين الرقم الأول للعملية
    private boolean operatorPressed = false; // لتتبع ما إذا تم الضغط على عامل تشغيل
    private boolean equalsPressed = false; // لتتبع ما إذا تم الضغط على "="

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        historyTextView = findViewById(R.id.historyTextView); // ربط الـ TextView الجديد

        // ربط جميع الأزرار وتعيين OnClickListener لها
        int[] numberButtonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                                 R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                                 R.id.button8, R.id.button9};
        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        int[] operatorButtonIds = {R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
                                   R.id.buttonDivide, R.id.buttonPercentage};
        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(this::onOperatorClick);
        }

        findViewById(R.id.buttonDecimal).setOnClickListener(this::onDecimalClick);
        findViewById(R.id.buttonEquals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.buttonClear).setOnClickListener(this::onClearClick);
        findViewById(R.id.buttonDelete).setOnClickListener(this::onDeleteClick);

        // تهيئة الشاشات
        resultTextView.setText("0");
        historyTextView.setText("");
    }

    // معالج النقر للأرقام
    private void onNumberClick(View view) {
        Button button = (Button) view;
        String number = button.getText().toString();

        if (equalsPressed) { // إذا تم الضغط على "="، ابدأ عملية حسابية جديدة
            currentInput = "";
            currentOperator = "";
            firstOperand = 0;
            historyTextView.setText("");
            equalsPressed = false;
        }

        if (operatorPressed) { // إذا كان قد تم الضغط على عملية، ابدأ رقمًا جديدًا
            currentInput = "";
            operatorPressed = false;
        }

        // منع إدخال أصفار غير ضرورية في البداية (مثل 007)
        if (currentInput.equals("0") && !number.equals(".")) {
            currentInput = number;
        } else {
            currentInput += number;
        }

        resultTextView.setText(currentInput);
    }

    // معالج النقر للنقطة العشرية
    private void onDecimalClick(View view) {
        if (equalsPressed) { // إذا تم الضغط على "="، ابدأ عملية حسابية جديدة
            currentInput = "";
            currentOperator = "";
            firstOperand = 0;
            historyTextView.setText("");
            equalsPressed = false;
        }

        if (operatorPressed) { // إذا كان قد تم الضغط على عملية، ابدأ رقمًا جديدًا
            currentInput = "0"; // البدء بـ 0.5 بدلاً من .5
            operatorPressed = false;
        }

        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) { // إذا كانت الشاشة فارغة وبدأت بنقطة
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

        if (currentInput.isEmpty() && currentOperator.isEmpty()) {
            // لا يوجد رقم قبل العملية، لا تفعل شيئا أو ابدأ بـ 0
            // يمكن تعيين firstOperand لـ 0 أو تركها كما هي
            return;
        }

        if (equalsPressed) { // إذا تم الضغط على "=" مسبقًا، فاستخدم النتيجة كأول عامل تشغيل
            firstOperand = Double.parseDouble(resultTextView.getText().toString());
            currentInput = ""; // مسح المدخلات استعداداً للرقم الثاني
            equalsPressed = false;
        }


        if (!currentInput.isEmpty()) { // إذا كان هناك رقم حالي
            if (!currentOperator.isEmpty() && !operatorPressed) { // إذا كانت هناك عملية سابقة ورقم ثاني
                calculateResult(); // احسب النتيجة أولاً
                firstOperand = Double.parseDouble(resultTextView.getText().toString());
            } else if (currentOperator.isEmpty()) { // أول عملية
                firstOperand = Double.parseDouble(currentInput);
            }
        } else if (!currentOperator.isEmpty() && operatorPressed) {
            // إذا كان المستخدم يغير العملية (مثال: 5+ ثم يضغط -)
            currentOperator = newOperator;
            String currentHistory = historyTextView.getText().toString();
            if (!currentHistory.isEmpty()) {
                historyTextView.setText(currentHistory.substring(0, currentHistory.length() - 1) + newOperator);
            }
            return; // لا تكمل، فقط غير العملية
        }


        currentOperator = newOperator;
        operatorPressed = true;
        historyTextView.setText(formatResult(firstOperand) + " " + currentOperator);
    }

    // معالج النقر للمساواة
    private void onEqualsClick(View view) {
        if (!currentInput.isEmpty() && !currentOperator.isEmpty() && !equalsPressed) {
            // تأكد من تحديث historyTextView بالرقم الثاني
            historyTextView.append(" " + currentInput);
            calculateResult();
            currentOperator = ""; // مسح العملية بعد الحساب
            equalsPressed = true;
            operatorPressed = false; // إعادة تعيين حالة عامل التشغيل
        } else if (currentOperator.isEmpty() && !currentInput.isEmpty() && equalsPressed) {
            // إذا ضغط المستخدم "=" مرة أخرى بعد نتيجة، فما زلنا نعرض النتيجة فقط.
            // لا تفعل شيئا سوى التأكد من عرض النتيجة بشكل صحيح
            resultTextView.setText(formatResult(Double.parseDouble(currentInput)));
        } else if (!currentOperator.isEmpty() && currentInput.isEmpty() && !equalsPressed) {
            // الحالة: 5 + = (يعني 5 + 5 = 10)
            currentInput = String.valueOf(firstOperand);
            historyTextView.append(" " + currentInput);
            calculateResult();
            currentOperator = "";
            equalsPressed = true;
            operatorPressed = false;
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
    }

    // معالج النقر للحذف
    private void onDeleteClick(View view) {
        if (equalsPressed) { // لا يمكن الحذف بعد "="، يجب مسح الكل أو بدء جديد
            onClearClick(view);
            return;
        }
        if (!currentInput.isEmpty() && !operatorPressed) { // الحذف من الرقم الحالي
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.isEmpty() || currentInput.equals("-")) { // إذا أصبح فارغًا أو فقط علامة سالبة
                resultTextView.setText("0");
                currentInput = ""; // تأكد من أن المدخلات فارغة لتبدأ من جديد
            } else {
                resultTextView.setText(currentInput);
            }
        } else if (!currentOperator.isEmpty() && operatorPressed && currentInput.isEmpty()) {
            // إذا كان المستخدم يريد حذف العملية (مثال: 5 + ثم يضغط DEL)
            // هذا السيناريو يتطلب تفكيراً أعمق لمنع تعقيدات في المنطق.
            // في الوقت الحالي، سنقوم فقط بمسح العملية الحالية ونعود للرقم الأول.
            currentOperator = "";
            operatorPressed = false;
            resultTextView.setText(formatResult(firstOperand));
            historyTextView.setText("");
            currentInput = String.valueOf(firstOperand); // إعادة الرقم الأول للمدخلات الحالية
            firstOperand = 0; // مسح الرقم الأول
        }
    }

    // دالة لحساب النتيجة
    private void calculateResult() {
        if (currentInput.isEmpty() || currentOperator.isEmpty()) {
            // لا يمكن الحساب بدون رقمين وعملية
            return;
        }

        double secondOperand;
        try {
            secondOperand = Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            resultTextView.setText("Error");
            onClearClick(null); // مسح كل شيء عند الخطأ
            return;
        }

        double result = firstOperand; // ابدأ بالرقم الأول كقيمة مبدئية

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
                    onClearClick(null); // مسح كل شيء عند الخطأ
                    return;
                }
                break;
            case "%":
                // معالجة النسبة المئوية: مثال: 100 * 5% = 5، 50 + 10% = 55
                // نعتبرها كعملية "a % b" حيث b هي نسبة مئوية من a
                // إذا كانت العملية الأخيرة هي ضرب أو قسمة: 100 * 5% = 100 * (5/100)
                // إذا كانت العملية الأخيرة هي جمع أو طرح: 50 + 10% (من 50) = 50 + (10/100 * 50)
                if (currentOperator.equals("*") || currentOperator.equals("/")) {
                    secondOperand = secondOperand / 100.0;
                } else { // للجمع والطرح
                    secondOperand = (firstOperand * secondOperand) / 100.0;
                }
                // بعد حساب القيمة المئوية، يجب إعادة تطبيق العملية الأصلية
                // هذه الطريقة لا تدعم التسلسل (مثلاً 50 + 10% ثم * 2)
                // الأبسط هو جعل % تعمل كـ 'a mod b' أو 'a / 100'
                // لتطبيق النسبة المئوية كـ "النسبة من الرقم الأول":
                // 50 + 10% (من 50) = 50 + (50 * 0.1) = 55
                // 100 * 5% (من 100) = 100 * (100 * 0.05) = 500
                // الطريقة الأكثر شيوعاً للحاسبات: إذا كان % بعد رقم، يصبح الرقم / 100.
                // 100 * 5% -> 100 * (5/100)
                // 50 + 10% -> 50 + (10/100 * previous result or first operand)
                // سأبسطها للتعامل مع "50%" يعني 0.50
                // أو إذا كانت بعد عملية، فتطبق النسبة على الرقم الأول أو النتيجة السابقة
                // مثال: 50 + 10%
                // 10% من 50 = (10/100) * 50 = 5
                // 50 + 5 = 55
                // سأعيد ضبط المنطق ليتناسب مع هذا السلوك الشائع للحاسبات.
                if (currentOperator.equals("%")) { // إذا كانت العملية هي % نفسها
                    secondOperand = firstOperand * (secondOperand / 100.0);
                    result = secondOperand; // النتيجة هي قيمة النسبة المئوية
                } else { // إذا كانت % تستخدم كتحويل لـ secondOperand
                    result = firstOperand; // أبقي على firstOperand ونقوم بالعملية عليه
                    double percentageValue = (firstOperand * secondOperand) / 100.0;
                    switch (currentOperator) {
                        case "+":
                            result = firstOperand + percentageValue;
                            break;
                        case "-":
                            result = firstOperand - percentageValue;
                            break;
                        case "*":
                            result = firstOperand * (secondOperand / 100.0); // 100 * 5% -> 100 * 0.05
                            break;
                        case "/":
                            if (secondOperand != 0) {
                                result = firstOperand / (secondOperand / 100.0); // 100 / 5% -> 100 / 0.05
                            } else {
                                resultTextView.setText("Error");
                                onClearClick(null);
                                return;
                            }
                            break;
                    }
                }
                break;
        }

        currentInput = String.valueOf(result); // النتيجة تصبح المدخلات الحالية لعمليات لاحقة
        resultTextView.setText(formatResult(result));
        firstOperand = result; // تحديث الرقم الأول للعمليات المتسلسلة
        currentOperator = ""; // مسح العملية بعد الحساب
    }

    // دالة لتنسيق النتيجة (إزالة .0 إذا كانت النتيجة عددًا صحيحًا، والحد من الأرقام العشرية)
    private String formatResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return "Error";
        }
        String s = String.valueOf(result);
        if (s.endsWith(".0")) {
            return s.substring(0, s.length() - 2);
        }
        // يمكن إضافة تنسيق لأرقام عشرية طويلة هنا إذا لزم الأمر
        return s;
    }
}