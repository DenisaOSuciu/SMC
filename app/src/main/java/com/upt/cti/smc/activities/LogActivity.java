package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.databinding.ActivityLogBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

public class LogActivity extends AppCompatActivity {

   private ActivityLogBinding binding;
   private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityLogBinding.inflate(getLayoutInflater());
        preferenceManager =new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent i=new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(binding.getRoot());
        setListeners();


    }
    private void setListeners(){
        binding.signBt.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.login.setOnClickListener(v-> {
            if (areDetailsValid()){
                signIn();
            }
        });
    }

    private void signIn(){

        FirebaseFirestore database =FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_USERNAME, binding.usernameLog.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.passwordLog.getText().toString())
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        if(binding.rememberMe.isChecked()){
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);}
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());

                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_PRENAME, documentSnapshot.getString(Constants.KEY_PRENAME));
                        preferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        preferenceManager.putString(Constants.KEY_ADRESS1, documentSnapshot.getString(Constants.KEY_ADRESS1));
                        preferenceManager.putString(Constants.KEY_ADRESS2, documentSnapshot.getString(Constants.KEY_ADRESS2));
                        preferenceManager.putString(Constants.KEY_CITY, documentSnapshot.getString(Constants.KEY_CITY));
                        preferenceManager.putString(Constants.KEY_STATE, documentSnapshot.getString(Constants.KEY_STATE));
                        preferenceManager.putString(Constants.KEY_ZIPCODE, documentSnapshot.getString(Constants.KEY_ZIPCODE));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                        Intent i =new Intent(getApplicationContext(), MainPageActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        System.out.println(preferenceManager.getString(Constants.KEY_USER_ID));
                    }
                    else {
                        binding.error.setText("Numele de utilizator sau parola sunt grsite");
                    }

                });

    }


    private Boolean areDetailsValid(){
        if(binding.usernameLog.getText().toString().trim().isEmpty()){
            binding.error.setText("Intrpdu numele de utilizator");
            return false;
        }else if(binding.passwordLog.getText().toString().trim().isEmpty()){
            binding.error.setText("Introdu Parola");
            return false;
        }
        else return true;
    }


}