package com.genderanddevelopmentprimer.app;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {
    ImageView btnGoogle;
    TextInputEditText loginEmail, loginPass;
    Button btnLogin;
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

        btnsignUp = findViewById(R.id.btn_signUp);
        btnForgotPass = findViewById(R.id.btn_forgotPass);

        btnGoogle = findViewById(R.id.btn_google);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loading = findViewById(R.id.login_loading);

        mainLayout = findViewById(R.id.login_form);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount accountCheck = GoogleSignIn.getLastSignedInAccount(this);
        if (accountCheck != null) {
            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
            finish();
        }

        //account logged in
        if (fAuth.getCurrentUser() != null) {
            FirebaseUser getUser = fAuth.getCurrentUser();
            if (getUser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                finish();
            }
        }

        //Login Button
        btnLogin.setOnClickListener(v -> {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            loginEmail.clearFocus();
            loginPass.clearFocus();

            String varEmail = Objects.requireNonNull(loginEmail.getText()).toString().trim();
            String varPass = Objects.requireNonNull(loginPass.getText()).toString().trim();

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
                        unverified++;

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
                    loginPass.setText(null);
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

        //Google Sign In
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    // Signed in successfully, start new activity
                    fAuth.signInWithEmailAndPassword(account.getEmail(), account.getId()).addOnCompleteListener(this, task13 -> {
                        if (task13.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                            finish();
                        }else {
                            // Define the options for the sex and role drop-down boxes
                            String[] sexOptions = {"Male", "Female"};
                            String[] roleOptions = {"Student", "Teacher"};

                            // Build the alert dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setTitle("Fill up the information:");

                            // Create the layout for the dialog box
                            LinearLayout layout = new LinearLayout(Login.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(30, 50, 30, 0);

                            // Create the role drop-down box
                            TextView roleLabel = new TextView(Login.this);
                            roleLabel.setText("Role:");
                            layout.addView(roleLabel);

                            Spinner roleSpinner = new Spinner(Login.this);
                            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(Login.this, android.R.layout.simple_spinner_item, roleOptions);
                            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            roleSpinner.setAdapter(roleAdapter);
                            layout.addView(roleSpinner);

                            // Create the sex drop-down box
                            TextView sexLabel = new TextView(Login.this);
                            sexLabel.setText("Sex:");
                            layout.addView(sexLabel);

                            Spinner sexSpinner = new Spinner(Login.this);
                            ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(Login.this, android.R.layout.simple_spinner_item, sexOptions);
                            sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sexSpinner.setAdapter(sexAdapter);
                            layout.addView(sexSpinner);

                            TextInputLayout muniLayout = new TextInputLayout(Login.this);
                            muniLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
                            muniLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            TextInputEditText municipality = new TextInputEditText(Login.this);
                            municipality.setHint("Municipality");
                            municipality.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            muniLayout.addView(municipality);

                            TextInputLayout proviLayout = new TextInputLayout(Login.this);
                            proviLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
                            proviLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            TextInputEditText province = new TextInputEditText(Login.this);
                            province.setHint("Province");
                            province.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            proviLayout.addView(province);

                            layout.addView(muniLayout);
                            layout.addView(proviLayout);

                            // Set the layout for the dialog box
                            builder.setView(layout);

                            // Set the action buttons for the dialog box
                            builder.setPositiveButton("Done", (dialog, which) -> {
                                // Retrieve the selected sex and role options
                                String selectedSex = sexOptions[sexSpinner.getSelectedItemPosition()];
                                String selectedRole = roleOptions[roleSpinner.getSelectedItemPosition()];
                                String municipalityVal = Objects.requireNonNull(municipality.getText()).toString();
                                String provinceVal = Objects.requireNonNull(province.getText()).toString();

                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(account.getEmail(), account.getId()).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("firstName", account.getGivenName());
                                        user.put("lastName", account.getFamilyName());
                                        user.put("email", account.getEmail());
                                        user.put("sex", selectedSex);
                                        user.put("userType", selectedRole);
                                        user.put("municipality", municipalityVal);
                                        user.put("province", provinceVal);
                                        documentReference1.set(user).addOnSuccessListener(unused -> {
                                            Toast.makeText(Login.this, "User Created!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                                            finish();
                                        });
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            });
                            builder.setNegativeButton("Cancel", (dialog, which) -> googleSignInClient.revokeAccess().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    Toast.makeText(Login.this, "Sign In Cancelled.", Toast.LENGTH_SHORT).show();
                                }
                            }));
                            // Show the dialog box
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                } catch (ApiException e) {
                    Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
                }
            }
        });
        btnGoogle.setOnClickListener(v -> someActivityResultLauncher.launch(new Intent(googleSignInClient.getSignInIntent())));
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