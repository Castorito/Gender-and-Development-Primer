package com.genderanddevelopmentprimer.app.mainfunctions;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class Settings extends AppCompatActivity {
    EditText fName, lName, email, municipality, province;
    TextView sex, usertype;
    Button btneditProfile, btnChangePass;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set background
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setBackgroundDrawableResource(R.drawable.darkbackground);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getWindow().setBackgroundDrawableResource(R.drawable.lightbackground);
                break;
        }

        btneditProfile = findViewById(R.id.btn_editProfile);
        btnChangePass = findViewById(R.id.btn_changePass);

        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        sex = findViewById(R.id.sex);
        email = findViewById(R.id.email);
        municipality = findViewById(R.id.municipality);
        province = findViewById(R.id.province);
        usertype = findViewById(R.id.userType);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            fName.setText(value.getString("firstName"));
            lName.setText(value.getString("lastName"));
            sex.setText(value.getString("sex"));
            email.setText(value.getString("email"));
            municipality.setText(value.getString("municipality"));
            province.setText(value.getString("province"));
            usertype.setText(value.getString("userType"));
        });

        btnChangePass.setOnClickListener(v -> showCustomChangePass());

        btneditProfile.setOnClickListener(v -> {
            if (btneditProfile.getText().equals("Edit Profile")) {
                fName.setEnabled(true);
                lName.setEnabled(true);
                email.setEnabled(true);
                municipality.setEnabled(true);
                province.setEnabled(true);
                btneditProfile.setText("Save");

            } else if (btneditProfile.getText().equals("Save")) {
                DocumentReference check = fStore.collection("users").document(user.getUid());
                check.addSnapshotListener((value, error) -> {
                    assert value != null;
                    if (fName.getText().toString().equals(value.getString("firstName"))
                            && lName.getText().toString().equals(value.getString("lastName"))
                            && email.getText().toString().equals(value.getString("email"))
                            && municipality.getText().toString().equals(value.getString("municipality"))
                            && province.getText().toString().equals(value.getString("province"))) {
                        fName.setEnabled(false);
                        lName.setEnabled(false);
                        email.setEnabled(false);
                        municipality.setEnabled(false);
                        province.setEnabled(false);
                        btneditProfile.setText("Edit Profile");
                    } else {
                        String varEmail = email.getText().toString();

                        user.updateEmail(varEmail).addOnSuccessListener(unused -> {
                            DocumentReference documentRef = fStore.collection("users").document(user.getUid());
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", fName.getText().toString());
                            user.put("lastName", lName.getText().toString());
                            user.put("municipality", municipality.getText().toString());
                            user.put("province", province.getText().toString());
                            user.put("email", varEmail);
                            documentRef.update(user).addOnSuccessListener(unused1 -> {
                                Toast.makeText(Settings.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }).addOnFailureListener(e -> Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }

    void showCustomChangePass() {
        Dialog changepass = new Dialog(Settings.this);
        changepass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changepass.setContentView(R.layout.password_change_dialog_box);
        changepass.setCancelable(false);

        EditText newPass = changepass.findViewById(R.id.newPass);
        EditText retype = changepass.findViewById(R.id.retypeNewPass);
        CheckBox showPass = changepass.findViewById(R.id.showChangePass);
        Button btnsave = changepass.findViewById(R.id.btn_savePass);
        Button btncancel = changepass.findViewById(R.id.btn_cancelPass);

        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                retype.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                retype.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (newPass.length() < 8) {
                    newPass.setError("Password must be 8 characters or longer!");
                } else if (!newPass.getText().toString().equals(retype.getText().toString())) {
                    retype.setError("Password not the same!");
                } else {
                    retype.setError(null);
                }
            }
        });

        retype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!retype.getText().toString().equals(newPass.getText().toString())) {
                    newPass.setError("Password not the same!");
                } else if (retype.getText().toString().equals("")) {
                    retype.setError(null);
                } else {
                    newPass.setError(null);
                }
            }

        });
        btnsave.setOnClickListener(v -> {
            String varPass = newPass.getText().toString();
            String varRetype = retype.getText().toString();

            if (varPass.length() < 8) {
                newPass.setError("Password must be 8 characters or longer!");
            } else if (!varPass.equals(varRetype)) {
                newPass.setError("Passwords not the same!");
                retype.setError("Passwords not the same!");
            } else {
                user.updatePassword(varPass).addOnSuccessListener(unused -> {
                    Toast.makeText(Settings.this, "Password changed!", Toast.LENGTH_SHORT).show();
                    changepass.dismiss();
                }).addOnFailureListener(e -> Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        btncancel.setOnClickListener(v -> changepass.dismiss());
        changepass.show();

    }
}