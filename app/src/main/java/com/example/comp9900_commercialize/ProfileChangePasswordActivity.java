package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityLoginBinding;
import com.example.comp9900_commercialize.databinding.ActivityProfileChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
public class ProfileChangePasswordActivity extends AppCompatActivity {
    private ActivityProfileChangePasswordBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile_change_password);
        binding = ActivityProfileChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.btSet.setOnClickListener(v -> {
            EditText newPassword = binding.etNewPassword;
            EditText confirmPassword = binding.etConfirmPassword;
            if (checkSame(newPassword,confirmPassword)){
                if (checkValid(newPassword)){
                    DocumentReference Ref = db.collection("users").document(user.getEmail());
                    Ref
                            .update("Password", newPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(getApplicationContext(), "Reset1 succeed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    user.updatePassword(newPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Reset succeed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Fail, the password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Fail, two passwords are different", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btCancel.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
    }

    public boolean checkValid(EditText newPassword) {
        String password = newPassword.getText().toString();
        if (password.length() < 6) {
            return false;
        }
        return true;
    }

    public boolean checkSame(EditText newPassword, EditText confirmPassword) {
        String newPassword_s = newPassword.getText().toString();
        String confirmPassword_s = confirmPassword.getText().toString();
        if (newPassword_s.equals(confirmPassword_s)) {
            return true;
        }
        return false;


    }
}
