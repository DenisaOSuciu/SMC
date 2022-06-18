package com.upt.cti.smc.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.databinding.ItemContainerInCartProductBinding;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.MyProductsViewHolder> {

        private final List<Products> myProducts;
        private final ProductListener productListener;
        FirebaseFirestore database;
        PreferenceManager preferenceManager;


        public MyProductsAdapter(List<Products> products, ProductListener productListener) {

            this.myProducts = products;
            this.productListener = productListener;
            database = FirebaseFirestore.getInstance();

        }

        @NonNull
        @Override
        public MyProductsAdapter.MyProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemContainerInCartProductBinding productRowItemBinding = ItemContainerInCartProductBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            preferenceManager = new PreferenceManager(parent.getContext());
            return new MyProductsAdapter.MyProductsViewHolder(productRowItemBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull MyProductsAdapter.MyProductsViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.setProductData(myProducts.get(position));

            holder.binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
                    collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_ADDED_TO_CART)
                            .document(myProducts.get(position).getDocumentID())
                            .delete().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        myProducts.remove(myProducts.get(position));
                                        notifyDataSetChanged();
                                        Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(view.getContext(), "Fail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }


        @Override
        public int getItemCount() {
            return myProducts.size();
        }


        class MyProductsViewHolder extends RecyclerView.ViewHolder {
            ItemContainerInCartProductBinding binding;


            MyProductsViewHolder(ItemContainerInCartProductBinding itemContainerInCartProductBinding) {
                super(itemContainerInCartProductBinding.getRoot());
                binding = itemContainerInCartProductBinding;

            }

            void setProductData(Products products) {
                binding.name.setText(products.nume);
                binding.pret.setText(products.pret);

                binding.marime.setText(products.marime);
                binding.image.setImageBitmap(getProductPhoto(products.imagine));
                binding.getRoot().setOnClickListener(v -> productListener.onProductClicked(products));


            }

            private Bitmap getProductPhoto(String encodedImage) {
                byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }


        }

    }



