package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.comp9900_commercialize.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                finish());
        binding.btEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));
        binding.btChangePassword.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ProfileChangePasswordActivity.class)));
    }
}