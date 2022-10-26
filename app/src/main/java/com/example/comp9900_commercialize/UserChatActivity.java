package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.comp9900_commercialize.adapters.UsersAdapter;
import com.example.comp9900_commercialize.databinding.ActivityUserChatBinding;
import com.example.comp9900_commercialize.listeners.UserListener;
import com.example.comp9900_commercialize.models.User;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserChatActivity extends AppCompatActivity implements UserListener {

    // This activity lists all available users you can chat with. And you can choose one of them to chat.
    private ActivityUserChatBinding binding;
    private Preferences preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new Preferences(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    // Request any other users in 'users' table except for the users him/herself and store their info in an User type object.
    // Then use these info to fill item containers on the screen by setting adapter.
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(MacroDef.KEY_KEY_LIVECHATTABLE)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserEmail = preferenceManager.getString(MacroDef.KEY_EMAIL);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserEmail.equals(queryDocumentSnapshot.getString(MacroDef.KEY_EMAIL))){
                                continue;
                            }
                            User user = new User();
                            if (queryDocumentSnapshot.getString(MacroDef.KEY_LIVECHATAVATAR) != null) {
                                user.avatar = queryDocumentSnapshot.getString(MacroDef.KEY_LIVECHATAVATAR);
                            }
                            user.name = queryDocumentSnapshot.getString(MacroDef.KEY_LIVECHATNAME);
                            user.type = queryDocumentSnapshot.getString(MacroDef.KEY_LIVECHATTYPE);
                            user.email = queryDocumentSnapshot.getString(MacroDef.KEY_EMAIL);
                            users.add(user);
                        }
                        if(users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    // Animation of a progress bar killing time when loading.
    private void loading(Boolean isLoading) {
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Set the click listener, enter the chat page with a certain user when clicking.
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), LiveChatActivity.class);
        intent.putExtra(MacroDef.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}