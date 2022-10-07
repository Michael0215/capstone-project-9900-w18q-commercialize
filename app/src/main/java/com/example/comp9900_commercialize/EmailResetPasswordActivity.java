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

public class EmailResetPasswordActivity extends AppCompatActivity {
    private EditText newPassword,confirmPassword;
    private FirebaseAuth firebaseAuth;
    private Button set;
    private ImageView BackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_password);

        firebaseAuth = FirebaseAuth.getInstance();
        newPassword = findViewById(R.id.et_new_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        Button set = findViewById(R.id.bt_set);
        ImageView BackToLogin = findViewById(R.id.bt_cancel);

        BackToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.bt_cancel) {
                    // jump from RegisterActivity.java to LoginActivity.java
                    Intent intent = new Intent(EmailResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        set.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkSame(newPassword,confirmPassword)){
                    if (checkValid(newPassword)){
                        user.updatePassword(newPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EmailResetPasswordActivity.this, "Reset succeed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(EmailResetPasswordActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(EmailResetPasswordActivity.this, "Fail, the password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                    /*user.updatePassword(newPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EmailResetPasswordActivity.this, "Reset succeed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EmailResetPasswordActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });*/
                }
                else{
                    Toast.makeText(EmailResetPasswordActivity.this, "Fail, two passwords are different", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public boolean checkValid(EditText newPassword){
        String password = newPassword.getText().toString();
        if (password.length() < 6){
            return false;
        }
        return true;
    }

    public boolean checkSame(EditText newPassword,EditText confirmPassword){
        String newPassword_s = newPassword.getText().toString();
        String confirmPassword_s = confirmPassword.getText().toString();
        if (newPassword_s.equals(confirmPassword_s)){
            return true;
        }
        return false;


    }


}