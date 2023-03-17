package com.genderanddevelopmentprimer.app.mainfunctions;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

        setBackground();

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
            FirebaseUser getUser = fAuth.getCurrentUser();
            if (getUser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                finish();
            }
        }
        //show password
        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                loginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        //Login Button
        btnLogin.setOnClickListener(v -> {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            loginEmail.clearFocus();
            loginPass.clearFocus();

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
                            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Email still not verified!").setMessage("Resend verification link?").setPositiveButton("Yes", (dialog, which) -> user.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(Login.this, "Email verification link sent!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show())).setNegativeButton("No", null).show();
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
            Dialog forgotPass = new Dialog(Login.this);
            forgotPass.requestWindowFeature(Window.FEATURE_NO_TITLE);
            forgotPass.setContentView(R.layout.forgot_password);
            forgotPass.setCancelable(false);

            EditText forgotPassEmail = forgotPass.findViewById(R.id.forgotPass_email);
            Button btnCancel = forgotPass.findViewById(R.id.btn_cancelforgotPass);
            Button btnSend = forgotPass.findViewById(R.id.btn_send);

            btnCancel.setOnClickListener(v12 -> forgotPass.dismiss());

            btnSend.setOnClickListener(v1 -> {
                //reset link
                String email = forgotPassEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(Login.this, "Email cannot be blank!", Toast.LENGTH_SHORT).show();
                } else {
                    fAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                        Toast.makeText(Login.this, "Reset link sent!", Toast.LENGTH_SHORT).show();
                        forgotPass.dismiss();
                    }).addOnFailureListener(e -> Toast.makeText(Login.this, "Error! Reset link not sent. " + Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            });
            forgotPass.show();
        });
    }

    private void setBackground() {
        //set background
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setBackgroundDrawableResource(R.drawable.darkbackground);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:

            case Configuration.UI_MODE_NIGHT_NO:
                getWindow().setBackgroundDrawableResource(R.drawable.lightbackground);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Press again to confirm exit.", Toast.LENGTH_SHORT).show();
        isBackPressedOnce = true;
        new Handler().postDelayed(() -> isBackPressedOnce = false, 2000);
    }
}