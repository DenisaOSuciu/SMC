package com.upt.cti.smc.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upt.cti.smc.databinding.ItemContainerUserBinding;
import com.upt.cti.smc.listeners.UsersListener;
import com.upt.cti.smc.model.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<Users> users;
    private final UsersListener usersListener;

    public UsersAdapter(List<Users> users, UsersListener usersListener) {

        this.users = users;
        this.usersListener=usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
              LayoutInflater.from(parent.getContext()), parent, false);
      return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;


        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding =itemContainerUserBinding;
        }

        void setUserData(Users user){
            String nume=user.name + " " +user.prename;
            binding.name.setText(nume);
            binding.username.setText(user.username);
            binding.image.setImageBitmap(getUserPhoto(user.image));
            binding.getRoot().setOnClickListener( v-> usersListener.onUserClicked(user)

            );
        }


    }

    private Bitmap getUserPhoto(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
