package com.upt.cti.smc.activities;

import static android.util.Base64.decode;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivityProductPageBinding;
import com.upt.cti.smc.listeners.ConversationListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductPageActivity extends AppCompatActivity implements ConversationListener {
    private PreferenceManager preferenceManager;
    ActivityProductPageBinding binding;
    private Products product;
    FirebaseFirestore database;
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database =FirebaseFirestore.getInstance();
        product = (Products) getIntent().getSerializableExtra(Constants.KEY_PRODUCT);
        binding.name.setText(product.nume);
        binding.descriere.setText(product.descriere);
        binding.pret.setText(product.pret);
        binding.marime.setText(product.marime);
        Users users =new Users();
        users.name = product.seller;
        users.image=product.imagine;
        users.id= product.sellerID;


        binding.message.setOnClickListener(v-> onConversationClicked(users)

                );

        binding.sellerName.setText(users.name);
        byte[] bytes;
        bytes = decode(product.imagine, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.image.setImageBitmap(bitmap);
        favorite();

        byte[] bytes2 = decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytes2, 0, bytes2.length);
        binding.userImage.setImageBitmap(bitmap2);
        setListeners();

        byte[] bytes3 = decode(product.imagine, Base64.DEFAULT);
        Bitmap bitmap3 = BitmapFactory.decodeByteArray(bytes3, 0, bytes3.length);
        encodedImage = encodeImage(bitmap3);
            binding.reducere.setText(preferenceManager.getString(Constants.KEY_PRODUCT_PRICE_SALE));
         binding.reducere.setTextColor(Color.RED);
        binding.pret.setPaintFlags(binding.pret.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }


    public void setListeners() {
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.addBtn.setOnClickListener(v -> addedToCart());
        binding.favoriteBtn.setOnClickListener(v -> addedToFavorite());

    }

    public void addedToCart() {
        database = FirebaseFirestore.getInstance();
        final HashMap<String, Object> products = new HashMap<>();


     //   products.put(Constants.KEY_ADDED_FROM_USER, preferenceManager.getString(Constants.KEY_NAME));
        products.put(Constants.KEY_PRODUCT_ADDED_ID_FROM_PRINCIPAL_COLLECTION,product.id);
        products.put(Constants.KEY_PRODUCT_ADDED_NAME, binding.name.getText().toString());
        products.put(Constants.KEY_PRODUCT_ADDED_DESCRIPTION, binding.descriere.getText().toString());
        products.put(Constants.KEY_PRODUCT_ADDED_PRICE, binding.pret.getText().toString());
        products.put(Constants.KEY_PRODUCT_ADDED_SIZE, binding.marime.getText().toString());
        //products.put(Constants.KEY_PRODUCT_ADDED_SELLER, binding.sellerName.getText().toString());
        products.put(Constants.KEY_PRODUCT_ADDED_SELLER_ID, product.sellerID);
        products.put(Constants.KEY_PRODUCT_ADDED_IMAGE, encodedImage);


        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS); //colectia users
        collectionReference
                .document(preferenceManager.getString(Constants.KEY_USER_ID)) // in user
                .collection(Constants.KEY_ADDED_TO_CART)
                .add(products)
                .addOnSuccessListener(documentReference -> {

                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_NAME, binding.name.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_DESCRIPTION, binding.descriere.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_SIZE, binding.marime.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_PRICE, binding.pret.getText().toString());
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_IMAGE, encodedImage);
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_ID_FROM_PRINCIPAL_COLLECTION, product.id);
                    preferenceManager.putString(Constants.KEY_PRODUCT_ADDED_SELLER_ID, product.sellerID);
                    Intent i = new Intent(getApplicationContext(), CartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(exception -> {
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


    private void favorite(){
        database = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_FAVORITE).get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Products> existent = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            Products exist = new Products();
                            exist.nume = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_NAME);
                            existent.add(exist);
                            for (int i = 0; i < existent.size(); i++) {
                                if (existent.get(i).nume.equals(binding.name.getText().toString())) { //&& exist.imagine.equals(encodedImage)){ //&& exist.sellerID.equals(binding.sellerName.getText().toString())) {
                                    binding.text.setText("Produsul este in lista de favorite");
                                    binding.favoriteBtn.setVisibility(View.INVISIBLE);
                                    binding.favorite.setBackground(getDrawable(R.drawable.ic_favorite_red));
                                }
                            }
                        }
                    }
                });}

    public void addedToFavorite() {
        database = FirebaseFirestore.getInstance();
        final HashMap<String, Object> products = new HashMap<>();

        products.put(Constants.KEY_FAVORITE_ADDED_FROM_USER, preferenceManager.getString(Constants.KEY_NAME));
        products.put(Constants.KEY_FAVORITE_NAME, binding.name.getText().toString());
        products.put(Constants.KEY_FAVORITE_DESCRIPTION, binding.descriere.getText().toString());
        products.put(Constants.KEY_FAVORITE_PRICE, binding.pret.getText().toString());
        products.put(Constants.KEY_FAVORITE_SALE_PRICE, "");
        products.put(Constants.KEY_FAVORITE_SIZE, binding.marime.getText().toString());
        products.put(Constants.KEY_FAVORITE_SELLER, binding.sellerName.getText().toString());
        products.put(Constants.KEY_FAVORITE_IMAGE, encodedImage);
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);

     collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_FAVORITE)
            .add(products)
              .addOnSuccessListener(documentReference -> {
                   preferenceManager.putString(Constants.KEY_FAVORITE_NAME, binding.name.getText().toString());
                   preferenceManager.putString(Constants.KEY_FAVORITE_DESCRIPTION, binding.descriere.getText().toString());
                   preferenceManager.putString(Constants.KEY_FAVORITE_SIZE, binding.marime.getText().toString());
                   preferenceManager.putString(Constants.KEY_FAVORITE_PRICE, binding.pret.getText().toString());
                   preferenceManager.putString(Constants.KEY_FAVORITE_SALE_PRICE, "");
                   preferenceManager.putString(Constants.KEY_FAVORITE_IMAGE, encodedImage);
                   preferenceManager.putString(Constants.KEY_FAVORITE_ADDED_FROM_USER, preferenceManager.getString(Constants.KEY_NAME));
                   preferenceManager.putString(Constants.KEY_FAVORITE_SELLER, binding.sellerName.getText().toString());
                                            })
                                            .addOnFailureListener(exception -> {
                                            });
                                    binding.text.setText("Produsul a fost adaugat la favorite");
                                    binding.favorite.setBackgroundResource(R.drawable.ic_favorite_red);

                            }



    @Override
    public void onConversationClicked(Users user) {
        Intent i= new Intent(getApplicationContext(), MessageActivity.class);
        i.putExtra(Constants.KEY_USER, user);
        startActivity(i);
        finish();
    }
}



