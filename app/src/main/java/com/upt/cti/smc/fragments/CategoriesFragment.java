package com.upt.cti.smc.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.R;
import com.upt.cti.smc.activities.CartActivity;
import com.upt.cti.smc.activities.ChatActivity;
import com.upt.cti.smc.activities.MainPageActivity;
import com.upt.cti.smc.activities.MyProductActivity;
import com.upt.cti.smc.activities.MyProductsActivity;
import com.upt.cti.smc.activities.ProductPageActivity;
import com.upt.cti.smc.adapter.ProductAdapter;
import com.upt.cti.smc.databinding.FragmentCategoriesBinding;

import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoriesFragment extends Fragment implements ProductListener {
    FragmentCategoriesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentCategoriesBinding.inflate(getLayoutInflater());
        binding.backBtn.setOnClickListener(v-> startActivity(new Intent(getContext(), MainPageActivity.class)));
        getProducts();

        binding.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option=binding.spinner2.getSelectedItem().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
                groupsQuery
                        .get()
                        .addOnCompleteListener( task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                List<Products> products = new ArrayList<>();

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {


                                    Products product = new Products();
                                    product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                    product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                    product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                    product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                    product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                    product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                    product.salePrice = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                    product.categorie =queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY);
                                    product.id = queryDocumentSnapshot.getId();

                                    if(!(product.categorie.equals("Incaltaminte") || product.categorie.equals("Accesorii"))){
                                        products.add(product);}

                                }

                                int n = products.size();

                                if (option.equals("Preț crescător")) {
                                    for (int i = 0; i < n - 1; i++)
                                        for (int j = 0; j < n - i - 1; j++)

                                            if (Integer.parseInt(products.get(j).pret) > Integer.parseInt(products.get(j + 1).pret)) {
                                                swap(products, j, j + 1);
                                            }
                                } else if (option.equals("Preț descrescător")) {
                                    for (int i = 0; i < n - 1; i++)
                                        for (int j = 0; j < n - i - 1; j++)

                                            if (Integer.parseInt(products.get(j).pret) < Integer.parseInt(products.get(j + 1).pret)) {
                                                swap(products, j, j + 1);
                                            }
                                } else if (option.equals("După relevanță") || option.equals("Sortează produsele")) {
                                   getProducts();
                                }
                                setAdapter(products);

                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getProducts();
            }
        });

        binding.categorii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = binding.categorii.getSelectedItem().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
                groupsQuery.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                List<Products> products = new ArrayList<>();

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                    Products product = new Products();
                                    product.categorie =queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY);
                                    product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                    product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                    product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                    product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                    product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                    product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                    product.salePrice = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                    product.id = queryDocumentSnapshot.getId();
                                    product.sellerID = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER_ID);

                                    if(!(product.categorie.equals("Incaltaminte") || product.categorie.equals("Accesorii"))){
                                    if(option.equals(queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY))){
                                        products.add(product);}}

                                } if(option.equals("Toate produsele")){
                                    getProducts();
                                }
                                setAdapter(products);
                            }
                        });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getProducts();
            }

        });



   binding.homeBtn.setOnClickListener(v-> {Intent i = new Intent(getContext(), MainPageActivity.class);
       startActivity(i); });

   binding.myProductsBtn.setOnClickListener(v-> {
       Intent i = new Intent(getContext(), MyProductsActivity.class);
       startActivity(i);
   });
        binding.chatBtn.setOnClickListener(v-> {
            Intent i = new Intent(getContext(), ChatActivity.class);
            startActivity(i);
        });

        binding.cartBtn.setOnClickListener(v-> {
            Intent i = new Intent(getContext(), CartActivity.class);
            startActivity(i);
        });
        binding.favoriteBtn.setOnClickListener(v-> replaceLayout("favorite"));

        return binding.getRoot();
    }




    @SuppressLint("ResourceType")
    private void replaceLayout(String fragment) {

        Bundle bundle = new Bundle();
        bundle.putString("message", fragment);
        AllProductsFragment fragobj = new AllProductsFragment();
        fragobj.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragobj, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    public  final <T> void swap (List<T> l, int i, int j) {
        Collections.<T>swap(l, i, j);
    }
    private void getProducts(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
        groupsQuery.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Products> products = new ArrayList<>();

                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){

                            Products product = new Products();
                            product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                            product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                            product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                            product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                            product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                            product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                            product.salePrice = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                            product.categorie =queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY);
                            product.id = queryDocumentSnapshot.getId();

                            if(!(product.categorie.equals("Incaltaminte") || product.categorie.equals("Accesorii"))){
                                products.add(product);}

                        }if(products.size() >=0 ){
                           setAdapter(products);
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void onProductClicked(Products products) {
        Intent i= new Intent(getContext(), ProductPageActivity.class);
        i.putExtra(Constants.KEY_PRODUCT, products);
        startActivity(i);

    }

    private void setAdapter(List<Products> products){
        ProductAdapter productAdapter =new ProductAdapter(products, this);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);

        binding.salesProducts.setLayoutManager(gridLayoutManager);
        binding.salesProducts.setAdapter(productAdapter);
        binding.salesProducts.setVisibility(View.VISIBLE);
    }

}