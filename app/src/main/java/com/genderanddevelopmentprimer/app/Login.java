package com.genderanddevelopmentprimer.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPass;
    Button btnLogin;
    TextView btnsignUp, btnForgotPass;
    RelativeLayout loading;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_emailAddress);
        loginPass = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);

        btnsignUp = findViewById(R.id.btn_signUp);
        btnForgotPass = findViewById(R.id.btn_forgotPass);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loading = findViewById(R.id.loading_layout);

        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), StudentActivity.class));
            finish();
        }

        //Login Button
        btnLogin.setOnClickListener(v -> {

            isOnline();

            String varEmail = loginEmail.getText().toString().trim();
            String varPass = loginPass.getText().toString().trim();

            if (TextUtils.isEmpty(varEmail) && TextUtils.isEmpty(varPass)) {
                loginEmail.setError("Email is required!");
                loginPass.setError("Password is Required!");
                return;
            }

            if (TextUtils.isEmpty(varEmail)) {
                loginEmail.setError("Email is required!");
                return;
            }

            if (TextUtils.isEmpty(varPass)) {
                loginPass.setError("Password is required!");
                return;
            } else if (loginPass.length() < 8) {
                loginPass.setError("Password must be 8 characters or longer!");
                return;
            }

            LinearLayout mainLayout = findViewById(R.id.login_form);
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            loading.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            fAuth.signInWithEmailAndPassword(varEmail, varPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //get userType/retrieve data
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.addSnapshotListener((value, error) -> {
                        assert value != null;
                        if (Objects.equals(value.getString("userType"), "Teacher")) {
                                startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                            } else if (Objects.equals(value.getString("userType"), "Student")) {
                                startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                            }
                    });
                    Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Login.this, "Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

        });

        btnsignUp.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

        btnForgotPass.setOnClickListener(v -> {
            EditText resetEmail = new EditText(v.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter registered email to receive reset link:");

            //add padding to textbox in forgot pass
            FrameLayout container = new FrameLayout(Login.this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = 50;
            params.rightMargin = 50;

            resetEmail.setLayoutParams(params);
            container.addView(resetEmail);

            passwordResetDialog.setView(resetEmail);
            passwordResetDialog.setView(container);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                //reset link
                String email = resetEmail.getText().toString().trim();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused ->
                        Toast.makeText(Login.this, "Reset link sent!", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                        Toast.makeText(Login.this, "Error! Reset link not sent. " + Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show()
                );
            });
            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                //close dialog
            });

            passwordResetDialog.create().show();
        });
    }

    //check of there is internet connection
    public void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }
}