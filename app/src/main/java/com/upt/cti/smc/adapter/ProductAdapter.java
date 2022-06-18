package com.upt.cti.smc.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.R;
import com.upt.cti.smc.activities.CartActivity;
import com.upt.cti.smc.databinding.ProductRowItemBinding;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.Toast;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

        private  List<Products> products;
        private final ProductListener productListener;



        public ProductAdapter(List<Products> products, ProductListener productListener) {

            this.products=products;
            this.productListener = productListener;

        }

    @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           ProductRowItemBinding productRowItemBinding =ProductRowItemBinding.inflate(
                   LayoutInflater.from(parent.getContext()), parent, false);
           return new ProductViewHolder(productRowItemBinding);

        }


        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.setProductData(products.get(position));
        }

        @Override
        public int getItemCount() {
            return products.size();
        }


                    class ProductViewHolder extends RecyclerView.ViewHolder{
                      ProductRowItemBinding binding;



                           ProductViewHolder(ProductRowItemBinding productRowItemBinding) {
                            super(productRowItemBinding.getRoot());
                             binding =productRowItemBinding;
                            }

                            void setProductData(Products products){
                               binding.name.setText(products.nume);

                               if(products.salePrice.equals("")){
                           binding.pret.setText(products.pret);}
                               else {
                                   binding.pret.setText(products.salePrice);
                                   binding.pret.setTextColor(Color.RED);
                               }
                               binding.favorite.setOnClickListener(v-> binding.favorite.setBackgroundResource(R.drawable.ic_favorite_red));

                           binding.marime.setText(products.marime);
                           binding.image.setImageBitmap(getProductPhoto(products.imagine));
                           binding.getRoot().setOnClickListener( v-> productListener.onProductClicked(products));
            }




        }

        private Bitmap getProductPhoto(String encodedImage){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }




    }

