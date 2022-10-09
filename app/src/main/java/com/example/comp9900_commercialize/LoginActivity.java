package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityLoginBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;;
    private Preferences preferences;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void setListeners(){
        binding.btLogin.setOnClickListener(v -> {
            if (isValidInput()) {
                login();
            }});
        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));
        binding.tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
        if(preferences.getBoolean(MacroDef.KEY_IS_SIGNED_IN)){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void login() {
        String email = binding.etEmail.getText().toString().trim();
        String pwd = binding.etPassword.getText().toString().trim();
        auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            preferences = new Preferences(getApplicationContext());
                            preferences.putBoolean(MacroDef.KEY_IS_SIGNED_IN, true);
                            preferences.putString(MacroDef.KEY_EMAIL, email);
                            firebaseFirestore.collection(MacroDef.KEY_COLLECTION_USERS)
                                    .whereEqualTo(MacroDef.KEY_EMAIL, email)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful() && task1.getResult() != null
                                                && task1.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                            preferences.putString(MacroDef.KEY_USERNAME, documentSnapshot.getString("Name"));
                                            preferences.putString(MacroDef.KEY_CONTACT, documentSnapshot.getString("Contact Detail"));
                                            preferences.putString(MacroDef.KEY_AVATAR, documentSnapshot.getString("Avatar"));
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    private boolean isValidInput() {
        if (binding.etEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (binding.etPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }

}