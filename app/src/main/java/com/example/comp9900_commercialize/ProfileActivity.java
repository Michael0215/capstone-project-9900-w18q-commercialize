package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;


import com.example.comp9900_commercialize.databinding.ActivityProfileBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Preferences preferences;

    public static ProfileActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        init();
        loadData();
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void loadData(){
        binding.tvUserName.setText(preferences.getString(MacroDef.KEY_USERNAME));
        binding.tvContactDetail.setText(preferences.getString(MacroDef.KEY_CONTACT));
        if (preferences.getString(MacroDef.KEY_AVATAR) != null){
            byte[] bytes = Base64.decode(preferences.getString(MacroDef.KEY_AVATAR), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.rivUserPhoto.setImageBitmap(bitmap);
        }
    }

    private void setListeners(){
        binding.btSetting.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SettingActivity.class)));
        binding.ivNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.ivCollection.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CollectionActivity.class)));
        binding.ivFollowing.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), FollowingActivity.class)));
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibSearch.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SearchActivity.class)));
        binding.ibCreate.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class)));
        binding.ibSubscribe.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SubscribeActivity.class)));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

}