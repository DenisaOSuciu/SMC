package com.upt.cti.smc.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivityProductPageBinding;
import com.upt.cti.smc.databinding.FragmentSellerBinding;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.model.Users;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class SellerFragment extends Fragment {
    FragmentSellerBinding binding;
    private PreferenceManager preferenceManager;

    private Products product;
    private Users user;
    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentSellerBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getContext());
        database = FirebaseFirestore.getInstance();
        product = (Products) getActivity().getIntent().getSerializableExtra(Constants.KEY_PRODUCT);
     //   user = (Users) getActivity().getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.numeSeller.setText(product.seller);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                            Users users = new Users();
                            users.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);

                            if (users.name.equals(product.seller)) {
                                binding.usernameSeller.setText(users.username);
                            }
                        } }});

       return binding.getRoot();
         }

    public void openDialog() {
        final Dialog dialog = new Dialog(getContext()); // Context, this, etc.
        dialog.setContentView(R.layout.fragment_seller);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //  dialog.setTitle("Introdu noua adresă");
        dialog.show();
    }
}