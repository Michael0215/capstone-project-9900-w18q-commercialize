package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityGuestSearchBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuestSearchActivity extends AppCompatActivity {

    private Preferences preferences;
    private ActivityGuestSearchBinding binding;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.ibExplore.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), GuestMainActivity.class));
            finish();
        });
        binding.ibCreate.setOnClickListener(v ->
                Toast.makeText(GuestSearchActivity.this, "Please register a new account!", Toast.LENGTH_SHORT).show());
        binding.ibSubscribe.setOnClickListener(v ->
                Toast.makeText(GuestSearchActivity.this, "Please register a new account!", Toast.LENGTH_SHORT).show());
        binding.ibProfile.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), GuestProfileActivity.class));
            finish();
        });
        binding.btSearch.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By keywords");
            preferences.putString(MacroDef.KEY_SEARCH_CONTENT, binding.etSearchBar.getText().toString());
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType1.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Snack");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType2.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Breakfast");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType3.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Brunch");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType4.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Lunch");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType5.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Afternoon tea");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });
        binding.btSelectType6.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Dinner");
            startActivity(new Intent(getApplicationContext(), GuestSearchResultActivity.class));
        });

    }
}