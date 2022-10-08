package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private RadioButton radioButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    //create actions for palette
    private void setListeners() {
        binding.tvBackToLogin.setOnClickListener(v ->
                finish());
        binding.btRegister.setOnClickListener(v -> {
            if (checkInput()) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        FirebaseFirestore firestoreDatabase;
        firestoreDatabase = FirebaseFirestore.getInstance();
        int usertypeid = binding.radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(usertypeid);
        String type = radioButton.getText().toString().trim();
        String name = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password= binding.etPassword.getText().toString().trim();

        // store the account information into the user hashMap
        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("Type", type);
        user.put("E-mail", email);
        user.put("Password", password);
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        //using the API to store the input information into firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // table 'users' in firebase to store the information in the hashMap 'user'
                            firestoreDatabase.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully!\nNow you can login.", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                            // jump from RegisterActivity.java to LoginActivity.java
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }) // error handling if the registration is failed
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                        }
                                    });
                        }else{ // error handling if the registration is failed
                            Toast.makeText(RegisterActivity.this, "Registered failed, try email format again", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    // set rules for created email address, password and confirmed password
    public boolean checkInput(){
        String name = binding.etUsername.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confirm_password = binding.etConfirmPassword.getText().toString();
        if(name.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"User name cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"User E-mail cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"Password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"Confirmed password cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"Password input is inconsistent!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(RegisterActivity.this,"Please create a stronger password, your password should be at least 6 characters!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}