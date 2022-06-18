package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.adapter.CartProductsAdapter;
import com.upt.cti.smc.databinding.ActivityCartBinding;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ProductListener {

    ActivityCartBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;
    List<Products> productsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        productsList = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(v -> onBackPressed());
        //sum = 0.0;

        binding.placeOrderBtn.setOnClickListener(v -> {
            if (productsList.size() == 0) {
                showToast("Cosul de cumparaturi este gol");
            } else {
                startActivity(new Intent(getApplicationContext(), FinishOrderActivity.class));
            }
        });
        getProducts();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getProducts() {
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_ADDED_TO_CART)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {


                        for (DocumentSnapshot queryDocumentSnapshot : task.getResult().getDocuments()) {
                            Products product = queryDocumentSnapshot.toObject(Products.class);
                            String documentID = queryDocumentSnapshot.getId();

                            product.setDocumentID(documentID);
                            product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_NAME);
                            product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_IMAGE);
                            product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_DESCRIPTION);
                            product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_PRICE);
                            product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SIZE);
                            productsList.add(product);

                        }
                        if (productsList.size() > 0) {
                            CartProductsAdapter productAdapter = new CartProductsAdapter(productsList, this);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            binding.products.setLayoutManager(layoutManager);
                            binding.products.setAdapter(productAdapter);
                            binding.products.setVisibility(View.VISIBLE);
                            binding.error.setVisibility(View.INVISIBLE);
                        }
                        else if(productsList.size() == 0){
                            binding.error.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void onProductClicked(Products products) {
        Intent i = new Intent(getApplicationContext(), ProductPageActivity.class);
        i.putExtra(Constants.KEY_PRODUCT, products);
        startActivity(i);
        finish();
    }
}