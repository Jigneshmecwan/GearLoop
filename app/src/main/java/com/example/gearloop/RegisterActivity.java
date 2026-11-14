
package com.example.gearloop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtCity;
    private EditText edtPassword;
    private EditText edtConfirmPassword;

    private TextView txtSelectedRole;
    private TextView txtLogin;
    private TextView txtBack;

    private Button btnRegister;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;

    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setStatusBarColor(0xFFF7FAFF);
        getWindow().setNavigationBarColor(0xFFF7FAFF);

        // Connect XML views
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtCity = findViewById(R.id.edtCity);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        txtSelectedRole = findViewById(R.id.txtSelectedRole);
        txtLogin = findViewById(R.id.txtLogin);
        txtBack = findViewById(R.id.txtBack);

        btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        usersReference = FirebaseDatabase
                .getInstance()
                .getReference("users");

        // Get the role selected on the Welcome screen
        SharedPreferences preferences =
                getSharedPreferences("GearLoopPrefs", MODE_PRIVATE);

        selectedRole = preferences.getString("selectedRole", "");

        // Only allow the two supported roles
        if (!selectedRole.equals("Customer")
                && !selectedRole.equals("Owner")) {

            Toast.makeText(
                    this,
                    "Please choose a role first.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (selectedRole.equals("Customer")) {
            txtSelectedRole.setText("Customer — Rent Equipment");
        } else {
            txtSelectedRole.setText("Owner — List Equipment");
        }

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());

        // Return to Welcome
        txtBack.setOnClickListener(v -> finish());

        // Login screen is our next stage
        txtLogin.setOnClickListener(v ->
                Toast.makeText(
                        RegisterActivity.this,
                        "Login screen will be added next.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void registerUser() {

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String city = edtCity.getText().toString().trim();

        String password =
                edtPassword.getText().toString();

        String confirmPassword =
                edtConfirmPassword.getText().toString();

        // 1. Check empty fields
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Please enter your name");
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            edtCity.setError("Please enter your city");
            edtCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Please enter a password");
            edtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError(
                    "Please confirm your password"
            );
            edtConfirmPassword.requestFocus();
            return;
        }

        // 2. Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return;
        }

        // 3. Validate password length
        if (password.length() < 6) {
            edtPassword.setError(
                    "Password must have at least 6 characters"
            );
            edtPassword.requestFocus();
            return;
        }

        // 4. Check passwords match
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError(
                    "Passwords do not match"
            );
            edtConfirmPassword.requestFocus();
            return;
        }

        // Prevent repeated taps during registration
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        // 5. Create Firebase Authentication account
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Create Account");

                        String message = "Registration failed";

                        if (task.getException() != null
                                && task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    FirebaseUser user =
                            firebaseAuth.getCurrentUser();

                    if (user == null) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Create Account");

                        Toast.makeText(
                                this,
                                "Account created, but user was not found.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String uid = user.getUid();

                    // 6. Prepare user profile
                    Map<String, Object> userData = new HashMap<>();

                    userData.put("name", name);
                    userData.put("email", email);
                    userData.put("city", city);
                    userData.put("role", selectedRole);
                    userData.put("createdAt",
                            System.currentTimeMillis());

                    // Never save the password in Realtime Database

                    // 7. Save the role and profile to Firebase
                    usersReference.child(uid)
                            .setValue(userData)
                            .addOnCompleteListener(databaseTask -> {

                                btnRegister.setEnabled(true);
                                btnRegister.setText("Create Account");

                                if (databaseTask.isSuccessful()) {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Account created successfully!",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    // Temporary return to Welcome.
                                    // Role-based dashboard comes later.
                                    Intent intent = new Intent(
                                            RegisterActivity.this,
                                            MainActivity.class
                                    );

                                    startActivity(intent);
                                    finish();

                                } else {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Authentication account created, "
                                                    + "but profile could not be saved. "
                                                    + "Check database rules and connection.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });
                });
    }
}
