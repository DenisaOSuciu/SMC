package com.upt.cti.smc.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upt.cti.smc.R;
import com.upt.cti.smc.databinding.FragmentEditProfileBinding;
import com.upt.cti.smc.databinding.FragmentUserInformationsBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

public class UserInformationsFragment extends Fragment {
    PreferenceManager preferenceManager;
    FragmentUserInformationsBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentUserInformationsBinding.inflate(getLayoutInflater());


        preferenceManager = new PreferenceManager(getContext());


        String adr= preferenceManager.getString(Constants.KEY_ADRESS2)
                + "\n" + preferenceManager.getString(Constants.KEY_CITY)
                + "\n" + preferenceManager.getString(Constants.KEY_STATE)
                + "\n" + preferenceManager.getString(Constants.KEY_ZIPCODE);
        binding.streed.setText(preferenceManager.getString(Constants.KEY_ADRESS1));
        binding.adresa.setText(adr);

        binding.email.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.telefon.setText(preferenceManager.getString(Constants.KEY_PHONE));
        return binding.getRoot();
    }
}