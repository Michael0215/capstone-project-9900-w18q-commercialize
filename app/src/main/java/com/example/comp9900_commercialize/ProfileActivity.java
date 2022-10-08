package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;


import com.example.comp9900_commercialize.databinding.ActivityProfileBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }


    private void setListeners(){
//        binding.rivUserPhoto.setOnClickListener(v ->
//        {
//            Intent intent = new Intent(Intent.ACTION_PICK, null);
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//            startActivityForResult(intent, 1);
//            });
        binding.btSetting.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SettingActivity.class)));
        binding.ivNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.ivCollection.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CollectionActivity.class)));
        binding.ivFollowing.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), FollowingActivity.class)));
        binding.ibExplore.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.ibSearch.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SearchActivity.class)));
        binding.ibCreate.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CollectionActivity.class)));
        binding.ibSubscribe.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SubscribeActivity.class)));
        binding.ibProfile.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
    }

}