package com.upt.cti.smc.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.databinding.ActivityAddProductBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        setListeners();

    }

    private void setListeners() {

        binding.addBtn.setOnClickListener(v -> {
            if(areDetailsValid()){

                addProduct(); } });

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.photo.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

    }

    public void addProduct() {
        FirebaseFirestore database =FirebaseFirestore.getInstance();

        HashMap<String,Object> product=new HashMap<>();
        String sellerName =preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME);

        product.put(Constants.KEY_PRODUCT_NAME, binding.nume.getText().toString());
        product.put(Constants.KEY_PRODUCT_DESCRIPTION, binding.descriere.getText().toString());
        product.put(Constants.KEY_PRODUCT_SIZE, binding.marime.getSelectedItem().toString());
        product.put(Constants.KEY_PRODUCT_CATEGORY, binding.categorie.getSelectedItem().toString());
        product.put(Constants.KEY_PRODUCT_PRICE, binding.pret.getText().toString());
        product.put(Constants.KEY_PRODUCT_SELLER, sellerName);
        product.put(Constants.KEY_PRODUCT_IMAGE, encodedImage);
        product.put(Constants.KEY_PRODUCT_SELLER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        product.put(Constants.KEY_PRODUCT_PRICE_SALE, "");


        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_PRODUCTS)
                .add(product)
                .addOnSuccessListener(documentReference -> {

                    preferenceManager.putString(Constants.KEY_PRODUCT_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_PRODUCT_SELLER, sellerName);
                    preferenceManager.putString(Constants.KEY_PRODUCT_NAME, binding.nume.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_IMAGE, encodedImage);
                    preferenceManager.putString(Constants.KEY_PRODUCT_SELLER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                    preferenceManager.putString(Constants.KEY_PRODUCT_PRICE, binding.pret.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_DESCRIPTION, binding.descriere.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_SIZE, binding.marime.getSelectedItem().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_CATEGORY, binding.categorie.getSelectedItem().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_PRICE_SALE, "");
                    Intent i =new Intent(getApplicationContext(), MyProductsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    System.out.println(preferenceManager.getString(Constants.KEY_USER_ID));
                    System.out.println(preferenceManager.getString(Constants.KEY_PRODUCT_SELLER_ID));
                })
                .addOnFailureListener(exception -> {
                    showToast(exception.getMessage());
                });
    }




    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getHeight();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.photo.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
    );

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean areDetailsValid() {
        if (encodedImage == null) {
            showToast("Selectati o imagine");
            return false;

        } else if (binding.nume.getText().toString().trim().isEmpty()) {
            showToast("Introduceti numele");
            return false;
        } else if (binding.descriere.getText().toString().trim().isEmpty()) {
            showToast("Introduceti descrierea");
            return false;
        } else if (binding.marime.getSelectedItem().toString().trim().isEmpty()) {
            showToast("Introduceti marimea");
            return false;
        }
        else if (binding.pret.getText().toString().trim().isEmpty()) {
            showToast("Introduceti pretul");
            return false;
        }
        else return true;

    }

}


