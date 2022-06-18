package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.upt.cti.smc.adapter.ChatAdapter;
import com.upt.cti.smc.databinding.ActivityMessageBinding;
import com.upt.cti.smc.model.ChatMessage;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private Users receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversationId=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverData();
        init();
        listenMessages();

    }

    private void loadReceiverData(){
        receiverUser = (Users) getIntent().getSerializableExtra(Constants.KEY_USER);
        String name =receiverUser.name + " " + receiverUser.prename;
        binding.textName.setText(name);
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getBitmapFromEncodedString(receiverUser.image),chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID));

        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message =new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversationId !=null){
            updateConversation(binding.inputMessage.getText().toString());
        }
        else{
            HashMap<String, Object> conversation =new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME)+ " " + preferenceManager.getString(Constants.KEY_PRENAME));
            conversation.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversation.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversation.put(Constants.KEY_RECEIVER_NAME, receiverUser.name + " " + receiverUser.prename);
            conversation.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversation.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversation.put(Constants.KEY_TIMESTAMP, new Date());
            addConversation(conversation);

        }
        binding.inputMessage.setText(null);
    }

    private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{

        if(error !=null){
            return;
        }
        if (value !=null){
            int count= chatMessages.size();

            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED){

                    ChatMessage chatMessage =new ChatMessage();

                    chatMessage.senderID =documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverID =documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message =documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime= getDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject =documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);

                }
            }

            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }
            else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);

            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);

        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversationId == null){
            checkForConversations();
        }

    };


    private Bitmap getBitmapFromEncodedString( String encodedImage){
        byte[] bytes= Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void setListeners(){
        binding.imageBack.setOnClickListener( v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
    }

    public void checkForConversations(){
        if(chatMessages.size()!=0){
            checkForConversationsRemotely( preferenceManager.getString(Constants.KEY_USER_ID), receiverUser.id);
        }
        checkForConversationsRemotely(receiverUser.id, preferenceManager.getString(Constants.KEY_USER_ID));
    }


    public String getDateTime(Date date){
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void checkForConversationsRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }

    public final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener =task -> {
        if(task.isSuccessful() && task.getResult()!= null && task.getResult().getDocuments().size() >0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversationId =documentSnapshot.getId();
        }
    };

    private void addConversation(HashMap<String ,Object> conversation){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());

    }

    private void updateConversation(String message){
        DocumentReference documentReference=database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }


}