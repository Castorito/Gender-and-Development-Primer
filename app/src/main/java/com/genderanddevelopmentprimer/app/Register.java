package com.genderanddevelopmentprimer.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    TextInputEditText firstName, lastName, municipality, province, emailAddress, password, retypePass;
    Spinner userType, userSex;
    TextView btnregister, btnsignIn;
    LinearLayout mainLayout;
    RelativeLayout loading;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InputFilter capitalizeFirstFilter = (source, start, end, dest, dstart, dend) -> {
            if (start == 0 && source.length() > 0) {
                String firstLetter = Character.toUpperCase(source.charAt(0)) + "";
                String remainingLetters = source.subSequence(1, source.length()).toString();
                return firstLetter + remainingLetters;
            }
            return source;
        };

        firstName = findViewById(R.id.fName);
        lastName = findViewById(R.id.lName);
        userType = findViewById(R.id.userType);
        userSex = findViewById(R.id.sex);
        municipality = findViewById(R.id.register_municipality);
        municipality.setFilters(new InputFilter[]{capitalizeFirstFilter});
        province = findViewById(R.id.register_province);
        province.setFilters(new InputFilter[]{capitalizeFirstFilter});
        emailAddress = findViewById(R.id.register_emailAddress);
        password = findViewById(R.id.register_password);
        retypePass = findViewById(R.id.register_confirmPassword);

        btnregister = findViewById(R.id.btn_register);
        btnsignIn = findViewById(R.id.btn_login);

        loading = findViewById(R.id.register_loading);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mainLayout = findViewById(R.id.register_form);

        //register button
        btnregister.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            firstName.clearFocus();
            lastName.clearFocus();
            municipality.clearFocus();
            province.clearFocus();
            emailAddress.clearFocus();
            password.clearFocus();
            retypePass.clearFocus();

            String varFName = Objects.requireNonNull(firstName.getText()).toString().trim();
            String varLName = Objects.requireNonNull(lastName.getText()).toString().trim();
            String varUserType = userType.getSelectedItem().toString();
            String varSex = userSex.getSelectedItem().toString();
            String varMunicipality = Objects.requireNonNull(municipality.getText()).toString().trim();
            String varProvince = Objects.requireNonNull(province.getText()).toString().trim();
            String varEmailAdd = Objects.requireNonNull(emailAddress.getText()).toString().trim();
            String varPassword = Objects.requireNonNull(password.getText()).toString();
            String varRetypePass = Objects.requireNonNull(retypePass.getText()).toString();

            if (TextUtils.isEmpty(varFName)) {
                firstName.setError("Enter your first name.");
                return;
            }

            if (TextUtils.isEmpty(varLName)) {
                lastName.setError("Enter your last name.");
                return;
            }

            if (TextUtils.isEmpty(varMunicipality)) {
                municipality.setError("Enter your municipality.");
                return;
            }

            if (TextUtils.isEmpty(varProvince)) {
                province.setError("Enter your province.");
                return;
            }

            if (TextUtils.isEmpty(varEmailAdd)) {
                emailAddress.setError("Enter an email address.");
                return;
            }

            if (TextUtils.isEmpty(varPassword)) {
                password.setError("Enter a password.");
                return;
            }

            if (varPassword.length() < 8) {
                password.setError("Password must be 8 characters or longer");
                return;
            }

            if (TextUtils.isEmpty(varRetypePass)) {
                retypePass.setError("Cannot be empty.");
                return;
            }

            if (!varPassword.equals(varRetypePass)) {
                retypePass.setError("Passwords do not match.");
                return;
            }

            fAuth.createUserWithEmailAndPassword(varEmailAdd, varPassword).addOnCompleteListener(task -> {
                //insert user to database
                if (task.isSuccessful()) {
                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", varFName);
                    user.put("lastName", varLName);
                    user.put("userType", varUserType);
                    user.put("sex", varSex);
                    user.put("municipality", varMunicipality);
                    user.put("province", varProvince);
                    user.put("email", varEmailAdd);
                    documentReference.set(user).addOnSuccessListener(unused -> {
                        Toast.makeText(Register.this, "User Created!", Toast.LENGTH_SHORT).show();
                        //verify email
                        FirebaseUser verifyUser = fAuth.getCurrentUser();
                        Objects.requireNonNull(verifyUser).sendEmailVerification().addOnSuccessListener(unused1 -> {
                            Toast.makeText(Register.this, "Verification email sent, verify first before logging in!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        });
                    });
                } else {
                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            loading.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });

        btnsignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }
}