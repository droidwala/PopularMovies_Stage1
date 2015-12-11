package com.example.punit.popularmovies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.punit.popularmovies.Helpers.Movie;
import com.example.punit.popularmovies.R;

import java.util.ArrayList;

public class SearchListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<Movie> movies;
    public SearchListAdapter(Context context, int resource,ArrayList<Movie> movies) {
        super(context, resource);
        mContext = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.search_list_row,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.search_title);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(movies.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder{
        public TextView title;
    }
}
