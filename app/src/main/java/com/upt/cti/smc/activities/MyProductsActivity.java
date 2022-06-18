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
import com.upt.cti.smc.adapter.MyProductsAdapter;
import com.upt.cti.smc.databinding.ActivityMyProductsBinding;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.List;


public class MyProductsActivity extends AppCompatActivity implements ProductListener {
    ActivityMyProductsBinding binding;
    private PreferenceManager preferenceManager;

    FirebaseFirestore  database;
    List<Products> productsList;
    MyProductsAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        database =FirebaseFirestore.getInstance();
        adapter=new MyProductsAdapter(productsList, this);

        productsList=new ArrayList<>();
        layoutManager    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        getProducts();
        setListeners();
    }


    public void setListeners(){
        binding.backBtn.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), MainPageActivity.class)));
        binding.plusBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddProductActivity.class)));
    }

    private void getProducts(){
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_PRODUCTS)

                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for(DocumentSnapshot queryDocumentSnapshot: task.getResult().getDocuments()){
                            Products product =queryDocumentSnapshot.toObject(Products.class);
                            String documentID=queryDocumentSnapshot.getId();

                            product.setDocumentID(documentID);
                            product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                            product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                            product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                            product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                            product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                            product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);

                            if(product.seller.equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))){
                                productsList.add(product);}

                        }if(productsList.size() >=0 ){
                            MyProductsAdapter productAdapter =new MyProductsAdapter(productsList, this);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

                            binding.productsRecycler.setLayoutManager(layoutManager);
                            binding.productsRecycler.setAdapter(productAdapter);
                            binding.productsRecycler.setVisibility(View.VISIBLE);
                        }
                        else {
                            showToast("Error1");
                        }
                    }
                    else {
                        showToast("Error2");
                    }
                });
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void onProductClicked(Products products) {
     Intent i= new Intent(getApplicationContext(), MyProductActivity.class);
        i.putExtra(Constants.KEY_PRODUCT, products);
        startActivity(i);
        finish();





    }


}