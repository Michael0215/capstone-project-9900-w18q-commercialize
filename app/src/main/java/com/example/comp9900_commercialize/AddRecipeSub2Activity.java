package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.comp9900_commercialize.bean.Ingredient;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.bean.Step;
import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub2Binding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.NonNull;


public class AddRecipeSub2Activity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddRecipeSub2Binding binding;
    private LinearLayout linearLayout;
    public static AddRecipeSub2Activity instance = null;
    private Preferences preferences;
    List<Step> stepList = new ArrayList<>();
    Recipe recipe;
    private String encodedImage;
    private ImageView imageView;
    private FirebaseFirestore firebaseFirestore;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUir = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUir);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            encodedImage = encodeImage(bitmap);
                            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        recipe = (Recipe) this.getIntent().getSerializableExtra("recipe");
        linearLayout = binding.llProcedures;
        init();
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            new AddRecipeSub1Activity().instance.finish();
            new AddRecipeActivity().instance.finish();
            finish();
        });
        binding.btPre.setOnClickListener(v ->
                onBackPressed());
        binding.btPublish.setOnClickListener(v -> {
            if(checkIfValidAndRead()){
                recipe.setRecipeStepList(stepList);
                recipe.setRecipeCommentsNum(0);
                recipe.setRecipeLikesNum(0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                recipe.setRecipePublishTime(simpleDateFormat.format(date));
                recipe.setRecipeContributorEmail(preferences.getString(MacroDef.KEY_EMAIL));
                recipe.setRecipeContributorName(preferences.getString(MacroDef.KEY_USERNAME));
                recipe.setRecipeContributorAvatar(preferences.getString(MacroDef.KEY_AVATAR));
                firebaseFirestore.collection("recipes").document(UUID.randomUUID().toString().replace("-", "")).set(recipe)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("Success");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                new AddRecipeSub1Activity().instance.finish();
                                new AddRecipeActivity().instance.finish();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("Error writing document" + e);
                            }
                        });


            }
        });
        binding.btAddProcedure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        addView();
    }

    private void addView() {
        View procedureView = getLayoutInflater().inflate(R.layout.row_add_procedure, null, false);
        EditText procedureDescription = (EditText)procedureView.findViewById(R.id.et_procedure_description);
        ImageView image = (ImageView) procedureView.findViewById(R.id.iv_procedure_photo);
        ImageView remove = (ImageView)procedureView.findViewById(R.id.iv_remove_procedure);


        image.setOnClickListener(v -> {
            imageView = image;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        remove.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                removeView(procedureView);
            }
        });
        linearLayout.addView(procedureView);
    }

    private void removeView(View view) {
        linearLayout.removeView(view);
    }

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AddRecipeSub1Activity.class));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    public boolean checkIfValidAndRead() {
        stepList.clear();
        boolean result = true;
        if (binding.spnClassification.getSelectedItemPosition()!=0){
        }else{
            result = false;
        }
        for(int i = 0; i < linearLayout.getChildCount(); i++){
            View procedureView = linearLayout.getChildAt(i);
            EditText procedureDescription = (EditText)procedureView.findViewById(R.id.et_procedure_description);
            ImageView procedurePhoto = (ImageView)procedureView.findViewById(R.id.iv_procedure_photo);
            Step step = new Step();
            if(!procedureDescription.getText().toString().equals("")){
                step.setStepDescription(procedureDescription.getText().toString());
            }else{
                result = false;
                break;
            }
            if(procedurePhoto != null){
                step.setEncodedImage(encodeImage(((BitmapDrawable) procedurePhoto.getDrawable()).getBitmap()));
            }else{
                result = false;
                break;
            }
            stepList.add(step);
        }
        if(stepList.size() == 0){
            result = false;
            showToast("Add Procedures First!");
        }else if(!result){
            showToast("Input cannot be empty!");
        }
        return result;
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 260;
        int previewHeight = 260;
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}