package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.adapter.UsersAdapter;

import com.upt.cti.smc.databinding.ActivityUsersDisplayBinding;
import com.upt.cti.smc.listeners.UsersListener;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;


import java.util.ArrayList;
import java.util.List;

public class UsersDisplayActivity extends AppCompatActivity implements UsersListener {

    private ActivityUsersDisplayBinding binding;
    private PreferenceManager preferenceManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersDisplayBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        getUsers();
        setListeners();
    }

    public void setListeners(){
        binding.backBtn.setOnClickListener(v-> onBackPressed());
    }

    private void getUsers(){
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener( task -> {
                    String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Users> users = new ArrayList<>();

                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            Users user =new Users();
                            user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.prename=queryDocumentSnapshot.getString(Constants.KEY_PRENAME);
                            user.username=queryDocumentSnapshot.getString(Constants.KEY_USERNAME);
                            user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id= queryDocumentSnapshot.getId();
                            users.add(user);

                        }if(users.size() >0){
                            UsersAdapter usersAdapter =new UsersAdapter(users, this);
                            binding.usersRecycler.setAdapter(usersAdapter);
                            binding.usersRecycler.setVisibility(View.VISIBLE);
                        }
                        else {
                            showToast("Error");
                        }
                    }
                    else {
                        showToast("Error");
                    }
                });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserClicked(Users users) {
        Intent i= new Intent(getApplicationContext(), MessageActivity.class);
        i.putExtra(Constants.KEY_USER, users);
        startActivity(i);
        finish();
    }
}