package com.upt.cti.smc.activities;

import static android.util.Base64.decode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivityProfileBinding;
import com.upt.cti.smc.fragments.EditProfileFragment;
import com.upt.cti.smc.fragments.UserInformationsFragment;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity  {
    PreferenceManager preferenceManager;
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());

        String nume =preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME);
        binding.userName.setText(nume);
        binding.username.setText(preferenceManager.getString(Constants.KEY_USERNAME));

        binding.receivedOrders.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), ReceivedOrdersActivity.class)));

        setFragment(new UserInformationsFragment());
        byte[] bytes = decode(preferenceManager.getString(Constants.KEY_IMAGE), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.image.setImageBitmap(bitmap);
        setListeners();


    }

    private void setListeners(){
        binding.backBtn.setOnClickListener(v-> onBackPressed());
        binding.deconectare.setOnClickListener(v-> signOut());
        binding.editProfile.setOnClickListener(v-> setFragment(new EditProfileFragment()));
        binding.profile.setOnClickListener(v-> setFragment(new UserInformationsFragment()));
    }

    protected void setFragment(Fragment fragment) {
       FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frameLayout, fragment);
        t.commit();
    }

    private void signOut(){
        FirebaseFirestore database =FirebaseFirestore.getInstance();

        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();

        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), LogActivity.class));
            finish();
        })
                .addOnFailureListener(e-> {});
    }
}



