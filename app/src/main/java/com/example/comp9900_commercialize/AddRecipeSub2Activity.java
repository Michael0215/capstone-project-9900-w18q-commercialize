package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub2Binding;



public class AddRecipeSub2Activity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddRecipeSub2Binding binding;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        linearLayout = binding.llProcedures;
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.btPre.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeSub1Activity.class)));
        binding.btPublish.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
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
}