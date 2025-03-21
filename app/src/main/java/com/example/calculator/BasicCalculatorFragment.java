// BasicCalculatorFragment.java
package com.example.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BasicCalculatorFragment extends Fragment {
    private EditText result;
    private double valueOne = Double.NaN;
    private double valueTwo;
    private char currentOperation;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_calculator, container, false);
        result = view.findViewById(R.id.result);
        dbRef = FirebaseDatabase.getInstance().getReference("history");
        setupButtons(view);
        return view;
    }

    private void setupButtons(View view) {
        int[] numberIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_dot};

        View.OnClickListener numberClickListener = v -> {
            Button button = (Button) v;
            result.append(button.getText());
        };

        for (int id : numberIds) {
            view.findViewById(id).setOnClickListener(numberClickListener);
        }

        view.findViewById(R.id.btn_add).setOnClickListener(v -> compute('+'));
        view.findViewById(R.id.btn_subtract).setOnClickListener(v -> compute('-'));
        view.findViewById(R.id.btn_multiply).setOnClickListener(v -> compute('*'));
        view.findViewById(R.id.btn_divide).setOnClickListener(v -> compute('/'));

        view.findViewById(R.id.btn_equals).setOnClickListener(v -> {
            if (!Double.isNaN(valueOne) && !result.getText().toString().isEmpty()) {
                valueTwo = Double.parseDouble(result.getText().toString());
                computeResult();
                String historyEntry = valueOne + " " + currentOperation + " " + valueTwo + " = " + valueOne;
                dbRef.push().setValue(historyEntry);
                result.setText(String.valueOf(valueOne));
                valueOne = Double.NaN;
            }
        });

        view.findViewById(R.id.btn_clear).setOnClickListener(v -> {
            valueOne = Double.NaN;
            valueTwo = 0;
            result.setText("");
        });
    }

    private void compute(char operation) {
        if (!result.getText().toString().isEmpty()) {
            if (Double.isNaN(valueOne)) {
                valueOne = Double.parseDouble(result.getText().toString());
            } else {
                valueTwo = Double.parseDouble(result.getText().toString());
                computeResult();
            }
            currentOperation = operation;
            result.setText("");
        }
    }

    private void computeResult() {
        switch (currentOperation) {
            case '+': valueOne += valueTwo; break;
            case '-': valueOne -= valueTwo; break;
            case '*': valueOne *= valueTwo; break;
            case '/':
                if (valueTwo != 0) valueOne /= valueTwo;
                else result.setText("Erreur");
                break;
        }
    }
}