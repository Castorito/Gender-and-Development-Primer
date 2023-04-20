package com.genderanddevelopmentprimer.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
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

public class Settings extends AppCompatActivity {
    TextInputEditText fName, lName, email, municipality, province, sex, usertype;
    Button btneditProfile, btnChangePass;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
            if (value != null) {
                fName.setText(value.getString("firstName"));
                lName.setText(value.getString("lastName"));
                sex.setText(value.getString("sex"));
                email.setText(value.getString("email"));
                municipality.setText(value.getString("municipality"));
                province.setText(value.getString("province"));
                usertype.setText(value.getString("userType"));
            }
        });

        btnChangePass.setOnClickListener(v -> showCustomChangePass());

        btneditProfile.setOnClickListener(v -> {
            if (btneditProfile.getText().equals("Edit Profile")) {
                fName.setEnabled(true);
                lName.setEnabled(true);
                municipality.setEnabled(true);
                province.setEnabled(true);
                btneditProfile.setText("Save");

            } else if (btneditProfile.getText().equals("Save")) {
                DocumentReference check = fStore.collection("users").document(user.getUid());
                check.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the current values from the document snapshot
                        String firstNamevar = documentSnapshot.getString("firstName");
                        String lastNamevar = documentSnapshot.getString("lastName");
                        String municipalityvar = documentSnapshot.getString("municipality");
                        String provincevar = documentSnapshot.getString("province");
                        String emailvar = documentSnapshot.getString("email");

                        if (Objects.requireNonNull(fName.getText()).toString().isEmpty() || Objects.requireNonNull(lName.getText()).toString().isEmpty() || Objects.requireNonNull(municipality.getText()).toString().isEmpty() || Objects.requireNonNull(province.getText()).toString().isEmpty() || Objects.requireNonNull(email.getText()).toString().isEmpty()) {
                            Toast.makeText(this, "Fill up empty fields!", Toast.LENGTH_SHORT).show();
                        } else if (fName.getText().toString().equals(firstNamevar) && lName.getText().toString().equals(lastNamevar) && municipality.getText().toString().equals(municipalityvar) && province.getText().toString().equals(provincevar) && email.getText().toString().equals(emailvar)) {
                            // No changes were made, so do nothing or show a message
                            fName.setEnabled(false);
                            lName.setEnabled(false);
                            municipality.setEnabled(false);
                            province.setEnabled(false);
                            btneditProfile.setText("Edit Profile");
                        } else {
                            // Update the user profile
                            DocumentReference documentRef = fStore.collection("users").document(user.getUid());
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("firstName", fName.getText().toString());
                            userProfile.put("lastName", lName.getText().toString());
                            userProfile.put("municipality", municipality.getText().toString());
                            userProfile.put("province", province.getText().toString());
                            documentRef.update(userProfile).addOnSuccessListener(unused1 -> {
                                Toast.makeText(Settings.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // Document does not exist, handle the error
                        Toast.makeText(Settings.this, "Error: Document does not exist!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(Settings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }
        });
    }

    private void showCustomChangePass() {
        Dialog changepass = new Dialog(Settings.this);
        changepass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changepass.setContentView(R.layout.password_change_dialog_box);
        changepass.setCancelable(false);

        TextInputEditText newPass = changepass.findViewById(R.id.newPass);
        TextInputEditText retype = changepass.findViewById(R.id.retypeNewPass);
        Button btnsave = changepass.findViewById(R.id.btn_savePass);
        Button btncancel = changepass.findViewById(R.id.btn_cancelPass);

        btnsave.setOnClickListener(v -> {
            String varPass = Objects.requireNonNull(newPass.getText()).toString();
            String varRetype = Objects.requireNonNull(retype.getText()).toString();

            if (varPass.length() < 8) {
                newPass.setError("Password must be 8 characters or longer.");
            } else if (!varPass.equals(varRetype)) {
                retype.setError("Passwords do not match.");
            } else {
                user.updatePassword(varPass).addOnSuccessListener(unused -> {
                    Toast.makeText(Settings.this, "Password changed.", Toast.LENGTH_SHORT).show();
                    changepass.dismiss();
                }).addOnFailureListener(e -> Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        btncancel.setOnClickListener(v -> changepass.dismiss());
        changepass.show();

    }
}