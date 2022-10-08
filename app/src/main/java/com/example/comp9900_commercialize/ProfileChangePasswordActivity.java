package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.comp9900_commercialize.databinding.ActivityProfileChangePasswordBinding;

public class ProfileChangePasswordActivity extends AppCompatActivity {

    private ActivityProfileChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                finish());
    }

}