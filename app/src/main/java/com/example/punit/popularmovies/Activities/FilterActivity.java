package com.example.punit.popularmovies.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.punit.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar tbar;
    @Bind(R.id.toolbar_txt) TextView tbar_title;
    @Bind(R.id.apply_filters) Button apply_filter;
    @Bind(R.id.sort_group) RadioGroup rg;
    @Bind(R.id.popularity_sort) RadioButton popularity_sort;
    @Bind(R.id.rate_sort) RadioButton rating_sort;
    MenuItem reset;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String PREFS_NAME="SORT_CRITERIA";
    private static final String SORT_POPULAR="POPULAR";
    private static final String SORT_RATING ="RATING";
    private static final String TITLE="FILTER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        //setting up toolbar title,home indicator,etc.
        setSupportActionBar(tbar);
        tbar_title.setText(TITLE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);

        //setting up radio button and apply filter button depending on preference values
        preferences = getSharedPreferences(PREFS_NAME,0);
        popularity_sort.setChecked(preferences.getBoolean(SORT_POPULAR,false));
        rating_sort.setChecked(preferences.getBoolean(SORT_RATING,false));
        if(popularity_sort.isChecked() || rating_sort.isChecked()){
            apply_filter.setVisibility(View.VISIBLE);
        }

        //Handling Radio button clicks.
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                apply_filter.setVisibility(View.VISIBLE);
                reset.setVisible(true);
            }
        });

        //Apply Filter button click handling
        apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
               // if popularity is checked then save the same in preferences and send returnIntent with Sort details to MainActivity
               if(popularity_sort.isChecked()){
                   editor = preferences.edit();
                   editor.putBoolean(SORT_RATING,false);
                   editor.putBoolean(SORT_POPULAR,true);
                   editor.apply();
                   returnIntent.putExtra("SORT","POPULARITY");
                   setResult(Activity.RESULT_OK,returnIntent);
                   finish();
               }
               //similarly for Rating radio button
               else if (rating_sort.isChecked()){
                   editor = preferences.edit();
                   editor.putBoolean(SORT_POPULAR,false);
                   editor.putBoolean(SORT_RATING,true);
                   editor.apply();
                   returnIntent.putExtra("SORT","RATING");
                   setResult(Activity.RESULT_OK,returnIntent);
                   finish();
               }

            }
        });
    }


    //Inflates menu and shows reset menu item depending whether radio button's are checked or not.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reset_sort,menu);
        reset = menu.findItem(R.id.reset);
        if(!(popularity_sort.isChecked()||rating_sort.isChecked())){
              reset.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //Menu items click handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.reset:
                rg.clearCheck();//Reset radio group on reset click
                reset.setVisible(false);//make reset and apply filter button invisible
                apply_filter.setVisibility(View.INVISIBLE);
                if(!(preferences.getBoolean(SORT_POPULAR,false) || preferences.getBoolean(SORT_RATING,false))){
                   finish();
                }
                else{
                    editor = preferences.edit();//clear out all preferences once reset is clicked.
                    editor.clear();
                    editor.apply();
                    Intent i = new Intent();
                    i.putExtra("SORT","RESET");
                    setResult(Activity.RESULT_OK,i);
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
