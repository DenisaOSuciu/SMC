package com.upt.cti.smc.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivityProfileBinding;
import com.upt.cti.smc.databinding.FragmentEditProfileBinding;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.PreferenceChangeEvent;


public class EditProfileFragment extends Fragment  {
    FragmentEditProfileBinding binding;
    FirebaseFirestore database;
    PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentEditProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getContext());
        database =FirebaseFirestore.getInstance();

        binding.strada.setText(preferenceManager.getString(Constants.KEY_ADRESS1));
        binding.adresa2.setText(preferenceManager.getString(Constants.KEY_ADRESS2));
        binding.email.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.telefon.setText(preferenceManager.getString(Constants.KEY_PHONE));
        binding.oras.setText(preferenceManager.getString(Constants.KEY_CITY));
        binding.codPostal.setText(preferenceManager.getString(Constants.KEY_ZIPCODE));
        binding.judet.setText(preferenceManager.getString(Constants.KEY_STATE));
        binding.telefon.setText(preferenceManager.getString(Constants.KEY_PHONE));

        binding.saveBtn.setOnClickListener(v-> updateData());
        return binding.getRoot();
    }

    public void updateData(){
        Map<String, Object> updateInfo =new HashMap<>();

        if(!binding.strada.getText().toString().matches(preferenceManager.getString(Constants.KEY_ADRESS1))){
            updateInfo.put(Constants.KEY_ADRESS1, binding.strada.getText().toString());
            preferenceManager.putString(Constants.KEY_ADRESS1, binding.strada.getText().toString());
        }
        if(!binding.adresa2.getText().toString().matches(preferenceManager.getString(Constants.KEY_ADRESS2))){
            updateInfo.put(Constants.KEY_ADRESS2, binding.adresa2.getText().toString());
            preferenceManager.putString(Constants.KEY_ADRESS2, binding.adresa2.getText().toString());

        }
        if(!binding.oras.getText().toString().matches(preferenceManager.getString(Constants.KEY_CITY))){
            updateInfo.put(Constants.KEY_CITY, binding.oras.getText().toString());
            preferenceManager.putString(Constants.KEY_CITY, binding.oras.getText().toString());

        }
        if(!binding.judet.getText().toString().matches(preferenceManager.getString(Constants.KEY_STATE))){
            updateInfo.put(Constants.KEY_STATE, binding.judet.getText().toString());
            preferenceManager.putString(Constants.KEY_STATE, binding.judet.getText().toString());

        }
        if(!binding.codPostal.getText().toString().matches(preferenceManager.getString(Constants.KEY_ZIPCODE))){
            updateInfo.put(Constants.KEY_ZIPCODE, binding.codPostal.getText().toString());
            preferenceManager.putString(Constants.KEY_ZIPCODE, binding.codPostal.getText().toString());

        }
        if(!binding.telefon.getText().toString().matches(preferenceManager.getString(Constants.KEY_PHONE))){
            updateInfo.put(Constants.KEY_PHONE, binding.telefon.getText().toString());
            preferenceManager.putString(Constants.KEY_PHONE, binding.telefon.getText().toString());

        }
        if(!binding.email.getText().toString().matches(preferenceManager.getString(Constants.KEY_EMAIL))){
            updateInfo.put(Constants.KEY_EMAIL, binding.email.getText().toString());
            preferenceManager.putString(Constants.KEY_EMAIL, binding.email.getText().toString());

        }

        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);


      collectionReference.get().addOnCompleteListener(
               task -> {
                   if(task.isSuccessful() && !task.getResult().isEmpty()){
                       DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                       DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));


                       documentReference.update(updateInfo).addOnSuccessListener(
                               v -> setFragment(new UserInformationsFragment())
                       ).addOnFailureListener(e -> {
                       });

                   }
               }
       );



    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getParentFragmentManager().beginTransaction();
        t.replace(R.id.frameLayout, fragment);
        t.commit();
    }


}