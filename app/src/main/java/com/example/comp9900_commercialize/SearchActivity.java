package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.example.comp9900_commercialize.databinding.ActivitySearchBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;

public class SearchActivity extends AppCompatActivity {

    private Preferences preferences;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void setListeners(){
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
                });
        binding.ibSubscribe.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SubscribeActivity.class));
                    finish();
                });
        binding.ibProfile.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                });
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
        binding.btSearch.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_CONTENT, binding.etSearchBar.getText().toString());
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
    }

}