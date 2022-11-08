package com.example.comp9900_commercialize;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.CollectionAdapter;
import com.example.comp9900_commercialize.adapters.CommentAdapter;
import com.example.comp9900_commercialize.adapters.FollowAdapter;
import com.example.comp9900_commercialize.adapters.SubscribeAdapter;
import com.example.comp9900_commercialize.bean.Collection;
import com.example.comp9900_commercialize.bean.Comment;
import com.example.comp9900_commercialize.bean.Follow;
import com.example.comp9900_commercialize.bean.ItemCollection;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.bean.itemComment;
import com.example.comp9900_commercialize.databinding.ActivityCommentDetailBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentDetailBinding binding;
    private static final String TAG = "CommentActivity";
    //private List<itemComment> myCommentList;
    Comment myComment;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RecyclerView RCVlist;
    private Preferences preferences;
    private DatabaseReference reference;
    private List<itemComment> mData;
    String recipeId, publishEmail,username,date,avatar;
    String commentsInput;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RCVlist=binding.rcvAllComment;
        init();
        setListener();
        loadData();
    }

    private void loadData() {
        mData=new ArrayList<itemComment>();
        itemComment iC=new itemComment();
        //获取recipeId的的reference
        DocumentReference docRef=db.collection("comment").document(recipeId);
        //加载数据然后布局
        //    private List<itemComment> myCommentList;
        //    Comment myComment;
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myComment=documentSnapshot.toObject(Comment.class);
                if(myComment != null) {
                    mData = myComment.commentList;
                    System.out.println(mData.size());
                }
                showLinear();
            }
        });
    }
//d

    private void showLinear() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RCVlist.setLayoutManager(layoutManager);
        //创建适配器
        adapter = new CommentAdapter(mData);
        //设置适配器
        RCVlist.setAdapter(adapter);
        //initListener();
    }

//    private void initListener() {
//        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                preferences.putString(MacroDef.KEY_RECIPE_ID, mData.get(position).recipeId);
//                preferences.putBoolean(MacroDef.KEY_MODE_CREATE, false);
//                startActivity(new Intent(getApplicationContext(), RecipeDetailActivity.class));
//            }
//        }) ;
//    }


    private void CommentMainFunc() {
        mData=new ArrayList<>();
        itemComment newComment=new itemComment();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        newComment.setDate(simpleDateFormat.format(date));
        newComment.setComment(commentsInput);
        newComment.setUsername(preferences.getString(MacroDef.KEY_USERNAME));
        newComment.setAvatar(preferences.getString(MacroDef.KEY_AVATAR));
        DocumentReference docRef = db.collection("comment").document(recipeId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myComment = documentSnapshot.toObject(Comment.class);
                if (myComment!=null){
                    if(myComment.commentList != null){
                        myComment.commentList.add(newComment);
                        showToast("You comment something");
                    }
                }else{
                    myComment=new Comment(newComment);
                    showToast("You comment something 1st");
                    binding.ivAddComment.setText("");
                }
                db.collection("comment").document(recipeId).set(myComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"successfully written!");
                    }
                });
            }
        });
    }

    private void setListener() {
        binding.btCancel.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsInput = binding.ivAddComment.getText().toString();
                if (commentsInput.equals("")) {
                    showToast("You can't enter empty comment!");
                } else {
                    CommentMainFunc();
                }
            }
        });
    }



    private void init() {
        preferences = new Preferences(getApplicationContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        recipeId = preferences.getString(MacroDef.KEY_RECIPE_ID);
        publishEmail = user.getEmail();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}