package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.comp9900_commercialize.databinding.ActivitySettingBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private Preferences preferences;
    private FirebaseAuth auth;;

    public static SettingActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                onBackPressed());
        binding.btEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));
        binding.btChangePassword.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ProfileChangePasswordActivity.class)));
        binding.btLogOut.setOnClickListener(v -> signOut());
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