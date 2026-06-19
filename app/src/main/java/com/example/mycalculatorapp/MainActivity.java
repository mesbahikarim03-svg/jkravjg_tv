package com.example.mycalculatorapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private StringBuilder currentInput = new StringBuilder(); // لتخزين المدخلات الحالية

    // متغير لتخزين إذا كانت النتيجة الأخيرة قد تم حسابها، وذلك لبدء إدخال جديد
    private boolean isResultCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        // تهيئة الشاشة بالقيمة الافتراضية "0"
        resultTextView.setText("0");

        // الحصول على مرجع لـ GridLayout الذي يحتوي على الأزرار
        GridLayout buttonsLayout = findViewById(R.id.buttonsGridLayout);

        // تكرار على جميع عناصر GridLayout لتعيين OnClickListener
        for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
            View child = buttonsLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnClickListener(this::onButtonClick);
            }
        }
    }

    private void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        String buttonTag = (String) button.getTag();

        if (buttonTag != null) {
            switch (buttonTag) {
                case "number":
                    appendNumber(buttonText);
                    break;
                case "operator":
                    appendOperator(buttonText);
                    break;
                case "decimal":
                    appendDecimal();
                    break;
                case "clear":
                    clearInput();
                    break;
                case "delete":
                    deleteLastChar();
                    break;
                case "equals":
                    calculateResult();
                    break;
                default:
                    // لا شيء
                    break;
            }
        }
        // تحديث TextView بالمدخلات الحالية
        updateResultTextView();
    }

    private void appendNumber(String number) {
        if (isResultCalculated) {
            // إذا كانت نتيجة قد تم حسابها، ابدأ إدخال جديد
            currentInput.setLength(0); // مسح المدخلات السابقة
            isResultCalculated = false;
        }

        // منع الأصفار الزائدة في البداية إلا إذا كانت النقطة العشرية موجودة
        if (currentInput.toString().equals("0") && !number.equals(".")) {
             currentInput.setLength(0); // مسح الصفر الأولي إذا لم يكن جزءًا من رقم عشري
        }
        currentInput.append(number);
    }

    private void appendOperator(String operator) {
        // إذا كان هناك رقم قد تم حسابه مسبقاً، نستخدمه كبداية للتعبير الجديد
        if (isResultCalculated && !currentInput.toString().equals("Error")) {
            // إذا كانت النتيجة "Error"، لا يمكننا بناء تعبير عليها، في هذه الحالة يجب مسح كل شيء
            // أو التعامل معها بشكل خاص، لكن حالياً سنفترض نتيجة صالحة
            isResultCalculated = false; // نبدأ عملية إدخال جديدة
        }

        if (currentInput.length() == 0 && !operator.equals("-")) {
            // لا تسمح ببدء التعبير بعامل تشغيل إلا إذا كان سالبًا
            Toast.makeText(this, "Cannot start with an operator!", Toast.LENGTH_SHORT).show();
            return;
        }

        // منع إدخال عاملين تشغيل متتاليين أو استبدال عامل تشغيل بآخر
        if (currentInput.length() > 0) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);
            if (isOperator(lastChar)) {
                // استبدال عامل التشغيل الأخير إذا كان الجديد ليس "- " و القديم ليس " - "
                // تسمح بإدخال "5 * - 2" ولكن لا تسمح ب "5 * / 2"
                if (!(operator.equals("-") && lastChar == operator.charAt(0)) && !(operator.equals("-") && lastChar != '-')) { // allow "5 - -" to be "5 --"
                    if (currentInput.length() > 1 && lastChar == operator.charAt(0) && currentInput.charAt(currentInput.length()-2) == '-') {
                        // this handles "5 - -" to become "5 - (-)" if we want, for now, just replace
                    } else {
                        currentInput.deleteCharAt(currentInput.length() - 1);
                    }
                }
            }
        }
        currentInput.append(operator);
    }

    private void appendDecimal() {
        if (isResultCalculated) {
            currentInput.setLength(0);
            currentInput.append("0"); // ابدأ بـ "0." إذا كانت نتيجة قد تم حسابها
            isResultCalculated = false;
        }

        // منع إدخال أكثر من نقطة عشرية في الرقم الحالي
        // هذا يتطلب منطق أكثر تعقيداً إذا كان التعبير يحتوي على أكثر من رقم
        // حالياً، سنمنع إدخال نقطة إذا كان الرقم الحالي يحتوي عليها
        // (افتراضًا أن currentInput يمثل الرقم الأخير أو التعبير بأكمله)
        String currentExpression = currentInput.toString();
        // تقسيم التعبير إلى أجزاء (أرقام وعوامل) ومراجعة الجزء الأخير
        // هذا المنطق سيصبح أسهل مع الـ tokenization والـ parsing
        // لكن حاليا، سنقوم بتحقق بسيط
        int lastOperatorIndex = findLastOperatorIndex(currentExpression);
        String lastNumber = (lastOperatorIndex == -1) ? currentExpression : currentExpression.substring(lastOperatorIndex + 1);

        if (!lastNumber.contains(".")) {
            if (currentExpression.isEmpty() || isOperator(currentExpression.charAt(currentExpression.length() - 1))) {
                currentInput.append("0"); // إذا كان التعبير فارغًا أو ينتهي بعامل، أضف "0."
            }
            currentInput.append(".");
        } else {
            Toast.makeText(this, "Already has a decimal point!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInput() {
        currentInput.setLength(0);
        resultTextView.setText("0"); // إعادة تعيين الشاشة إلى 0
        isResultCalculated = false;
    }

    private void deleteLastChar() {
        if (currentInput.length() > 0 && !isResultCalculated) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            if (currentInput.length() == 0) {
                resultTextView.setText("0"); // إذا أصبح فارغًا، أظهر 0
            }
        } else if (isResultCalculated) {
            // إذا كانت نتيجة قد تم عرضها، مسح كل شيء عند الضغط على DEL
            clearInput();
        }
    }

    private void calculateResult() {
        // هذا هو المكان الذي سنقوم فيه باستدعاء محرك الحسابات
        // في الوقت الحالي، سنضع منطقًا مبسطًا جدًا أو رسالة توست
        if (currentInput.length() == 0) {
            Toast.makeText(this, "No input to calculate!", Toast.LENGTH_SHORT).show();
            return;
        }

        String expression = currentInput.toString();
        try {
            // هنا سيتم استدعاء CalculatorEngine.evaluate(expression)
            // مؤقتاً:
            double result = evaluateExpression(expression); // دالة وهمية مؤقتة
            currentInput.setLength(0);
            // لتجنب الأصفار الزائدة بعد النقطة العشرية إذا كان الرقم صحيحاً
            if (result == (long) result) {
                currentInput.append((long) result);
            } else {
                currentInput.append(result);
            }
            isResultCalculated = true;
        } catch (Exception e) {
            currentInput.setLength(0);
            currentInput.append("Error");
            isResultCalculated = true;
            Toast.makeText(this, "Invalid Expression: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateResultTextView() {
        if (currentInput.length() == 0) {
            resultTextView.setText("0");
        } else {
            resultTextView.setText(currentInput.toString());
        }
    }

    // ------- دوال مساعدة لمنطق العمليات الأساسية -------

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private int findLastOperatorIndex(String expression) {
        for (int i = expression.length() - 1; i >= 0; i--) {
            if (isOperator(expression.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    // دالة وهمية للحسابات، سيتم استبدالها لاحقاً بـ CalculatorEngine
    private double evaluateExpression(String expression) {
        // هذا مجرد مثال بسيط جداً، لا يدعم الأولويات أو العمليات المعقدة
        // سيتم استبداله بمحرك حاسبة حقيقي
        try {
            // محاولة بسيطة لتقييم تعبير مثل "5+2"
            // هذا المنطق ليس قوياً ولا يتعامل مع ترتيب العمليات (PEMDAS/BODMAS)
            // فقط للبدء
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                // معالجة حالة الأرقام السالبة في البداية
                if (parts.length == 2 && expression.startsWith("-")) {
                    return -Double.parseDouble(parts[1]);
                } else if (parts.length == 2) {
                    return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
                } else if (parts.length > 2) { // for expressions like "5-2-1"
                    double result = Double.parseDouble(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        result -= Double.parseDouble(parts[i]);
                    }
                    return result;
                }
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                double divisor = Double.parseDouble(parts[1]);
                if (divisor == 0) {
                    throw new ArithmeticException("Division by zero!");
                }
                return Double.parseDouble(parts[0]) / divisor;
            } else if (expression.contains("%")) {
                String[] parts = expression.split("%");
                return Double.parseDouble(parts[0]) / 100.0; // simple percentage for first number
            } else {
                return Double.parseDouble(expression);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
        } catch (ArithmeticException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression: " + e.getMessage());
        }
        return 0; // افتراضي
    }
}