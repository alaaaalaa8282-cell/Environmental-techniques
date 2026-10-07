package com.abumohamed.ecotools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionHelper.requestMissing(this);

        Button btnEis = findViewById(R.id.btnEis);
        Button btnAudit = findViewById(R.id.btnAudit);
        Button btnProblemSolver = findViewById(R.id.btnProblemSolver);
        Button btnDictionary = findViewById(R.id.btnDictionary);

        btnEis.setOnClickListener(v -> openTool("eis_generator.html"));
        btnAudit.setOnClickListener(v -> openTool("environmental_audit.html"));
        btnProblemSolver.setOnClickListener(v -> openTool("problem_solver_expert.html"));
        btnDictionary.setOnClickListener(v -> openTool("environmental_dictionary.html"));
    }

    private void openTool(String fileName) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("file", fileName);
        startActivity(intent);
    }
}
