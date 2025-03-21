package com.example.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.mariuszgromada.math.mxparser.Expression;

public class GraphFragment extends Fragment {
    private EditText functionInput;
    private GraphView graphView;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        functionInput = view.findViewById(R.id.function_input);
        graphView = view.findViewById(R.id.graph);
        dbRef = FirebaseDatabase.getInstance().getReference("history");

        view.findViewById(R.id.btn_plot).setOnClickListener(v -> plotFunction());

        // Initialisation de base du GraphView
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(-10);
        graphView.getViewport().setMaxX(10);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);

        return view;
    }

    private void plotFunction() {
        String function = functionInput.getText().toString().trim();
        if (function.isEmpty()) {
            Toast.makeText(getContext(), "Entrez une fonction", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création de l'expression avec la variable x
        Expression expr = new Expression(function);
        expr.defineArgument("x", 0); // Définir x comme variable

        if (!expr.checkSyntax()) {
            Toast.makeText(getContext(), "Fonction invalide: " + expr.getErrorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        try {
            for (double x = -10; x <= 10; x += 0.1) {
                expr.setArgumentValue("x", x); // Mettre à jour la valeur de x
                double y = expr.calculate();
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    series.appendData(new DataPoint(x, y), true, 200);
                }
            }

            graphView.addSeries(series);
            dbRef.push().setValue("Graphed: " + function);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur lors du traçage: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}