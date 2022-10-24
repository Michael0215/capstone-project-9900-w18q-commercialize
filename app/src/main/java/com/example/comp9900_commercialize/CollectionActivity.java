package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.comp9900_commercialize.bean.Collection;
import com.example.comp9900_commercialize.databinding.ActivityCollectionBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;


public class CollectionActivity extends AppCompatActivity {
    private static final String TAG="CollectionActivity";
    private ActivityCollectionBinding binding;
    ListView myCollection;
    List<Collection> myCollectionList;
    private FirebaseUser user;
    private String userEmail;
    private FirebaseFirestore db;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }
    private void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        preferences = new Preferences(getApplicationContext());
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                onBackPressed());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        userEmail=user.getEmail();
        //读取userEmail中所有collection
        DocumentReference docRef = db.collection("collection").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //email-> 一个arraylist 里面存recipeId
        //adapter显示出来//
    }
}