package com.upt.cti.smc.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upt.cti.smc.R;
import com.upt.cti.smc.activities.MainPageActivity;
import com.upt.cti.smc.databinding.FragmentInformationsBinding;


public class InformationsFragment extends Fragment {
    FragmentInformationsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentInformationsBinding.inflate(getLayoutInflater());
        binding.back.setOnClickListener(v-> startActivity(new Intent(getContext(), MainPageActivity.class)));
        // Inflate the layout for this fragment
        return binding.getRoot();
    }



}