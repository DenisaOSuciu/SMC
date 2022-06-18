package com.upt.cti.smc.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;
import com.upt.cti.smc.databinding.ItemContainerRecentConversationBinding;
import com.upt.cti.smc.listeners.ConversationListener;
import com.upt.cti.smc.model.ChatMessage;
import com.upt.cti.smc.model.Users;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversationViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(ItemContainerRecentConversationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
            holder.setUserData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConversationBinding binding;

        public ConversationViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding=itemContainerRecentConversationBinding;
        }

        void setUserData(ChatMessage chatMessage){
           // String nume=user.name + " " +user.prename;
            binding.name.setText(chatMessage.conversationName);
            binding.recentMessage.setText(chatMessage.message);
            binding.image.setImageBitmap(getUserPhoto(chatMessage.conversationImage));
            binding.getRoot().setOnClickListener( v->
                    {
                        Users user =new Users();
                        user.id= chatMessage.conversationId;
                        user.name= chatMessage.conversationName;
                        user.image= chatMessage.conversationImage;
                        conversationListener.onConversationClicked(user);
                    }
                    );
        }
    }


    private Bitmap getUserPhoto(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
