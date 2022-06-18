package com.upt.cti.smc.activities;

import static android.util.Base64.decode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.ActivityMyProductBinding;
import com.upt.cti.smc.model.Products;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MyProductActivity extends AppCompatActivity {
    ActivityMyProductBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMyProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        Products product = (Products) getIntent().getSerializableExtra(Constants.KEY_PRODUCT);
        binding.numeView.setText(product.nume);
        binding.descriereView.setText(product.descriere);
        binding.pretView.setText("Pret: "  +product.pret);
        binding.marimeView.setText("Marime: "  +product.marime);
      //  binding.categorieView.setText("Categorie: " +product.categorie);
        binding.back.setOnClickListener(v-> onBackPressed());

        binding.editBtn.setOnClickListener(v->
                {
                    binding.numeView.setVisibility(View.INVISIBLE);
                    binding.descriereView.setVisibility(View.INVISIBLE);
                    binding.pretView.setVisibility(View.INVISIBLE);
                    binding.marimeView.setVisibility(View.INVISIBLE);
                    binding.categorieView.setVisibility(View.INVISIBLE);
                    binding.editBtn.setVisibility(View.INVISIBLE);


                    binding.numeEdit.setVisibility(View.VISIBLE);
                    binding.descriereEdit.setVisibility(View.VISIBLE);
                    binding.pretEdit.setVisibility(View.VISIBLE);
                    binding.marimeEdit.setVisibility(View.VISIBLE);
                    binding.categorieEdit.setVisibility(View.VISIBLE);
                    binding.saveBtn.setVisibility(View.VISIBLE);
                    binding.numeEdit.setText(product.nume);
                    binding.descriereEdit.setText(product.descriere);
                    binding.pretEdit.setText(product.pret);
                    binding.marimeEdit.setText(product.marime);

                }

                );
        binding.saveBtn.setOnClickListener(v-> updateData());


        byte[] bytes;
        bytes = decode(product.imagine, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imagine.setImageBitmap(bitmap);


    }

    public void updateData(){
        Map<String, Object> update =new HashMap<>();
        update.put(Constants.KEY_PRODUCT_NAME, binding.numeEdit.getText().toString());
        update.put(Constants.KEY_PRODUCT_DESCRIPTION, binding.descriereEdit.getText().toString());
        update.put(Constants.KEY_PRODUCT_SIZE, binding.marimeEdit.getText().toString());
        if(Integer.parseInt(binding.pretEdit.getText().toString())<Integer.parseInt(preferenceManager.getString(Constants.KEY_PRODUCT_PRICE))){
            update.put(Constants.KEY_PRODUCT_PRICE_SALE, binding.pretEdit.getText().toString());
        }
        update.put(Constants.KEY_PRODUCT_PRICE, binding.pretEdit.getText().toString());
        update.put(Constants.KEY_PRODUCT_CATEGORY, binding.categorieEdit.getSelectedItem().toString());


        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_PRODUCTS)
                .get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).collection(Constants.KEY_COLLECTION_PRODUCTS)
                                .document(preferenceManager.getString(Constants.KEY_PRODUCT_ID))
                                .update(update)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @SuppressLint("SetTextI18n")
                                                          @Override
                                                          public void onSuccess(Void unused) {
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_PRICE_SALE, binding.pretEdit.getText().toString());
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_NAME, binding.numeEdit.getText().toString());
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_DESCRIPTION, binding.descriereEdit.getText().toString());
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_SIZE, binding.marimeEdit.getText().toString());
                                                                  if(Integer.parseInt(binding.pretEdit.getText().toString())<Integer.parseInt(preferenceManager.getString(Constants.KEY_PRODUCT_PRICE))){
                                                                      preferenceManager.putString(Constants.KEY_PRODUCT_PRICE_SALE, binding.pretEdit.getText().toString());
                                                                  }
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_PRICE, binding.pretEdit.getText().toString());
                                                                  preferenceManager.putString(Constants.KEY_PRODUCT_CATEGORY, binding.categorieEdit.getSelectedItem().toString());


                                                              binding.numeView.setText( binding.numeEdit.getText().toString());
                                                              binding.descriereView.setText(binding.descriereEdit.getText().toString());
                                                              binding.pretView.setText("Pret: "  +binding.pretEdit.getText().toString());
                                                                binding.categorieView.setText("Categorie: " + binding.categorieEdit.getSelectedItem().toString());
                                                              binding.marimeView.setText("Marime: "  +binding.marimeEdit.getText().toString());
                                                              }
                                                          }

                                    ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }

                    }

        );


        binding.numeView.setVisibility(View.VISIBLE);
        binding.descriereView.setVisibility(View.VISIBLE);
        binding.pretView.setVisibility(View.VISIBLE);
        binding.marimeView.setVisibility(View.VISIBLE);
        binding.categorieView.setVisibility(View.VISIBLE);
        binding.editBtn.setVisibility(View.VISIBLE);


        binding.numeEdit.setVisibility(View.INVISIBLE);
        binding.descriereEdit.setVisibility(View.INVISIBLE);
        binding.pretEdit.setVisibility(View.INVISIBLE);
        binding.marimeEdit.setVisibility(View.INVISIBLE);
        binding.categorieEdit.setVisibility(View.INVISIBLE);
        binding.saveBtn.setVisibility(View.INVISIBLE);







    }



}