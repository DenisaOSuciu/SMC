package com.upt.cti.smc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.upt.cti.smc.databinding.ActivityFinishOrderBinding;
import com.upt.cti.smc.fragments.FinalFragment;
import com.upt.cti.smc.R;
import com.upt.cti.smc.adapter.CartProductsAdapter;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Order;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FinishOrderActivity extends AppCompatActivity implements ProductListener {

    ActivityFinishOrderBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore  database;
    List<Products> productsList;
    LinearLayoutManager layoutManager;
    Double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityFinishOrderBinding.inflate(getLayoutInflater());


        binding.backBtn.setOnClickListener(v-> onBackPressed());
        preferenceManager = new PreferenceManager(getApplicationContext());

        database = FirebaseFirestore.getInstance();
        layoutManager    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        productsList=new ArrayList<>();
        getProducts();

       checkFields();

        String adr= preferenceManager.getString(Constants.KEY_ADRESS2)
                + "\n" + preferenceManager.getString(Constants.KEY_CITY)
                + "\n" + preferenceManager.getString(Constants.KEY_STATE)
                + "\n" + preferenceManager.getString(Constants.KEY_ZIPCODE);


        binding.addressCard.strada.setText(preferenceManager.getString(Constants.KEY_ADRESS1));
        binding.addressCard.adresa.setText(adr);
        binding.addressCard.phoneNumber.setText(preferenceManager.getString(Constants.KEY_PHONE));
      //  livrare();
      //  binding.total.setText(total.toString());
        setContentView(binding.getRoot());
    }





public void checkFields(){
    if(preferenceManager.getString(Constants.KEY_ADRESS1).equals("") || preferenceManager.getString(Constants.KEY_ADRESS2).equals("") || preferenceManager.getString(Constants.KEY_PHONE).equals("")){
        binding.error.setText("Pentru a finaliza comanda completati campurile pentru adresa de livrare in sectiunea profil");
    }
    else {
        binding.finish.setOnClickListener(v-> finishOrder());
    }
}

    private void getProducts(){
        total=0.0;
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_ADDED_TO_CART)
                .get()
                .addOnCompleteListener( task -> {
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
                            total=total+Integer.parseInt(product.pret);
                            livrare();

                        }
                        if (productsList.size() >= 0) {
                            CartProductsAdapter productAdapter = new CartProductsAdapter(productsList, this);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

                            binding.productsRecycler.products.setLayoutManager(layoutManager);
                            binding.productsRecycler.products.setAdapter(productAdapter);
                            binding.productsRecycler.products.setVisibility(View.VISIBLE);
                        }
                    } });

    }


    public void onProductClicked(Products products) {

    }

    public void livrare(){
        if(binding.delivery.posta.isChecked()){
            total=total+15.0;
            binding.total.setText(total.toString());
        }
        else if(binding.delivery.curier.isSelected()){
            total=total+20.0;
            binding.total.setText(total.toString());
        }else {  binding.total.setText(total.toString());}
    }

    @SuppressLint("ResourceType")
    private void openFragment() {
        FrameLayout fragmentLayout = new FrameLayout(this);
        fragmentLayout.setLayoutParams(new
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentLayout.setId(1000);
        setContentView(fragmentLayout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(1000, new FinalFragment()).commit();
    }

    private void finishOrder(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
            collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_ADDED_TO_CART)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {

                            for (DocumentSnapshot queryDocumentSnapshot : task.getResult().getDocuments()) {

                                Products product = new Products();

                                String documentID = queryDocumentSnapshot.getId();
                                product.id = queryDocumentSnapshot.getId();
                                product.sellerID = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SELLER_ID);
                                product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_ID_FROM_PRINCIPAL_COLLECTION);


                                HashMap<String, Object> order = new HashMap<>();
                                String buyerName =preferenceManager.getString(Constants.KEY_NAME) + "  " + preferenceManager.getString(Constants.KEY_PRENAME);

                                order.put(Constants.KEY_RECEIVED_ORDERS_PRODUCT_NAME, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_NAME));
                                order.put(Constants.KEY_RECEIVED_ORDERS_BUYER_NAME,buyerName);
                                order.put(Constants.KEY_RECEIVED_ORDERS_PRODUCT_SIZE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_PRICE));
                                order.put(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS1, binding.addressCard.strada.getText().toString());
                                order.put(Constants.KEY_RECEIVED_ORDERS_PRODUCT_IMAGE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_IMAGE));
                                order.put(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS2, binding.addressCard.adresa.getText().toString());
                                order.put(Constants.KEY_RECEIVED_ORDERS_BUYER_PHONE, binding.addressCard.phoneNumber.getText().toString());

                                FirebaseFirestore orders = FirebaseFirestore.getInstance();
                                CollectionReference collectionOrders = orders.collection(Constants.KEY_COLLECTION_USERS);

                                collectionOrders.document(product.sellerID).collection(Constants.KEY_COLLECTION_RECEIVED_ORDERS)
                                        .add(order)
                                        .addOnSuccessListener(documentReference -> {
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_NAME, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_NAME));
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_BUYER_NAME, buyerName);
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_SIZE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SIZE));
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS1, binding.addressCard.strada.getText().toString());
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_IMAGE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_IMAGE));
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS2, binding.addressCard.adresa.getText().toString());
                                            preferenceManager.putString(Constants.KEY_RECEIVED_ORDERS_BUYER_PHONE, binding.addressCard.phoneNumber.getText().toString());
                                        })
                                        .addOnFailureListener(exception -> {
                                        });



                                HashMap<String, Object> products = new HashMap<>();
                                products.put(Constants.KEY_PRODUCT_NAME, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_NAME));
                                products.put(Constants.KEY_PRODUCT_DESCRIPTION, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_DESCRIPTION));
                                products.put(Constants.KEY_PRODUCT_SIZE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SIZE));
                                products.put(Constants.KEY_PRODUCT_PRICE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_PRICE));
                                products.put(Constants.KEY_PRODUCT_IMAGE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_IMAGE));
                                products.put(Constants.KEY_PRODUCT_SELLER, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SELLER_ID));

                                collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_MY_ORDERS)
                                        .add(products)
                                        .addOnSuccessListener(documentReference -> {
                                            preferenceManager.putString(Constants.KEY_PRODUCT_NAME, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_NAME));
                                            preferenceManager.putString(Constants.KEY_PRODUCT_DESCRIPTION, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_DESCRIPTION));
                                            preferenceManager.putString(Constants.KEY_PRODUCT_SIZE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_SIZE));
                                            preferenceManager.putString(Constants.KEY_PRODUCT_PRICE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_PRICE));
                                            preferenceManager.putString(Constants.KEY_PRODUCT_IMAGE, queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_ADDED_IMAGE));

                                        })
                                        .addOnFailureListener(exception -> {
                                        });


                                deleteFromCart(documentID);
                                deleteFromAllProducts(product.sellerID, product.seller);
                                deleteFromAllCarts(product.seller);


                            }
                        }
                    });




    }

    public void deleteFromCart(String documentID){
        FirebaseFirestore cart = FirebaseFirestore.getInstance();
        cart.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_ADDED_TO_CART)
                .document(documentID)
                .delete().addOnCompleteListener(
                task -> openFragment());
    }

    public void deleteFromAllProducts(String sellerID, String productID){
        FirebaseFirestore allProducts = FirebaseFirestore.getInstance();
        allProducts.collection(Constants.KEY_COLLECTION_USERS)
                .document(sellerID)
                .collection(Constants.KEY_COLLECTION_PRODUCTS)
                .document(productID)
                .delete().addOnCompleteListener(
                task1 -> {
                    openFragment();
                });
    }


    public void deleteFromAllCarts(String productID){
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot query : task.getResult()) {
                    Users user = new Users();
                    user.id = query.getId();

                    FirebaseFirestore data2 = FirebaseFirestore.getInstance();
                        data2.collection(Constants.KEY_COLLECTION_USERS).document(user.id).collection(Constants.KEY_ADDED_TO_CART).get()
                                .addOnCompleteListener(task2 -> {
                                    for (QueryDocumentSnapshot query2 : task2.getResult()) {
                                        Products productToDelete = new Products();
                                        productToDelete.id = query2.getId();
                                        productToDelete.sellerID = query2.getString(Constants.KEY_PRODUCT_ADDED_ID_FROM_PRINCIPAL_COLLECTION);

                                        if (productToDelete.sellerID.equals(productID)) {
                                            FirebaseFirestore data3 = FirebaseFirestore.getInstance();
                                            data3.collection(Constants.KEY_COLLECTION_USERS).document(user.id).collection(Constants.KEY_ADDED_TO_CART)
                                                    .document(productToDelete.id).delete().addOnCompleteListener(task3 -> { });
                                            }
                                        }
                                    });

                        }
                    }
                });
    }


}
