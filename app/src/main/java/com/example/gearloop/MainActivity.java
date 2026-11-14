
package com.example.gearloop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout cardCustomer;
    private LinearLayout cardOwner;

    private TextView txtCustomerStatus;
    private TextView txtOwnerStatus;
    private TextView txtSignIn;

    private Button btnContinue;

    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(0xFFF7FAFF);
        getWindow().setNavigationBarColor(0xFFF7FAFF);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        // Connect XML with Java
        cardCustomer = findViewById(R.id.cardCustomer);
        cardOwner = findViewById(R.id.cardOwner);

        txtCustomerStatus =
                findViewById(R.id.txtCustomerStatus);

        txtOwnerStatus =
                findViewById(R.id.txtOwnerStatus);

        txtSignIn = findViewById(R.id.txtSignIn);
        btnContinue = findViewById(R.id.btnContinue);

        // Initially no role is selected
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        cardCustomer.setOnClickListener(v ->
                selectRole("Customer")
        );

        cardOwner.setOnClickListener(v ->
                selectRole("Owner")
        );

        // Continue to registration
        btnContinue.setOnClickListener(v -> {

            if (selectedRole.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Please select a role",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            SharedPreferences preferences =
                    getSharedPreferences(
                            "GearLoopPrefs",
                            MODE_PRIVATE
                    );

            preferences.edit()
                    .putString("selectedRole", selectedRole)
                    .apply();

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Login comes next
        txtSignIn.setOnClickListener(v ->
                Toast.makeText(
                        MainActivity.this,
                        "Login screen will be added next.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void selectRole(String role) {

        selectedRole = role;

        boolean isCustomer = role.equals("Customer");

        cardCustomer.setBackgroundResource(
                isCustomer
                        ? R.drawable.bg_role_selected
                        : R.drawable.bg_role_unselected
        );

        cardOwner.setBackgroundResource(
                isCustomer
                        ? R.drawable.bg_role_unselected
                        : R.drawable.bg_role_selected
        );

        txtCustomerStatus.setText(
                isCustomer ? "✓ Selected" : "Select"
        );

        txtOwnerStatus.setText(
                isCustomer ? "Select" : "✓ Selected"
        );

        btnContinue.setEnabled(true);
        btnContinue.setAlpha(1.0f);
    }
}
