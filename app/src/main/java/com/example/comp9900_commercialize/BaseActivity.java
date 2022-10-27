package com.example.comp9900_commercialize;

import  android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google. firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences preferenceManager = new Preferences(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(MacroDef.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(MacroDef.KEY_EMAIL));
    }

    //offline
    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(MacroDef.KEY_AVAILABILITY, 0);
    }

    //online
    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(MacroDef.KEY_AVAILABILITY, 1);
    }
}
