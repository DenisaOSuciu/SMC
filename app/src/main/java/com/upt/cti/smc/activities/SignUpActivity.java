package com.upt.cti.smc.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.databinding.ActivitySignUpBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        setListeners();

    }
    private void setListeners(){
        binding.registerBtn.setOnClickListener(v -> {
            if(areDetailsValid()){

                signUp(); } });

        binding.addPhotoBtn.setOnClickListener(v -> {
            Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

        binding.goToLog.setOnClickListener(v-> {
            Intent i =new Intent(getApplicationContext(), LogActivity.class);
            startActivity(i);
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);
        FirebaseFirestore database =FirebaseFirestore.getInstance();

        HashMap<String,Object> user=new HashMap<>();

        user.put(Constants.KEY_NAME, binding.nameReg.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.emailReg.getText().toString());
        user.put(Constants.KEY_PRENAME, binding.prenameReg.getText().toString());
        user.put(Constants.KEY_USERNAME, binding.usernameReg.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.passwordReg.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        user.put(Constants.KEY_ADRESS1, "");
        user.put(Constants.KEY_ADRESS2, "");
        user.put(Constants.KEY_PHONE, "");
        user.put(Constants.KEY_CITY, "");
        user.put(Constants.KEY_STATE, "");
        user.put(Constants.KEY_ZIPCODE, "");

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_PRENAME, binding.prenameReg.getText().toString());
                    preferenceManager.putString(Constants.KEY_NAME, binding.nameReg.getText().toString());
                    preferenceManager.putString(Constants.KEY_USERNAME, binding.usernameReg.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.emailReg.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    preferenceManager.putString(Constants.KEY_ADRESS1, "");
                    preferenceManager.putString(Constants.KEY_ADRESS2, "");
                    preferenceManager.putString(Constants.KEY_PHONE, "");
                    preferenceManager.putString(Constants.KEY_CITY, "");
                    preferenceManager.putString(Constants.KEY_STATE, "");
                    preferenceManager.putString(Constants.KEY_ZIPCODE, "");
                    Intent i =new Intent(getApplicationContext(), SuplimentarDetailsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });

    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth =150;
        int previewHeight =bitmap.getHeight() * previewWidth /bitmap.getHeight();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes =byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream =getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.image.setImageBitmap(bitmap);
                            encodedImage =encodeImage(bitmap);
                        }
                        catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

            }
    );

    private Boolean areDetailsValid(){
        if(encodedImage == null){
            showToast("Select profile image");
            return false;

        }else if(binding.nameReg.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }else if(binding.usernameReg.getText().toString().trim().isEmpty()) {
            showToast("Enter username");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailReg.getText().toString()).matches()){
            showToast("Your email adress is not valid!");
            return false;
        }else if(binding.passwordReg.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }
        else return true;

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.registerBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.registerBtn.setVisibility(View.VISIBLE);
        }
    }


















}