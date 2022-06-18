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


public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder> {

    private final List<Products> products;
    private final ProductListener productListener;
    FirebaseFirestore database;
    PreferenceManager preferenceManager;

    public CartProductsAdapter(List<Products> products, ProductListener productListener) {

        this.products = products;
        this.productListener = productListener;
        database = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public CartProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerInCartProductBinding productRowItemBinding = ItemContainerInCartProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
         preferenceManager = new PreferenceManager(parent.getContext());
        return new CartProductsViewHolder(productRowItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull CartProductsAdapter.CartProductsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setProductData(products.get(position));
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
                collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_ADDED_TO_CART)
                        .document(products.get(position).getDocumentID())
                        .delete().addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    products.remove(products.get(position));
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
        return products.size();
    }


    class CartProductsViewHolder extends RecyclerView.ViewHolder {
        ItemContainerInCartProductBinding binding;


        CartProductsViewHolder(ItemContainerInCartProductBinding itemContainerInCartProductBinding) {
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


