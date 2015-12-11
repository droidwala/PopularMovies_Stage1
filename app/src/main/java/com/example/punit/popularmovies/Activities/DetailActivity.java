package com.example.punit.popularmovies.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.punit.popularmovies.Helpers.Movie;
import com.example.punit.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar tbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.backdrop) ImageView backdrop;
    @Bind(R.id.plot) TextView plot;
    @Bind(R.id.rating) TextView ratings;
    @Bind(R.id.votes) TextView votes;
    @Bind(R.id.release_date) TextView release_date;
    Movie movie;
    SimpleDateFormat format1,format2;
    Date date;
    String dateString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //Date Formatting for Release Date
        format1 = new SimpleDateFormat("yyyy-MM-dd");
        format2 = new SimpleDateFormat("dd-MMM-yyyy");

        //Toolbar setup
        setSupportActionBar(tbar);
        if(getSupportActionBar()!=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Restoring Movie during Orientation Changes.
        if(savedInstanceState!=null){
            movie = (Movie) savedInstanceState.getSerializable("Movie");
        }
        else {
            movie = getIntent().getParcelableExtra("MOVIE");
        }

        //setting up values of all views using movie object.

        collapsingToolbarLayout.setTitle(movie.getTitle());
        release_date.setText(DateConversion(movie.getRelease_date()));
        loadBackdrop();
        plot.setText(movie.getPlot());
        ratings.setText(String.valueOf((int)(movie.getRating()*10) + "%"));//converting rating to percentage format.
        votes.setText(movie.getVotes() + " votes");

    }



    private void loadBackdrop(){
        Picasso.with(this).load(movie.getPoster()).into(backdrop);
    }

    //converts "yyyy-MM-dd" date format received in json results to "dd-MMM-yyyy"
    private String DateConversion(String release_date){
        try {
            date = format1.parse(release_date);
            dateString = format2.format(date);
            dateString = dateString.replace("-"," ");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_share: //Share Intent to share movie details
                StringBuilder sb = new StringBuilder();
                sb.append("Movie Name: " + movie.getTitle() + "\n");
                sb.append("Plot: " + movie.getPlot() + "\n");
                sb.append("Release Date: " + DateConversion(movie.getRelease_date())+ "\n");
                sb.append("Votes Count: " + movie.getVotes() + "\n");
                sb.append("Rating: " + String.valueOf((int) (movie.getRating() * 10) + "%") + "\n");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,sb.toString());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,movie.getTitle());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,new String[]{"punitdama@gmail.com"});
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Save Movie object during orientation changes.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Movie",movie);
    }
}
