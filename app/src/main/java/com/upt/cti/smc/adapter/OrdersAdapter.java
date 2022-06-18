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
import com.upt.cti.smc.databinding.ItemOrderBinding;
import com.upt.cti.smc.listeners.OrderListener;
import com.upt.cti.smc.model.Order;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>{

    private final List<Order> orders;
    private final OrderListener orderListener;
    FirebaseFirestore database;
   PreferenceManager preferenceManager;

    public OrdersAdapter(List<Order> orders, OrderListener orderListener) {
        this.orders = orders;
        this.orderListener = orderListener;
        database = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public OrdersAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding itemOrderBinding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        preferenceManager = new PreferenceManager(parent.getContext());
        return new OrdersAdapter.OrderViewHolder(itemOrderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setOrderData(orders.get(position));

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
                collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_RECEIVED_ORDERS)
                        .document(orders.get(position).getDocumentID())
                        .delete().addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    orders.remove(orders.get(position));
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
        return orders.size();
    }


    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderBinding binding;


        OrderViewHolder(ItemOrderBinding itemOrderBinding) {
            super(itemOrderBinding.getRoot());
            binding =itemOrderBinding;
        }

        void setOrderData(Order order){

            binding.productName.setText(order.product_name);
            binding.buyerName.setText(order.buyer_name);
            binding.buyerAddress1.setText(order.buyer_address1);
            binding.buyerAddress2.setText(order.buyer_addres2);
            binding.price.setText(order.product_size);
            binding.phone.setText(order.buyer_phone);
            binding.image.setImageBitmap(getProductPhoto(order.product_photo));

            binding.getRoot().setOnClickListener( v-> orderListener.onOrderClicked(order));
        }
        private Bitmap getProductPhoto(String encodedImage){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

}
