package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText firstName, lastName, municipality, province, emailAddress, password, retypePass;
    CheckBox showPass;
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

        firstName = findViewById(R.id.fName);
        lastName = findViewById(R.id.lName);
        userType = findViewById(R.id.userType);
        userSex = findViewById(R.id.sex);
        municipality = findViewById(R.id.register_municipality);
        province = findViewById(R.id.register_province);
        emailAddress = findViewById(R.id.register_emailAddress);
        password = findViewById(R.id.register_password);
        retypePass = findViewById(R.id.register_retypePass);

        showPass = findViewById(R.id.showPassReg);

        btnregister = findViewById(R.id.btn_register);
        btnsignIn = findViewById(R.id.btn_login);

        loading = findViewById(R.id.register_loading);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mainLayout = findViewById(R.id.register_form);

        //show password
        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                retypePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                retypePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.length() < 8) {
                    password.setError("Password must be 8 characters or longer!");
                } else if (!password.getText().toString().equals(retypePass.getText().toString())) {
                    retypePass.setError("Password not the same!");
                } else {
                    retypePass.setError(null);
                }
            }
        });

        retypePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!retypePass.getText().toString().equals(password.getText().toString())) {
                    password.setError("Password not the same!");
                } else if (retypePass.getText().toString().equals("")) {
                    retypePass.setError(null);
                } else {
                    password.setError(null);
                }
            }

        });

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

            String varFName = firstName.getText().toString().trim();
            String varLName = lastName.getText().toString().trim();
            String varUserType = userType.getSelectedItem().toString();
            String varSex = userSex.getSelectedItem().toString();
            String varMunicipality = municipality.getText().toString().trim();
            String varProvince = province.getText().toString().trim();
            String varEmailAdd = emailAddress.getText().toString().trim();
            String varPassword = password.getText().toString();
            String varRetypePass = retypePass.getText().toString();

            if (TextUtils.isEmpty(varFName)) {
                firstName.setError("Enter your first name.");
            }

            if (TextUtils.isEmpty(varLName)) {
                lastName.setError("Enter your last name.");
            }

            if (TextUtils.isEmpty(varMunicipality)) {
                municipality.setError("Enter your municipality.");
            }

            if (TextUtils.isEmpty(varProvince)) {
                province.setError("Enter your province.");
            }

            if (TextUtils.isEmpty(varEmailAdd)) {
                emailAddress.setError("Enter an email address.");
            }

            if (TextUtils.isEmpty(varPassword)) {
                password.setError("Enter a password.");
            }

            if (TextUtils.isEmpty(varRetypePass)) {
                retypePass.setError("Retype your password.");
            } else if (!varPassword.equals(varRetypePass)) {
                retypePass.setError("Password not the same!");
            } else {

                loading.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                fAuth.createUserWithEmailAndPassword(varEmailAdd, varPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //insert user to database
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
                                Toast.makeText(Register.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(Register.this, "Verify email first before logging in!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();

                        }).addOnFailureListener(e -> Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(Register.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }
        });

        btnsignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }
}