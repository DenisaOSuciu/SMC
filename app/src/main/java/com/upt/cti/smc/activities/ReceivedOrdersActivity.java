package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.smc.R;
import com.upt.cti.smc.adapter.CartProductsAdapter;
import com.upt.cti.smc.adapter.OrdersAdapter;
import com.upt.cti.smc.databinding.ActivityReceivedOrdersBinding;
import com.upt.cti.smc.listeners.OrderListener;
import com.upt.cti.smc.listeners.ProductListener;
import com.upt.cti.smc.model.Order;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ReceivedOrdersActivity extends AppCompatActivity implements OrderListener {
    ActivityReceivedOrdersBinding binding;
    PreferenceManager preferenceManager;
    List<Order> orderList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReceivedOrdersBinding.inflate(getLayoutInflater());
        preferenceManager=  new PreferenceManager(getApplicationContext());
        orderList = new ArrayList<>();
        binding.backBtn.setOnClickListener(v-> onBackPressed());
        getProducts();
        setContentView(binding.getRoot());
    }

    private void getProducts() {
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_USERS);
        collectionReference.document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_RECEIVED_ORDERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {


                        for (DocumentSnapshot queryDocumentSnapshot : task.getResult().getDocuments()) {
                            Order order = new Order();
                            String documentID = queryDocumentSnapshot.getId();
                            order.setDocumentID(documentID);
                            order.product_name = queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_NAME);
                            order.buyer_name = queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_BUYER_NAME);
                            order.buyer_address1 = queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS1);
                            order.buyer_addres2 = queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_BUYER_ADDRESS2);
                            order.buyer_phone = queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_BUYER_PHONE);
                            order.product_photo=queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_IMAGE);
                          order.product_size=queryDocumentSnapshot.getString(Constants.KEY_RECEIVED_ORDERS_PRODUCT_SIZE);
                            orderList.add(order);

                        }
                        if (orderList.size() > 0) {
                            OrdersAdapter ordersAdapter = new OrdersAdapter(orderList, this);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            binding.receivedProducts.setLayoutManager(layoutManager);
                            binding.receivedProducts.setAdapter(ordersAdapter);
                            binding.receivedProducts.setVisibility(View.VISIBLE);
                            binding.error.setVisibility(View.INVISIBLE);
                        }
                        else if(orderList.size() == 0){
                            binding.error.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    @Override
    public void onOrderClicked(Order order) {

    }
}