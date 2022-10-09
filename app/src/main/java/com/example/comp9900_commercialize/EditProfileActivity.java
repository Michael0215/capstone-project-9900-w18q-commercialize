package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
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
import android.view.View;
import android.widget.Toast;

import com.example.comp9900_commercialize.databinding.ActivityEditProfileBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private String encodedImage;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void loadData(){
        if (preferences.getString(MacroDef.KEY_AVATAR) != null){
            byte[] bytes = Base64.decode(preferences.getString(MacroDef.KEY_AVATAR), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.ivUserPhoto.setImageBitmap(bitmap);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUir = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUir);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.ivUserPhoto.setImageBitmap(bitmap);
                            binding.tvUserPhoto.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    private void save(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("users").document(email)
                .update("Avatar", encodedImage,
                        "Name", binding.etUserName.getText().toString().trim(),
                        "Contact Detail", binding.etContact.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        preferences = new Preferences(getApplicationContext());
                        preferences.putString(MacroDef.KEY_AVATAR, encodedImage);
                        preferences.putString(MacroDef.KEY_USERNAME, binding.etUserName.getText().toString().trim());
                        preferences.putString(MacroDef.KEY_CONTACT, binding.etContact.getText().toString().trim());
                        Toast.makeText(EditProfileActivity.this, "Update Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        new ProfileActivity().instance.finish();
                        new SettingActivity().instance.finish();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Update failed, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                onBackPressed());
        binding.ivUserPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
                });
        binding.btSave.setOnClickListener(v ->{
            if(isValidInput()){
                save();
            }
        });
    }

    private boolean isValidInput(){
        if (binding.etUserName.getText().toString().trim().isEmpty()
                || binding.etContact.getText().toString().trim().isEmpty()){
            showToast("Input cannot be empty");
            return false;
        } else{
            return true;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }
}