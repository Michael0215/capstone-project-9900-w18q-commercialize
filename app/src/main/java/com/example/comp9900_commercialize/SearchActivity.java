package com.example.comp9900_commercialize;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.comp9900_commercialize.bean.Follow;
import com.example.comp9900_commercialize.bean.LastFeed;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivitySearchBinding;
import com.example.comp9900_commercialize.models.ChatMessage;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

import io.reactivex.rxjava3.annotations.NonNull;

public class SearchActivity extends AppCompatActivity {

    private Preferences preferences;
    private ActivitySearchBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private Follow follow;

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
        firebaseFirestore = FirebaseFirestore.getInstance();
        getFollowList();
    }

    private void setListeners(){
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
            preferences.putBoolean(MacroDef.KEY_MODE_CREATE, true);
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
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By keywords");
            preferences.putString(MacroDef.KEY_SEARCH_CONTENT, binding.etSearchBar.getText().toString());
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType1.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Snack");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType2.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Breakfast");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType3.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Dessert");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType4.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Lunch");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType5.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Afternoon tea");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });
        binding.btSelectType6.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_SEARCH_MODE, "By type");
            preferences.putString(MacroDef.KEY_SEARCH_TYPE, "Dinner");
            startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
        });

    }

    private void getFollowList() {
        DocumentReference docRef = firebaseFirestore.collection("follow").document(preferences.getString(MacroDef.KEY_EMAIL));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        follow = document.toObject(Follow.class);
                        listenUpdates();
                    }
                }
            }
        });
    }

    private void listenUpdates(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(follow != null && follow.followList != null && !follow.followList.isEmpty()){
            firebaseFirestore.collection("recipes")
                    .whereIn("recipeContributorEmail", follow.followList)
                    .orderBy("recipePublishTime", Query.Direction.DESCENDING)
                    .addSnapshotListener(eventListener);
        }
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            DocumentReference docRef = firebaseFirestore.collection("lastFeed").document(preferences.getString(MacroDef.KEY_EMAIL));
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    LastFeed lastFeed = documentSnapshot.toObject(LastFeed.class);
                    if(lastFeed == null){
                        binding.redDot.setVisibility(View.VISIBLE);
                        LastFeed createLastFeed = new LastFeed();
                        createLastFeed.latestTime = "1970-01-01 00:00:00";
                        firebaseFirestore.collection("lastFeed").document(preferences.getString(MacroDef.KEY_EMAIL))
                                .set(createLastFeed)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@io.reactivex.rxjava3.annotations.NonNull Exception e) {
                                    }
                                });
                    }
                    else{
                        if(value.getDocumentChanges().get(0).getDocument().toObject(Recipe.class).recipePublishTime
                                .compareTo(lastFeed.latestTime) > 0)
                            binding.redDot.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    };

}