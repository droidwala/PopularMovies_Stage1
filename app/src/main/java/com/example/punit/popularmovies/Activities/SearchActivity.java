package com.example.punit.popularmovies.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.punit.popularmovies.Adapters.SearchListAdapter;
import com.example.punit.popularmovies.Application.AppController;
import com.example.punit.popularmovies.Helpers.Movie;
import com.example.punit.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar tbar;
    @Bind(R.id.search_res_list) ListView lv;
    @Bind(R.id.progressBar) ProgressBar pbar;
    @Bind(R.id.no_result_txt) TextView error_msg;
    EditText search_bar;
    private static String SEARCH_BASE_URL ="http://api.themoviedb.org/3/search/movie?query=";
    private static String SEARCH_END_URL ="&api_key=";
    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private static String TAG="SEARCH";
    private ArrayList<Movie> movies;
    SearchListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        movies = new ArrayList<Movie>();
        search_bar = (EditText) tbar.findViewById(R.id.searchbar);

        //setting up toolbar
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Open detail activity screen on row click inside listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this,DetailActivity.class);
                Bundle b = new Bundle();
                Movie movie  = movies.get(position);
                b.putParcelable("MOVIE",movie);
                i.putExtras(b);
                startActivity(i);
            }
        });

        //TextWatcher to make Search Movies API Call on Edittext's text change..
        search_bar.addTextChangedListener(new TextWatcher() {
            Timer timer;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            // Delay using Timer and count logic implemented to reduce the no.of API calls made while user is typing
            @Override
            public void onTextChanged(final CharSequence charSequence, int start, int before, int count) {
                if(count > 2) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SearchActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timer.cancel();
                                    AppController.getInstance().cancelPendingRequests(TAG);
                                    try {
                                        String query =  URLEncoder.encode(charSequence.toString(), "utf-8");
                                        Log.d("TAG","Do we come inside textchange");
                                        if(charSequence.length()>0)
                                        FetchData(SEARCH_BASE_URL + query + SEARCH_END_URL + getResources().getString(R.string.api_key));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    }, 2000);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    //Cancel pending request on pause.
    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests(TAG);
        pbar.setVisibility(View.INVISIBLE);
    }

    //Search API call made using Volley
    private void FetchData(String URL){

        hideElements();
        error_msg.setVisibility(View.INVISIBLE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)  {
                 try {
                    movies.clear();//clear out old results.
                    JSONArray jsonArray = response.getJSONArray("results");
                    if(jsonArray.length()>0){

                        for(int i=0;i < jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Movie member = new Movie();
                            member.setId(jsonObject.getString("id"));
                            member.setTitle(jsonObject.getString("original_title"));
                            member.setPoster(IMAGE_BASE_URL + jsonObject.getString("poster_path"));
                            member.setBackdrop(IMAGE_BASE_URL + jsonObject.getString("backdrop_path"));
                            member.setPlot(jsonObject.getString("overview"));
                            member.setVotes(jsonObject.getString("vote_count"));
                            member.setRating(jsonObject.getDouble("vote_average"));
                            member.setRelease_date(jsonObject.getString("release_date"));
                            movies.add(member);

                        }
                    }
                    else{
                        error_msg.setVisibility(View.VISIBLE); //show error message in case zero results are returned for entered text.
                        error_msg.setText("Sorry,No results found by that name :(");
                    }
                  }
                 catch (JSONException e) {
                    e.printStackTrace();
                }
                showElements();
                adapter = new SearchListAdapter(SearchActivity.this,R.layout.search_list_row,movies);
                lv.setAdapter(adapter);
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                pbar.setVisibility(View.INVISIBLE);
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("Uh-Oh!!\nSomething bad happened..why don't you try again? :)");
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }

    //Hides Listview  and shows progressBar while API call is going on.
    private void hideElements(){
        lv.setVisibility(View.INVISIBLE);
        pbar.setVisibility(View.VISIBLE);
    }

    //As soon as JSON parsing of results is done we show back listview and hide progressBar
    private void showElements(){
        lv.setVisibility(View.VISIBLE);
        pbar.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.clear_txt: //used to clear edittext
                AppController.getInstance().cancelPendingRequests(TAG);
                pbar.setVisibility(View.INVISIBLE);
                error_msg.setVisibility(View.INVISIBLE);
                search_bar.setText("");
                movies.clear();
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
