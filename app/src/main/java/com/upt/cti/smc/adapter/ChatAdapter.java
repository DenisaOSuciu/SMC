package com.upt.cti.smc.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upt.cti.smc.databinding.ItemContainerReceivedMessageBinding;
import com.upt.cti.smc.databinding.ItemContainerSentMessageBinding;
import com.upt.cti.smc.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Bitmap reveiverUserProfile;
    private final List<ChatMessage> chatMessages;
    private final String senderID;

    public static final int VIEW_TYPE_SENT =1;
    public static final int VIEW_TYPE_RECEIVED =2;

    public ChatAdapter(Bitmap reveiverUserProfile, List<ChatMessage> chatMessages, String senderID) {
        this.reveiverUserProfile = reveiverUserProfile;
        this.chatMessages = chatMessages;
        this.senderID = senderID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }
        else
            return new ReceiverMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }
        else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), reveiverUserProfile);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public int getItemViewType(int position){
        if(chatMessages.get(position).senderID.equals(senderID)){
            return VIEW_TYPE_SENT;
        } else return VIEW_TYPE_RECEIVED;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder( ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            this.binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceivedMessageBinding binding;

        public ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            this.binding=itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receivedProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.image.setImageBitmap(receivedProfileImage);
        }


    }


}
