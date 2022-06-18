package com.upt.cti.smc.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.activities.MainPageActivity;
import com.upt.cti.smc.activities.ProductPageActivity;
import com.upt.cti.smc.adapter.ProductAdapter;
import com.upt.cti.smc.databinding.FragmentAllProductsBinding;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AllProductsFragment extends Fragment implements ProductListener {

    PreferenceManager preferenceManager;
    FragmentAllProductsBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferenceManager =new PreferenceManager(getContext());
        binding = FragmentAllProductsBinding.inflate(getLayoutInflater());

        String myValue = this.getArguments().getString("message");

        binding.backBtn.setOnClickListener(v-> startActivity(new Intent(getContext(), MainPageActivity.class)));


       // binding.searchButton.setOnClickListener(v-> afisareProduse(binding.search.getText().toString()));





        if(myValue.equals("toate produsele")){
        afisareProduse("");
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
                                        product.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                        product.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                        product.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                        product.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                        product.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                        product.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                        product.salePrice = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                        product.id = queryDocumentSnapshot.getId();
                                        product.sellerID = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER_ID);
                                        if(option.equals(queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY))){
                                            products.add(product);}

                                    } if(option.equals("Toate produsele")){
                                          afisareProduse("");
                                    }
                                    setAdapter(products);
                                }
                            });
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                     afisareProduse("");
                }

            });
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String option=binding.spinner.getSelectedItem().toString();
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
                                        product.id = queryDocumentSnapshot.getId();
                                        product.sellerID =queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER_ID);
                                        if (!queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER).equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))) {
                                            products.add(product);
                                        }

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
                                          afisareProduse("");
                                    }
                                    setAdapter(products);

                                }
                            });

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                       afisareProduse("");
                }
            });
            binding.searchButton.setOnClickListener(v-> afisareProduse(binding.search.getText().toString()));

               }


      if(myValue.equals("reduceri")){
          afisareReduceri("");
        binding.categorii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  String option = binding.categorii.getSelectedItem().toString();
                  FirebaseFirestore db = FirebaseFirestore.getInstance();
                  Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
                  groupsQuery
                          .get()
                          .addOnCompleteListener( task -> {
                              if (task.isSuccessful() && task.getResult() != null) {
                                  List<Products> red = new ArrayList<>();

                                  for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){

                                      Products reducere = new Products();
                                      reducere.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                      reducere.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                      reducere.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                      reducere.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                      reducere.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                      reducere.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                      reducere.salePrice= queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                      reducere.id = queryDocumentSnapshot.getId();

                                      if(!reducere.salePrice.equals("")) {
                                          if (!queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER).equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))) {
                                              if (option.equals(queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY))) {
                                                  red.add(reducere);
                                              }

                                              if (option.equals("Toate produsele")) {
                                                  afisareReduceri("");
                                              }
                                              setAdapter(red);
                                          }
                                      }}}  });
                                  }
                                  @Override
                                  public void onNothingSelected(AdapterView<?> parent) {
                                      afisareProduse("");
                                  }

                              });

          binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  String option=binding.spinner.getSelectedItem().toString();
                  FirebaseFirestore db = FirebaseFirestore.getInstance();
                  Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
                  groupsQuery
                          .get()
                          .addOnCompleteListener( task -> {
                              if (task.isSuccessful() && task.getResult() != null) {
                                  List<Products> red = new ArrayList<>();

                                  for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){

                                      Products reducere = new Products();
                                      reducere.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                      reducere.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                      reducere.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                      reducere.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                      reducere.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                      reducere.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                      reducere.salePrice= queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                      reducere.id = queryDocumentSnapshot.getId();

                                      if(!reducere.salePrice.equals("")) {
                                          if (!queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER).equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))) {
                                                  red.add(reducere);

                                          }
                                      }
                                  }if(red.size() >0 ){
                                      setAdapter(red);
                                  }
                                  else{

                                  }

                                  int n = red.size();

                                  if (option.equals("Preț crescător")) {
                                      for (int i = 0; i < n - 1; i++)
                                          for (int j = 0; j < n - i - 1; j++)

                                              if (Integer.parseInt(red.get(j).pret) > Integer.parseInt(red.get(j + 1).pret)) {
                                                  swap(red, j, j + 1);
                                              }
                                  } else if (option.equals("Preț descrescător")) {
                                      for (int i = 0; i < n - 1; i++)
                                          for (int j = 0; j < n - i - 1; j++)

                                              if (Integer.parseInt(red.get(j).pret) < Integer.parseInt(red.get(j + 1).pret)) {
                                                  swap(red, j, j + 1);
                                              }
                                  } else if (option.equals("După relevanță") || option.equals("Sortează produsele")) {
                                      afisareReduceri("");
                                  }
                                  setAdapter(red);

                              }
                          });

              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {
                  afisareReduceri("");
              }
          });
          binding.searchButton.setOnClickListener(v-> afisareReduceri(binding.search.getText().toString()));



        }


        else if(myValue.equals("favorite")){
            binding.spinner.setVisibility(View.INVISIBLE);
            binding.categorii.setVisibility(View.INVISIBLE);
            afisareFavorite();
        }
        return binding.getRoot();

    }

    public void afisareFavorite(){

        FirebaseFirestore database =FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_FAVORITE)
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Products> favs = new ArrayList<>();

                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            Products favorit = new Products();
                            favorit.nume = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_NAME);
                            favorit.imagine = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_IMAGE);
                            favorit.descriere = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_DESCRIPTION);
                            favorit.pret = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_PRICE);
                            favorit.marime = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_SIZE);
                            favorit.seller = queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_SELLER);
                            favorit.salePrice= queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_SALE_PRICE);
                            favorit.id = queryDocumentSnapshot.getId();
                            if(preferenceManager.getString(Constants.KEY_NAME).equals(queryDocumentSnapshot.getString(Constants.KEY_FAVORITE_ADDED_FROM_USER))){
                                favs.add(favorit);}


                        }if(favs.size() >=0 ){
                            ProductAdapter productAdapter =new ProductAdapter(favs, this);
                            GridLayoutManager gridLayoutManager =new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                            binding.allProducts.setLayoutManager(gridLayoutManager);
                            binding.allProducts.setAdapter(productAdapter);
                            binding.allProducts.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }


    public  final <T> void swap (List<T> l, int i, int j) {
        Collections.<T>swap(l, i, j);
    }
        public void afisareReduceri(String searchWord){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
            groupsQuery
                    .get()
                    .addOnCompleteListener( task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Products> red = new ArrayList<>();

                            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){

                                Products reducere = new Products();
                                reducere.nume = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
                                reducere.imagine = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE);
                                reducere.descriere = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
                                reducere.pret = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
                                reducere.marime = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SIZE);
                                reducere.seller = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER);
                                reducere.salePrice= queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                                reducere.id = queryDocumentSnapshot.getId();

                                if(!reducere.salePrice.equals("")) {
                                    if (!queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER).equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))) {
                                        if (reducere.nume.toLowerCase().contains(searchWord.toLowerCase()) || reducere.descriere.toLowerCase().contains(searchWord.toLowerCase())) {
                                            red.add(reducere);
                                        }
                                    }
                                }
                            }if(red.size() >0 ){
                                setAdapter(red);
                            }
                            else{

                            }
                        }
                    });
        }

    public void setAdapter(List<Products> products){
        ProductAdapter productAdapter =new ProductAdapter(products, this);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.allProducts.setLayoutManager(gridLayoutManager);
        binding.allProducts.setAdapter(productAdapter);
        binding.allProducts.setVisibility(View.VISIBLE);
    }


    public void onProductClicked(Products products) {
        Intent i= new Intent(getContext(), ProductPageActivity.class);
        i.putExtra(Constants.KEY_PRODUCT, products);
        startActivity(i);
   }

    public void afisareProduse(String searchWord){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query groupsQuery = db.collectionGroup(Constants.KEY_COLLECTION_PRODUCTS);
        groupsQuery
                .get()
                .addOnCompleteListener( task -> {
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
                            product.salePrice= queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE_SALE);
                            product.sellerID =queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER_ID);
                            product.id = queryDocumentSnapshot.getId();
                            if(!queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_SELLER).equals(preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_PRENAME))){
                            if(product.nume.toLowerCase().contains(searchWord.toLowerCase()) || product.descriere.toLowerCase().contains(searchWord.toLowerCase())){
                            products.add(product);}}

                        }if(products.size() >=0 ){
                            setAdapter(products);
                        }
                    }
                });
    }

}

