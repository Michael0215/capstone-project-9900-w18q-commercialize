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
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
public class ProfileChangePasswordActivity extends AppCompatActivity {
    private ActivityProfileChangePasswordBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Preferences preferences;
    private FirebaseAuth auth;
    private String realPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        DocumentReference Ref = db.collection("users").document(user.getEmail());
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map doc = document.getData();
                        realPassword = (String) doc.get("Password");
                        //Toast.makeText(getApplicationContext(), realPassword, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setListeners(){
        binding.btSet.setOnClickListener(v -> {
            EditText oldPassword = binding.etPassword;
            EditText newPassword = binding.etNewPassword;
            EditText confirmPassword = binding.etConfirmPassword;
            /*DocumentReference Ref = db.collection("users").document(user.getEmail());
            Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map doc = document.getData();
                            realPassword = (String) doc.get("Password");
                            Toast.makeText(getApplicationContext(), realPassword, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });*/

            if (realPassword.equals(oldPassword.getText().toString())) {
            //if (getRealPassword().equals(oldPassword.getText().toString())) {
                if (checkSame(newPassword, confirmPassword)) {
                    if (checkValid(newPassword)) {
                        DocumentReference Ref = db.collection("users").document(user.getEmail());
                        Ref
                                .update("Password", newPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Reset succeed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        user.updatePassword(newPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            signOut();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail, the password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fail, two passwords are different", Toast.LENGTH_SHORT).show();
                }
            } else{
                Toast.makeText(getApplicationContext(), "Fail, the old password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btCancel.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SettingActivity.class)));
    }

    /*public String getRealPassword(){
        //public String realPassword;
        DocumentReference Ref = db.collection("users").document(user.getEmail());
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map doc = document.getData();
                        realPassword = (String) doc.get("Password");
                    }
                }
            }
        });
        return realPassword;
    }*/
    public boolean check(){
        return false;
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

    private void signOut(){
        preferences = new Preferences(getApplicationContext());
        preferences.putBoolean(MacroDef.KEY_IS_SIGNED_IN, false);
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        new ProfileActivity().instance.finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
