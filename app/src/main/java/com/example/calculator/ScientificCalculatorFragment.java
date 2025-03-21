package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.mariuszgromada.math.mxparser.Expression;

public class ScientificCalculatorFragment extends Fragment {
    private EditText result;
    private String currentExpression = "";
    private static final String TAG = "ScientificCalcFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflating fragment_scientific_calculator");
        View view = inflater.inflate(R.layout.fragment_scientific_calculator, container, false);
        result = view.findViewById(R.id.result);
        if (result == null) {
            Log.e(TAG, "onCreateView: result EditText not found");
        } else {
            Log.d(TAG, "onCreateView: result EditText found");
        }
        setupButtons(view);
        setupResultField(view);
        Log.d(TAG, "onCreateView: Fragment setup completed");
        return view;
    }

    private void setupButtons(View view) {
        Log.d(TAG, "setupButtons: Setting up buttons");

        // Bouton Fermer
        Button btnCloseFragment = view.findViewById(R.id.btn_close_paren);
        if (btnCloseFragment != null) {
            btnCloseFragment.setOnClickListener(v -> closeFragment());
            Log.d(TAG, "setupButtons: btn_close_fragment found and set");
        } else {
            Log.e(TAG, "setupButtons: btn_close_fragment not found");
        }

        // Nombres
        setButton(view, R.id.btn_0, "0");
        setButton(view, R.id.btn_1, "1");
        setButton(view, R.id.btn_2, "2");
        setButton(view, R.id.btn_3, "3");
        setButton(view, R.id.btn_4, "4");
        setButton(view, R.id.btn_5, "5");
        setButton(view, R.id.btn_6, "6");
        setButton(view, R.id.btn_7, "7");
        setButton(view, R.id.btn_8, "8");
        setButton(view, R.id.btn_9, "9");

        // Opérations
        setButton(view, R.id.btn_add, "+");
        setButton(view, R.id.btn_subtract, "-");
        setButton(view, R.id.btn_multiply, "*");
        setButton(view, R.id.btn_divide, "/");
        setButton(view, R.id.btn_percent, "%");
        setButton(view, R.id.btn_equals, "=");

        // Fonctions spéciales
        setButton(view, R.id.btn_open_paren, "(");
        setButton(view, R.id.btn_close_paren, ")");
        setButton(view, R.id.btn_dot, ".");
        setButton(view, R.id.btn_plus_minus, "-"); // Changement de signe
        setButton(view, R.id.btn_power, "^2");
        setButton(view, R.id.btn_power_y, "^");

        // Fonctions scientifiques
        setButton(view, R.id.btn_sin, "sin(");
        setButton(view, R.id.btn_cos, "cos(");
        setButton(view, R.id.btn_tan, "tan(");
        setButton(view, R.id.btn_log, "log(");
        setButton(view, R.id.btn_ln, "ln(");
        setButton(view, R.id.btn_sqrt, "sqrt(");

        // Contrôles
        Button btnAc = view.findViewById(R.id.btn_ac);
        if (btnAc != null) {
            btnAc.setOnClickListener(v -> clear());
            Log.d(TAG, "setupButtons: btn_ac found and set");
        } else {
            Log.e(TAG, "setupButtons: btn_ac not found");
        }

        Button btnDel = view.findViewById(R.id.btn_del);
        if (btnDel != null) {
            btnDel.setOnClickListener(v -> delete());
            Log.d(TAG, "setupButtons: btn_del found and set");
        } else {
            Log.e(TAG, "setupButtons: btn_del not found");
        }
    }

    private void setupResultField(View view) {
        if (result != null) {
            result.setOnLongClickListener(v -> {
                clear();
                Toast.makeText(getContext(), "Expression effacée", Toast.LENGTH_SHORT).show();
                return true;
            });
            result.setOnClickListener(v -> result.selectAll());
            Log.d(TAG, "setupResultField: Result field listeners set");
        } else {
            Log.e(TAG, "setupResultField: result is null");
        }
    }

    private void closeFragment() {
        Log.d(TAG, "closeFragment: Closing fragment");
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void setButton(View view, int buttonId, String value) {
        Button button = view.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                Log.d(TAG, "Button clicked: ID=" + buttonId + ", Value=" + value);
                if (value.equals("=")) {
                    evaluateExpression();
                } else if (value.equals("-") && !currentExpression.isEmpty() && !currentExpression.endsWith("(")) {
                    // Changement de signe pour un nombre existant
                    appendToExpression("*(-1)");
                } else if (value.endsWith("(")) {
                    // Fonctions scientifiques
                    appendToExpression(value);
                } else {
                    appendToExpression(value);
                }
            });
            Log.d(TAG, "setButton: Button ID " + buttonId + " found and set");
        } else {
            Log.e(TAG, "setButton: Button ID " + buttonId + " not found");
        }
    }

    private void appendToExpression(String text) {
        currentExpression += text;
        if (result != null) {
            result.setText(currentExpression);
            Log.d(TAG, "appendToExpression: Updated to " + currentExpression);
        } else {
            Log.e(TAG, "appendToExpression: result is null");
        }
    }

    private void evaluateExpression() {
        Log.d(TAG, "evaluateExpression: Evaluating " + currentExpression);
        if (result == null) {
            Log.e(TAG, "evaluateExpression: result is null");
            return;
        }

        try {
            if (currentExpression.trim().isEmpty()) {
                result.setText("0.000000");
                currentExpression = "0";
                Log.d(TAG, "evaluateExpression: Empty expression set to 0");
                return;
            }

            // Ajuster les parenthèses manquantes
            String expressionToEvaluate = currentExpression;
            int openParens = countOccurrences(expressionToEvaluate, '(');
            int closeParens = countOccurrences(expressionToEvaluate, ')');
            for (int i = 0; i < openParens - closeParens; i++) {
                expressionToEvaluate += ")";
                Log.d(TAG, "evaluateExpression: Added closing parenthesis, now: " + expressionToEvaluate);
            }

            Expression e = new Expression(expressionToEvaluate);
            if (!e.checkSyntax()) {
                result.setText("Erreur de syntaxe");
                Log.e(TAG, "evaluateExpression: Syntax error in " + expressionToEvaluate);
                return;
            }

            double resultValue = e.calculate();
            if (Double.isNaN(resultValue) || Double.isInfinite(resultValue)) {
                result.setText("Erreur");
                Log.e(TAG, "evaluateExpression: Invalid result");
                return;
            }

            String formattedResult = String.format("%.6f", resultValue);
            result.setText(formattedResult);
            currentExpression = formattedResult;
            Log.d(TAG, "evaluateExpression: Result = " + formattedResult);
        } catch (Exception e) {
            Log.e(TAG, "evaluateExpression: Error", e);
            result.setText("Erreur");
            currentExpression = "";
        }
    }

    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    private void clear() {
        currentExpression = "";
        if (result != null) {
            result.setText("");
            Log.d(TAG, "clear: Expression cleared");
        }
    }

    private void delete() {
        if (!currentExpression.isEmpty()) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
            if (result != null) {
                result.setText(currentExpression);
                Log.d(TAG, "delete: Expression updated to " + currentExpression);
            }
        }
    }
}