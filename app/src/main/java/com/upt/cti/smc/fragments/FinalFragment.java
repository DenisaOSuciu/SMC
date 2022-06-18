package com.upt.cti.smc.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upt.cti.smc.activities.MainPageActivity;
import com.upt.cti.smc.databinding.FragmentFinalBinding;

public class FinalFragment extends Fragment {
    FragmentFinalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentFinalBinding.inflate(getLayoutInflater());

        binding.back.setOnClickListener(v-> startActivity(new Intent(getContext(), MainPageActivity.class)));


        return binding.getRoot();
    }
}