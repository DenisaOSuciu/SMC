package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.upt.cti.smc.adapter.RecentConversationsAdapter;
import com.upt.cti.smc.databinding.ActivityChatBinding;
import com.upt.cti.smc.listeners.ConversationListener;
import com.upt.cti.smc.model.ChatMessage;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ConversationListener {

    private ActivityChatBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter adapter;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        preferenceManager =new PreferenceManager(getApplicationContext());
        loadUserInformations();
        setContentView(binding.getRoot());
        init();
        getToken();
        setListeners();
        listenConversations();
    }

    public void setListeners(){
        binding.plusBtn.setOnClickListener( v -> startActivity(new Intent(getApplicationContext(), UsersDisplayActivity.class)));
    }

    private void init(){
        conversations=new ArrayList<>();
        adapter =new RecentConversationsAdapter(conversations, this);
        binding.conversations.setAdapter(adapter);
        database =FirebaseFirestore.getInstance();
    }


    @SuppressLint("SetTextI18n")
    private void loadUserInformations(){
        binding.nameText.setText(preferenceManager.getString(Constants.KEY_NAME)+ " " + preferenceManager.getString(Constants.KEY_PRENAME));
        byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.image.setImageBitmap(bitmap);
    }
    private void listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error !=null){
            return;
        }
        if(value!=null){
            for(DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderID;
                    chatMessage.receiverID = receiverID;

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderID)){
                        chatMessage.conversationImage =documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversationName =documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversationId =documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }
                    else{
                        chatMessage.conversationImage =documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversationName =documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversationId =documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message =documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }
                else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for(int i=0; i<conversations.size(); i++){
                        String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).senderID.equals(senderID) && conversations.get(i).receiverID.equals(receiverID)){
                            conversations.get(i).message=documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                        }
                    }
                }
            }

            Collections.sort(conversations, (obj1, obj2) ->obj1.dateObject.compareTo(obj2.dateObject));
            adapter.notifyDataSetChanged();
            binding.conversations.smoothScrollToPosition(0);
            binding.conversations.setVisibility(View.VISIBLE);
            binding.error.setVisibility(View.INVISIBLE);

        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e->{});
    }


    @Override
    public void onConversationClicked(Users user) {
        Intent intent=new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}