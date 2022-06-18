package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivitySuplimentarDetailsBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.prefs.PreferenceChangeEvent;

public class SuplimentarDetailsActivity extends AppCompatActivity {
    ActivitySuplimentarDetailsBinding binding;
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager =new PreferenceManager(getApplicationContext());
        binding=ActivitySuplimentarDetailsBinding.inflate(getLayoutInflater());
        binding.skip.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), MainPageActivity.class)));
        binding.save.setOnClickListener(v-> addInfo());
        setContentView(binding.getRoot());
    }


    private void addInfo(){


        FirebaseFirestore database =FirebaseFirestore.getInstance();

        HashMap<String,Object> user=new HashMap<>();

        user.put(Constants.KEY_ADRESS1, binding.adresa1.getText().toString());
        user.put(Constants.KEY_ADRESS2, binding.adresa2.getText().toString());
        user.put(Constants.KEY_CITY, binding.oras.getText().toString());
        user.put(Constants.KEY_STATE, binding.judet.getText().toString());
        user.put(Constants.KEY_ZIPCODE, binding.codpostal.getText().toString());
        user.put(Constants.KEY_PHONE, binding.telefon.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(user)
                .addOnSuccessListener(documentReference -> {

                    preferenceManager.putString(Constants.KEY_PHONE, binding.telefon.getText().toString());
                    preferenceManager.putString(Constants.KEY_ADRESS1, binding.adresa1.getText().toString());
                    preferenceManager.putString(Constants.KEY_ADRESS2, binding.adresa2.getText().toString());
                    preferenceManager.putString(Constants.KEY_CITY, binding.oras.getText().toString());
                    preferenceManager.putString(Constants.KEY_ZIPCODE, binding.codpostal.getText().toString());
                    preferenceManager.putString(Constants.KEY_STATE, binding.judet.getText().toString());
                    Intent i =new Intent(getApplicationContext(), MainPageActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                })
                .addOnFailureListener(exception -> {
                });


    }



}