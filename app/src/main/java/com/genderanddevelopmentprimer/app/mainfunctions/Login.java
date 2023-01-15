package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPass;
    Button btnLogin;
    CheckBox showPass;
    TextView btnsignUp, btnForgotPass;
    RelativeLayout loading;
    LinearLayout mainLayout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Boolean isBackPressedOnce = false;
    int unverified = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_emailAddress);
        loginPass = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);

        showPass = findViewById(R.id.showPass);

        btnsignUp = findViewById(R.id.btn_signUp);
        btnForgotPass = findViewById(R.id.btn_forgotPass);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loading = findViewById(R.id.login_loading);

        mainLayout = findViewById(R.id.login_form);

        //account logged in
        if (fAuth.getCurrentUser() != null) {
            FirebaseUser newuser = fAuth.getCurrentUser();

            if (newuser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                finish();
            }
        }
        //show password
        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                loginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                loginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        //Login Button
        btnLogin.setOnClickListener(v -> {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            isOnline();

            unverified++;

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

            loading.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            fAuth.signInWithEmailAndPassword(varEmail, varPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = fAuth.getCurrentUser();
                    if (!user.isEmailVerified()) {
                        Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (unverified == 3) {
                            unverified = 0;
                            new AlertDialog.Builder(this)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle("Email still not verified!")
                                    .setMessage("Resend verification link?")
                                    .setPositiveButton("Yes", (dialog, which) -> user.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(Login.this, "Email verification link sent!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    } else {
                        Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                    }

                } else {
                    Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.INVISIBLE);
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
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Reset link sent!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, "Error! Reset link not sent. " + Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show());
            });
            passwordResetDialog.setNegativeButton("No", null);

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

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce){
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Press again to confirm exit.", Toast.LENGTH_SHORT).show();
        isBackPressedOnce = true;
        new Handler().postDelayed((Runnable) () -> isBackPressedOnce = false, 2000);
    }
}