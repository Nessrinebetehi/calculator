package com.example.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConverterFragment extends Fragment {
    private EditText inputNumber, firstNumber, secondNumber;
    private Spinner sourceBase, targetBase, calcBase;
    private TextView resultText, calcResultText;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        // Initialisation des vues pour la section de conversion
        inputNumber = view.findViewById(R.id.input_number);
        sourceBase = view.findViewById(R.id.source_base);
        targetBase = view.findViewById(R.id.target_base);
        resultText = view.findViewById(R.id.result_text);

        // Initialisation des vues pour la section de calcul
        firstNumber = view.findViewById(R.id.first_number);
        secondNumber = view.findViewById(R.id.second_number);
        calcBase = view.findViewById(R.id.calc_base);
        calcResultText = view.findViewById(R.id.calc_result_text);

        // Initialisation de Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("history");

        // Listener pour le bouton de conversion
        view.findViewById(R.id.btn_convert).setOnClickListener(v -> convertNumber());

        // Listeners pour les boutons de calcul
        view.findViewById(R.id.btn_add_calc).setOnClickListener(v -> performCalculation("+"));
        view.findViewById(R.id.btn_subtract_calc).setOnClickListener(v -> performCalculation("-"));
        view.findViewById(R.id.btn_multiply_calc).setOnClickListener(v -> performCalculation("*"));
        view.findViewById(R.id.btn_divide_calc).setOnClickListener(v -> performCalculation("/"));

        return view;
    }

    // Fonction pour gérer la conversion
    private void convertNumber() {
        String input = inputNumber.getText().toString().trim();
        if (input.isEmpty()) {
            resultText.setText("Erreur: Entrez un nombre");
            return;
        }

        int sourcePos = sourceBase.getSelectedItemPosition();
        int targetPos = targetBase.getSelectedItemPosition();

        try {
            long value = parseInput(input, sourcePos);
            String result = convertToTarget(value, targetPos);
            resultText.setText("Résultat: " + result);
            dbRef.push().setValue(input + " (" + sourceBase.getSelectedItem() + " → " +
                    targetBase.getSelectedItem() + ") = " + result);
        } catch (NumberFormatException e) {
            resultText.setText("Erreur: Entrée invalide pour la base sélectionnée");
        }
    }

    // Fonction pour gérer les calculs
    private void performCalculation(String operation) {
        String firstInput = firstNumber.getText().toString().trim();
        String secondInput = secondNumber.getText().toString().trim();

        if (firstInput.isEmpty() || secondInput.isEmpty()) {
            calcResultText.setText("Erreur: Entrez deux nombres");
            return;
        }

        int basePos = calcBase.getSelectedItemPosition();

        try {
            long num1 = parseInput(firstInput, basePos);
            long num2 = parseInput(secondInput, basePos);
            long result;

            switch (operation) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        calcResultText.setText("Erreur: Division par zéro");
                        return;
                    }
                    result = num1 / num2;
                    break;
                default:
                    calcResultText.setText("Erreur: Opération invalide");
                    return;
            }

            String resultStr = convertToTarget(result, basePos);
            calcResultText.setText("Résultat: " + num1 + " " + operation + " " + num2 + " = " + resultStr);
            dbRef.push().setValue(firstInput + " " + operation + " " + secondInput + " (" +
                    calcBase.getSelectedItem() + ") = " + resultStr);
        } catch (NumberFormatException e) {
            calcResultText.setText("Erreur: Entrée invalide pour la base sélectionnée");
        }
    }

    // Analyse l'entrée selon la base sélectionnée
    private long parseInput(String input, int baseIndex) {
        switch (baseIndex) {
            case 0: return Long.parseLong(input); // Décimal
            case 1: return Long.parseLong(input, 16); // Hexadécimal
            case 2: return Long.parseLong(input, 2); // Binaire
            case 3: return Long.parseLong(input, 8); // Octal
            default: throw new IllegalArgumentException("Base invalide");
        }
    }

    // Convertit la valeur dans la base cible
    private String convertToTarget(long value, int baseIndex) {
        switch (baseIndex) {
            case 0: return String.valueOf(value); // Décimal
            case 1: return Long.toHexString(value).toUpperCase(); // Hexadécimal
            case 2: return Long.toBinaryString(value); // Binaire
            case 3: return Long.toOctalString(value); // Octal
            default: throw new IllegalArgumentException("Base invalide");
        }
    }
}