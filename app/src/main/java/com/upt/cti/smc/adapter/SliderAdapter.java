package com.upt.cti.smc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.upt.cti.smc.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context=context;
    }

    public int[] slide_image={
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo
    };

    public String[] slide_title={
            "SHOP",
            "SELL",
            "BE SUSTAINABLE"
    };

    public String[] slide_description={
            "Shop from other users and bring some new amazing pieces to your wardrobe.",
            "Declutter your closet and let others enjoy the pieces you don't want anymore.",
            "Reusing clothes and accesories has a big positive impact on our planet. Stop fast fashion."
    };

    @Override
    public int getCount() {
        return slide_title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== (RelativeLayout) object;
    }


    @Override
    public Object instantiateItem( ViewGroup container, int position) {
        layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide_layout,container,false);


        TextView slideHeading=(TextView) view.findViewById(R.id.slide_title);
        TextView slideDescription=(TextView) view.findViewById(R.id.slide_description);


        ImageView slideImageView =(ImageView) view.findViewById(R.id.slide_image);

        slideImageView.setImageResource(slide_image[position]);
        slideHeading.setText(slide_title[position]);
        slideDescription.setText(slide_description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
