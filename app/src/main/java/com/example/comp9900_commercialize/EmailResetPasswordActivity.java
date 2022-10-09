package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comp9900_commercialize.databinding.ActivityEmailResetPasswordBinding;

public class EmailResetPasswordActivity extends AppCompatActivity {

    private ActivityEmailResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                finish());
        binding.tvSignIn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

}