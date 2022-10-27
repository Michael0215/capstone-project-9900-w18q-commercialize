package com.example.comp9900_commercialize;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.ChatAdapter;
import com.example.comp9900_commercialize.databinding.ActivityLiveChatBinding;
import com.example.comp9900_commercialize.models.ChatMessage;
import com.example.comp9900_commercialize.models.User;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.List;
import java.util.Objects;

public class LiveChatActivity extends BaseActivity {

    // This activity handles what happens in real-time chat.
    // Need an ActivityLiveChatBinding object to set content view.
    // Need an User object to store info of the receiver.
    // Need a list to store messages.
    // Need a ChatAdapter to fill message blobs with a real message.
    // Need a String variable to store the ID of certain conversation.
    private ActivityLiveChatBinding binding;
    private User receiveUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private Preferences preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    // Function sending a message.
    private void sendMessage() {

        // Package all attributes of a message in a hash map and add it to database.
        HashMap<String, Object> message = new HashMap<>();
        message.put(MacroDef.KEY_SENDER_EMAIL, preferenceManager.getString(MacroDef.KEY_EMAIL));
        message.put(MacroDef.KEY_RECEIVER_EMAIL, receiveUser.email);
        message.put(MacroDef.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(MacroDef.KEY_TIMESTAMP, new Date());
        database.collection(MacroDef.KEY_COLLECTION_CHAT).add(message);
        // If it is the first conversation between two users, then add a new conversation record to 'conversation' table.
        if (conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(MacroDef.KEY_SENDER_EMAIL, preferenceManager.getString(MacroDef.KEY_EMAIL));
            conversion.put(MacroDef.KEY_SENDER_NAME, preferenceManager.getString(MacroDef.KEY_USERNAME));
            conversion.put(MacroDef.KEY_SENDER_IMAGE, preferenceManager.getString(MacroDef.KEY_AVATAR));
            conversion.put(MacroDef.KEY_RECEIVER_EMAIL, receiveUser.email);
            conversion.put(MacroDef.KEY_RECEIVER_NAME, receiveUser.name);
            conversion.put(MacroDef.KEY_RECEIVER_IMAGE, receiveUser.avatar);
            conversion.put(MacroDef.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(MacroDef.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        // Fill this blank message blob with the content of message.
        binding.inputMessage.setText(null);
    }

    private void listenAvailabilityOfReceiver() {
        database.collection(MacroDef.KEY_COLLECTION_USERS).document(
                receiveUser.email
        ).addSnapshotListener(LiveChatActivity.this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                if (value.getLong(MacroDef.KEY_AVAILABILITY) != null) {
                    int availability = Objects.requireNonNull(
                           value.getLong(MacroDef.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
            }
            if (isReceiverAvailable) {
                binding.textAvailability.setVisibility(View.VISIBLE);
            } else {
                binding.textAvailability.setVisibility(View.GONE);
            }
        });
    }

    // Find all messages between two certain users for further displaying on the screen.
    private void listenMessages() {
        database.collection(MacroDef.KEY_COLLECTION_CHAT)
                .whereEqualTo(MacroDef.KEY_SENDER_EMAIL, preferenceManager.getString(MacroDef.KEY_EMAIL))
                .whereEqualTo(MacroDef.KEY_RECEIVER_EMAIL, receiveUser.email)
                .addSnapshotListener(eventListener);
        database.collection(MacroDef.KEY_COLLECTION_CHAT)
                .whereEqualTo(MacroDef.KEY_SENDER_EMAIL, receiveUser.email)
                .whereEqualTo(MacroDef.KEY_RECEIVER_EMAIL, preferenceManager.getString(MacroDef.KEY_EMAIL))
                .addSnapshotListener(eventListener);
    }

    // Initialization
    private void init() {
        preferenceManager = new Preferences(getApplicationContext());
        chatMessages = new ArrayList<>();
        Bitmap sender_avatar;
        if (preferenceManager.getString(MacroDef.KEY_AVATAR) != null){
            sender_avatar = getBitmapFromEncodedString(preferenceManager.getString(MacroDef.KEY_AVATAR));
        } else {
            @SuppressLint("ResourceType") InputStream img_avatar = getResources().openRawResource(R.drawable.default_avatar);
            sender_avatar =  BitmapFactory.decodeStream(img_avatar);
        }

        //If Receiver has null-value avatar, just use the local avatar
        if (getBitmapFromEncodedString(receiveUser.avatar) == null) {
            Bitmap bm_default_avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            chatAdapter = new ChatAdapter(
                    chatMessages,//list
                    preferenceManager.getString(MacroDef.KEY_EMAIL),//string
                    bm_default_avatar,//bitmap
                    sender_avatar
            );
        } else {
            chatAdapter = new ChatAdapter(
                    chatMessages,//list
                    preferenceManager.getString(MacroDef.KEY_EMAIL),//string
                    getBitmapFromEncodedString(receiveUser.avatar),//bitmap
                    sender_avatar
            );
        }
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    // Set event listener to monitor changes in table 'messages'.
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        // Add new message records to the table.
        if(value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail = documentChange.getDocument().getString(MacroDef.KEY_SENDER_EMAIL);
                    chatMessage.receiverEmail = documentChange.getDocument().getString(MacroDef.KEY_RECEIVER_EMAIL);
                    chatMessage.message = documentChange.getDocument().getString(MacroDef.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(MacroDef.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(MacroDef.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            // Sort records by creating time.
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            // If no existing messages between two users, then monitor whether this situation changes.
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                // If there exists messages, then monitor whether new records are inserted.
                // If inserted, scroll to the position of it.
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionId == null){
            checkForConversion();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    // Get the info of receiver from previous page and set the title of this page to be receiver's E-mail.
    private void loadReceiverDetails() {
        receiveUser = (User) getIntent().getSerializableExtra(MacroDef.KEY_USER);
        binding.textName.setText(receiveUser.name);
    }

    // Set listeners for back button and sending button.
    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    // Add a new conversation (which means two users has never talked with each other before this) to the table of 'conversations'.
    private void addConversion(HashMap<String, Object> conversion){
        database.collection(MacroDef.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    // Update existing conversation with the content and creating time of the last message.
    // This is used to display recent chat.
    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(MacroDef.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                MacroDef.KEY_LAST_MESSAGE, message,
                MacroDef.KEY_TIMESTAMP, new Date()
        );
    }

    // Get conversation info.
    private void checkForConversion() {
        if(chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(MacroDef.KEY_EMAIL),
                    receiveUser.email
            );
            checkForConversionRemotely(
                    receiveUser.email,
                    preferenceManager.getString(MacroDef.KEY_EMAIL)
            );
        }
    }

    // Request database to get records of conversations.
    private void checkForConversionRemotely(String senderEmail, String receiverEmail){
        database.collection(MacroDef.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(MacroDef.KEY_SENDER_EMAIL, senderEmail)
                .whereEqualTo(MacroDef.KEY_RECEIVER_EMAIL, receiverEmail)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    // Get E-mails of all other users except for the user him/herself.
    // It is used to list all available users in 'Select user' when starting to chat.
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}