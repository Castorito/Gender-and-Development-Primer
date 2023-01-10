package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText firstName, lastName, municipality, province, emailAddress, password, retypePass;
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

            isOnline();

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
                return;
            } else if (TextUtils.isEmpty(varLName)) {
                lastName.setError("Enter your last name.");
                return;
            } else if (TextUtils.isEmpty(varMunicipality)) {
                municipality.setError("Enter your municipality.");
                return;
            } else if (TextUtils.isEmpty(varProvince)) {
                province.setError("Enter your province.");
                return;
            } else if (TextUtils.isEmpty(varEmailAdd)) {
                emailAddress.setError("Enter an email address.");
                return;
            } else if (TextUtils.isEmpty(varPassword)) {
                password.setError("Enter a password.");
                return;
            } else if (TextUtils.isEmpty(varRetypePass)) {
                retypePass.setError("Retype your password.");
                return;
            }

            if (!varPassword.equals(varRetypePass)) {
                retypePass.setError("Password not the same!");
                return;
            }

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

                    Toast.makeText(Register.this, "User Created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                    finish();
                } else {
                    Toast.makeText(Register.this, "Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        });

        btnsignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }

    //check if there is internet
    public void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}