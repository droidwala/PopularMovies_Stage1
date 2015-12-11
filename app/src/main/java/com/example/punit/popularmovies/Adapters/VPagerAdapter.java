package com.example.punit.popularmovies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.punit.popularmovies.Helpers.Cast;
import com.example.punit.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Cast> casts;


    public VPagerAdapter(Context context, ArrayList<Cast> casts){
        mContext = context;
        this.casts = casts;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = ((Activity)mContext).getLayoutInflater().inflate(R.layout.activity_cast_details,container,false);
        TextView tv = (TextView) v.findViewById(R.id.cast_name);
        ImageView iv = (ImageView) v.findViewById(R.id.cast_image);
        tv.setText(casts.get(position).getCast_name());
        Picasso.with(mContext)
                .load(casts.get(position).getCast_image())
                .placeholder(R.drawable.placeholder_icon)
                .into(iv);
        container.addView(v);
        return v;
    }

    @Override
    public float getPageWidth(int position) {
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return (0.33f);//this makes sure 3 pages are shown at time in landscape mode
        }
        else {
            return (0.5f);//this makes sure 2 pages are shown at time in portrait mode
        }
    }

    @Override
    public int getCount() {
        return casts.size();
    }



    @Override
    public boolean isViewFromObject(View view, Object object){
        return (view==object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

}
