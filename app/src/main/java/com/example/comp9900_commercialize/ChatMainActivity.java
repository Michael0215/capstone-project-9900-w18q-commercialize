package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.comp9900_commercialize.listeners.ConversionListener;
import com.example.comp9900_commercialize.databinding.ActivityChatMainBinding;
import com.example.comp9900_commercialize.models.User;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;


public class ChatMainActivity extends AppCompatActivity implements ConversionListener {

    private ActivityChatMainBinding binding;
    private Preferences preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new Preferences(getApplicationContext());
//        init();
        loadUserDetails();
        setListeners();
//        listenConversations();
    }

    //    private void init() {
//        // Initialize the adapter and set the view of this page.
//        conversations = new ArrayList<>();
//        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
//        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
//        // Connect to Firestore database.
//        database = FirebaseFirestore.getInstance();
//    }

    // Set listeners for two buttons on this page.
    private void setListeners() {
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserChatActivity.class)));
        binding.imageBack.setOnClickListener(v ->
                onBackPressed());
    }


    // Load the E-mail of current user for further displaying on the page.
    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(MacroDef.KEY_USERNAME));
        if (preferenceManager.getString(MacroDef.KEY_AVATAR) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(MacroDef.KEY_AVATAR), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
        }

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // The function gets the record of the info of last chat between two users from Firestore.
    private void listenConversations() {

    }


    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), LiveChatActivity.class);
        intent.putExtra(MacroDef.KEY_USER, user);
        startActivity(intent);
    }
}