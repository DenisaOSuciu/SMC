package com.upt.cti.smc.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upt.cti.smc.R;
import com.upt.cti.smc.adapter.SliderAdapter;
import com.upt.cti.smc.databinding.ActivityMainBinding;
import com.upt.cti.smc.utilities.Constants;
import com.upt.cti.smc.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private LinearLayout slideLayout;
    private TextView[] dots;

    private int myCurrentPage;
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager =new PreferenceManager(getApplicationContext());
            if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
                Intent i=new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(i);
                finish();
            }
            setContentView(binding.getRoot());


        ViewPager mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        slideLayout =(LinearLayout) findViewById(R.id.dots);

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(myCurrentPage+1);
            }
        });


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(myCurrentPage-1);
            }
        });
        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LogActivity.class);
                startActivity(i);
            }
        });
       
        SliderAdapter sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDots(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    public void addDots(int position){
        dots=new TextView[3];
        slideLayout.removeAllViews();

        for(int i=0; i<dots.length; i++){
            dots[i]= new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            slideLayout.addView(dots[i]);
        }

        if(dots.length >0){
            dots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener =new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1){

       }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            myCurrentPage = position;

            if(position == 0){
                binding.nextBtn.setEnabled(true);
                binding.back.setEnabled(false);
                binding.finishBtn.setEnabled(false);
                binding.back.setVisibility(View.INVISIBLE);
                binding.finishBtn.setVisibility(View.INVISIBLE);
                binding.nextBtn.setVisibility(View.VISIBLE);
                binding.nextBtn.setText("Inainte");
                binding.back.setText("");
            }
           else if(position == 1){
                binding.nextBtn.setEnabled(true);
                binding.back.setEnabled(true);
                binding.finishBtn.setEnabled(false);
                binding.finishBtn.setVisibility(View.INVISIBLE);
                binding.back.setVisibility(View.VISIBLE);
                binding.nextBtn.setVisibility(View.VISIBLE);
                binding.nextBtn.setText("Inainte");
               binding.back.setText("Inapoi");
            }
           else if(position == 2){
                binding.nextBtn.setEnabled(false);
                binding.back.setEnabled(true);
                binding.finishBtn.setEnabled(true);
                binding.nextBtn.setVisibility(View.INVISIBLE);
                binding.finishBtn.setEnabled(true);
                binding.finishBtn.setVisibility(View.VISIBLE);
                binding.finishBtn.setText("Incepeti");
                binding.back.setText("Inapoi");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


    };
}