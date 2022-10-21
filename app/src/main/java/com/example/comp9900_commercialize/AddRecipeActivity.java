package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.example.comp9900_commercialize.MainActivity;
import com.example.comp9900_commercialize.NoticeActivity;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityAddRecipeBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class AddRecipeActivity extends AppCompatActivity {
    private ActivityAddRecipeBinding binding;
    private Preferences preferences;
    public static AddRecipeActivity instance = null;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        init();
        setListeners();
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
                            binding.ivAddCoverImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->  {
            if(new AddRecipeSub2Activity().instance != null){
                new AddRecipeSub2Activity().instance.finish();
            }
            if(new AddRecipeSub1Activity().instance != null){
                new AddRecipeSub1Activity().instance.finish();
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        binding.btNext.setOnClickListener(v -> {
                 if(isValidInput()){
//                     preferences.putString(MacroDef.KEY_RECIPE_NAME, binding.etRecipeName.getText().toString().trim());
//                     preferences.putString(MacroDef.KEY_RECIPE_DESCRIPTION, binding.etRecipeDescription.getText().toString().trim());
                     Recipe recipe = new Recipe();
                     recipe.setRecipeName(binding.etRecipeDescription.getText().toString().trim());
                     recipe.setRecipeDescription(binding.etRecipeName.getText().toString().trim());
                     recipe.setRecipeCover(encodedImage);
                     Intent intent = new Intent(AddRecipeActivity.this, AddRecipeSub1Activity.class);
                     Bundle bundle = new Bundle();
                     bundle.putSerializable("recipe", recipe);
                     intent.putExtras(bundle);
                     startActivity(intent);
                 }
                });

        binding.ivAddCoverImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    private boolean isValidInput() {
        if (binding.etRecipeName.getText().toString().trim().isEmpty()) {
            showToast("Enter a recipe name");
            return false;
        } else if (binding.etRecipeDescription.getText().toString().trim().isEmpty()) {
            showToast("Enter a description");
            return false;
        } else {
            return true;
        }
    }

    public void onBackPressed() {
        if(new AddRecipeSub2Activity().instance != null){
            new AddRecipeSub2Activity().instance.finish();
        }
        if(new AddRecipeSub1Activity().instance != null){
            new AddRecipeSub1Activity().instance.finish();
        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}