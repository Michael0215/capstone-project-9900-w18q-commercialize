package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.CollectionAdapter;
import com.example.comp9900_commercialize.adapters.FollowAdapter;
import com.example.comp9900_commercialize.adapters.ProfileRecipeAdapter;
import com.example.comp9900_commercialize.bean.Follow;
import com.example.comp9900_commercialize.bean.ItemFollow;
import com.example.comp9900_commercialize.databinding.ActivityFollowingBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FollowingActivity extends AppCompatActivity {
    private static final String TAG="FollowingActivity";
    private ActivityFollowingBinding binding;

    private List<String> myFollowList;
    Follow myFollow;
    private Preferences preferences;
//    private FirebaseUser user;
    private RecyclerView mList;
    private List<ItemFollow> mData;
    private FirebaseFirestore firebaseFirestore;
    private FollowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mList = this.findViewById(R.id.rcv_all_following);
        init();
        loadData();
        setListeners();
    }

    private void init() {
        preferences = new Preferences(getApplicationContext());
//        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    private void loadData() {

        //创建数据集合
        mData = new ArrayList<>();
        //创建模拟数据
        DocumentReference docRef = firebaseFirestore.collection("follow").document(preferences.getString(MacroDef.KEY_EMAIL));
        // retrieve all the post in the firestore's table 'posts'
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myFollow = documentSnapshot.toObject(Follow.class);
                if (myFollow != null) {
                    myFollowList = myFollow.followList;
                    if (!myFollowList.isEmpty()) {
                        CollectionReference cr_users = firebaseFirestore.collection("users");
                        // order the post in creating time order
                        Query query = cr_users.whereIn(FieldPath.documentId(), myFollowList);
//        Query query = posts.orderBy("recipePublishTime", Query.Direction.DESCENDING); //.whereEqualTo("recipeContributorEmail","767831805@qq.com");
                        query.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // retrieve all posts in the 'posts' table
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                ItemFollow follow_contributor = new ItemFollow();
//                                                Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();
                                                for (Map.Entry<String, Object> mapElement : document.getData().entrySet()){
                                                    // read the information in mapElement and set value for the object post
                                                    if (mapElement.getKey().equals("Name")){
                                                        follow_contributor.tv_contributor_name = mapElement.getValue().toString();
                                                    }
                                                    if (mapElement.getKey().equals("Avatar")){
                                                        if (mapElement.getValue() != null){
                                                            byte[] bytes_avatar = Base64.decode(mapElement.getValue().toString(), Base64.DEFAULT);
                                                            follow_contributor.avatar = BitmapFactory.decodeByteArray(bytes_avatar, 0, bytes_avatar.length);
                                                        } else {
                                                            @SuppressLint("ResourceType") InputStream img_avatar = getResources().openRawResource(R.drawable.default_avatar);
                                                            follow_contributor.avatar = BitmapFactory.decodeStream(img_avatar);
                                                        }
                                                    }
                                                }
                                                follow_contributor.id = document.getId();
                                                mData.add(follow_contributor);
                                            }
                                            showLinear();
                                        } else { // error handling
//                            Toast.makeText(CollectionActivity.this, mUser.toString(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(FollowingActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }
            }
        });

    }

    private void showLinear() {
        //准备布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器的方向
//        layoutManager.setOrientation(isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
//        layoutManager.setReverseLayout(isReverse);
        //设置布局管理器到RecyclerView里
        mList.setLayoutManager(layoutManager);
        //创建适配器
        adapter = new FollowAdapter(mData);
        //设置适配器
        mList.setAdapter(adapter);
        //初始化事件RecyclerView
        initListener();
    }

    private void initListener() {
        adapter.setOnItemClickListener(new FollowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                preferences.putString(MacroDef.KEY_CONTRIBUTOR_EMAIL, mData.get(position).id);
                startActivity(new Intent(getApplicationContext(), OtherProfileActivity.class));
            }
        });
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                onBackPressed());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }
}