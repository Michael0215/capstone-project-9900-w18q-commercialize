package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityGuestProfileBinding;

public class GuestProfileActivity extends AppCompatActivity {

    private ActivityGuestProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.tvRegister.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
        });
        binding.tvLogIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
        binding.ibExplore.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), GuestMainActivity.class));
            finish();
        });
        binding.ibSearch.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), GuestSearchActivity.class));
            finish();
        });
        binding.ibCreate.setOnClickListener(v ->
                Toast.makeText(GuestProfileActivity.this, "Please register a new account!", Toast.LENGTH_SHORT).show());
        binding.ibSubscribe.setOnClickListener(v ->
                Toast.makeText(GuestProfileActivity.this, "Please register a new account!", Toast.LENGTH_SHORT).show());
    }
}