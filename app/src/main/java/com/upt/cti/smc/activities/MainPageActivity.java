package com.upt.cti.smc.activities;

import static android.util.Base64.decode;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.upt.cti.smc.databinding.ActivityMainPageBinding;
import com.upt.cti.smc.fragments.AllProductsFragment;
import com.upt.cti.smc.fragments.InformationsFragment;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

public class MainPageActivity extends AppCompatActivity {
    ActivityMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.welcomeName.setText("Bine ai venit, " + preferenceManager.getString(Constants.KEY_PRENAME) + "!");
        byte[] bytes = decode(preferenceManager.getString(Constants.KEY_IMAGE), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.image.setImageBitmap(bitmap);

        binding.image.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));


        binding.categorii.toateCategoriile.setOnClickListener(v -> open("toate produsele"));
        binding.red.reduceriBox.setOnClickListener(v -> open("reduceri"));
        binding.despre.informatii.setOnClickListener(v-> openDetails());
        binding.categorii.imbracaminte.setOnClickListener(v -> open("imbracaminte"));
        binding.categorii.incaltaminte.setOnClickListener(v -> open("incaltaminte"));
        binding.categorii.accesorii.setOnClickListener(v -> open("accesorii"));
        binding.rochii.rochiiBox.setOnClickListener(v -> open("rochii"));



    }

    @SuppressLint("ResourceType")
    public void open(String fragment){
        Bundle bundle = new Bundle();
        bundle.putString("message", fragment);
        AllProductsFragment fragobj = new AllProductsFragment();
        fragobj.setArguments(bundle);

        FrameLayout fragmentLayout = new FrameLayout(this);
        fragmentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentLayout.setId(1000);
        setContentView(fragmentLayout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(1000, fragobj).commit();
    }


    public void homeClicked(android.view.View view) {
        Intent i = new Intent(MainPageActivity.this, MainPageActivity.class);
        startActivity(i);
    }

    public void cartClicked(android.view.View view) {
        Intent i = new Intent(MainPageActivity.this, CartActivity.class);
        startActivity(i);
    }

    public void favoriteClicked(android.view.View view) {
        open("favorite");
    }

    public void chatClicked(android.view.View view) {
        Intent i = new Intent(MainPageActivity.this, ChatActivity.class);
        startActivity(i);
    }
    public void myProductsClicked(android.view.View view) {
        Intent i = new Intent(MainPageActivity.this, MyProductsActivity.class);
        startActivity(i);
    }



    @SuppressLint("ResourceType")
    private void openDetails() {
        FrameLayout fragmentLayout = new FrameLayout(this);
        fragmentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentLayout.setId(1000);
        setContentView(fragmentLayout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(1000, new InformationsFragment()).commit();  // 1000 - is the id set for the container layout

    }


}