package com.genderanddevelopmentprimer.app.mainfunctions;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    ImageView editpic;
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

        editpic = findViewById(R.id.edit_icon);

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

        editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile pic
                Toast.makeText(Settings.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePass.setOnClickListener(v -> {

            EditText resetPass = new EditText(this);
            resetPass.setTransformationMethod(PasswordTransformationMethod.getInstance());


            AlertDialog.Builder changePass = new AlertDialog.Builder(this);
            changePass.setMessage("Enter new password:");
            changePass.setTitle("Change Password");

            //add padding to textbox in forgot pass
            FrameLayout container = new FrameLayout(Settings.this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = 70;
            params.rightMargin = 70;

            resetPass.setLayoutParams(params);
            container.addView(resetPass);

            changePass.setView(resetPass);
            changePass.setView(container);

            changePass.setPositiveButton("OK", null);
            changePass.setNegativeButton("Cancel", null);

            AlertDialog dialog = changePass.create();
            dialog.show();
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v12 -> {
                String newPass = resetPass.getText().toString();

                if (newPass.length() < 8) {
                    resetPass.setError("Password must be 8 characters or longer!");
                } else {
                    user.updatePassword(newPass).addOnSuccessListener(unused -> {
                        Toast.makeText(Settings.this, "Password changed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }).addOnFailureListener(e -> Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v1 -> dialog.dismiss());
        });

        btneditProfile.setOnClickListener(v -> {

            if (btneditProfile.getText().equals("Edit Profile")) {

                fName.setEnabled(true);
                lName.setEnabled(true);
                email.setEnabled(true);
                municipality.setEnabled(true);
                province.setEnabled(true);
                btneditProfile.setText("Save");

            } else if (btneditProfile.getText().equals("Save")) {

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
}