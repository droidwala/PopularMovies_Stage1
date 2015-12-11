package com.example.punit.popularmovies.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.punit.popularmovies.Activities.DetailActivity;
import com.example.punit.popularmovies.Activities.FilterActivity;
import com.example.punit.popularmovies.Activities.SearchActivity;
import com.example.punit.popularmovies.Adapters.GridAdapter;
import com.example.punit.popularmovies.Application.AppController;
import com.example.punit.popularmovies.Helpers.ConnectionDetector;
import com.example.punit.popularmovies.Helpers.Movie;
import com.example.punit.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PopularMoviesFragment extends Fragment {

    @Bind(R.id.gridview) GridView gridView;
    @Bind(R.id.progressBar) ProgressBar pbar;
    @Bind(R.id.rl_internet) RelativeLayout rl_internet;
    @Bind(R.id.rl_volley) RelativeLayout rl_volley;
    @Bind(R.id.retry) Button retry;
    @Bind(R.id.retry_volley) Button retry_volley;
    @Bind(R.id.volley_error_msg) TextView volley_error_msg;
    GridAdapter adapter;
    ArrayList<Movie> movies;
    private static String MOVIES_URL = "http://api.themoviedb.org/3/discover/movie?api_key=";
    private static String SORT_BY_POPULAR="http://api.themoviedb.org/3/movie/popular?api_key=";
    private static String SORT_BY_RATING="http://api.themoviedb.org/3/movie/top_rated?api_key=";
    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private static final String TAG="POPULAR";
    private static final String PREFS_NAME ="SORT_CRITERIA";
    private static final String SORT_POPULAR="POPULAR";
    private static final String SORT_RATING ="RATING";
    private static final int INTENT_REQ=99;
    private static final String TITLE= "Popular Movies";
    private MenuItem menu_item;
    TextView toolbar_title;
    SharedPreferences preferences;
    ConnectionDetector cd;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        cd = new ConnectionDetector(getActivity().getApplicationContext());
        preferences = getActivity().getSharedPreferences(PREFS_NAME,0);
        movies = new ArrayList<Movie>();
        //Restore ArrayList<movie> saved inside onSaveInstanceState during orientation changes
        if(savedInstanceState!=null && savedInstanceState.containsKey("popular_movie")) {
            movies = (ArrayList<Movie>) savedInstanceState.getSerializable("popular_movie");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popular_frag,container,false);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TAG","Inside onActivityCreated method");
        //Setting up Toolbar title
        Toolbar tbar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
        toolbar_title = (TextView) tbar.findViewById(R.id.toolbar_txt);
        toolbar_title.setText(TITLE);

        //Below condition makes sure no API call is made during orientation changes.
        if(movies.size()>0){
            adapter = new GridAdapter(getActivity(),R.layout.grid_item,movies);
            gridView.setAdapter(adapter);
        }
        else {
                CheckConnectionAndFetch();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Imageview is used as shared Element in transition
                ImageView iv = (ImageView) view.findViewById(R.id.picture);
                android.support.v4.util.Pair<View,String> p1 = android.support.v4.util.Pair.create((View) iv, "POSTER");
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),p1);
                Intent i = new Intent(getActivity(),DetailActivity.class);
                Bundle b = new Bundle();
                Movie movie  = movies.get(position);
                b.putParcelable("MOVIE",movie);//send movie object as bundle to be used in detail activity screen.
                i.putExtras(b);
                getActivity().startActivity(i, optionsCompat.toBundle());
            }
        });

        //Handles retry of fetching data on no-internet connection condition
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_internet.setVisibility(View.INVISIBLE);
                CheckConnectionAndFetch();

            }
        });

        //Handles retry on reception of volley error message either due to timeout/network error/volley error
        retry_volley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_volley.setVisibility(View.INVISIBLE);
                CheckConnectionAndFetch();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void CheckConnectionAndFetch(){
        if(cd.isConnectedToInternet()) {
            if (preferences.getBoolean(SORT_POPULAR,false)) {
                FetchData(SORT_BY_POPULAR);
            } else if (preferences.getBoolean(SORT_RATING,false)) {
                FetchData(SORT_BY_RATING);
            } else {
                FetchData(MOVIES_URL);
            }
        }
        else{
            NoInternetElements();
        }
    }

    //Makes API call using Volley.
    private void FetchData(String URL){
        hideElements();
        movies.clear();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL + getResources().getString(R.string.api_key),null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)  {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showElements();
                adapter = new GridAdapter(getActivity(),R.layout.grid_item,movies);
                gridView.setAdapter(adapter);

            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG","WHAT THE FUCK RA!!");
               if(error instanceof TimeoutError){
                   RetryVolleyElements("Uh-Oh!! Our Server was very slow to respond..Sorry!\nPlease try again!");
               }
               else if(error instanceof NetworkError){
                   RetryVolleyElements("Uh-Oh!! This network issue pisses me off..\nPlease check network settings and try again!");
               }
               else if(error instanceof VolleyError){
                   RetryVolleyElements("Duh!!Not sure what happened there!\n Please try again!");
               }
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq,TAG);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("TAG","OnCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu, inflater);
        menu_item = menu.findItem(R.id.action_filter);
        if(preferences.getBoolean(SORT_POPULAR,false) || preferences.getBoolean(SORT_RATING,false))
        menu_item.setIcon(R.drawable.filter_icon_selected);
        else
        menu_item.setIcon(R.drawable.filter_icon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_filter:
                Intent filter = new Intent(getActivity(),FilterActivity.class);
                startActivityForResult(filter,INTENT_REQ);
                break;
            case R.id.action_search:
                Intent search = new Intent(getActivity(),SearchActivity.class);
                startActivity(search);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == INTENT_REQ){
            if(resultCode == Activity.RESULT_OK){
                getActivity().invalidateOptionsMenu();
                String sort_param = data.getStringExtra("SORT");
                if(sort_param.equals("POPULARITY")){
                    FetchData(SORT_BY_POPULAR);
                    if(menu_item!=null)
                    menu_item.setIcon(R.drawable.filter_icon_selected);
                }
                else if(sort_param.equals("RATING")){

                    FetchData(SORT_BY_RATING);
                    if(menu_item!=null)
                    menu_item.setIcon(R.drawable.filter_icon_selected);
                }
                else if(sort_param.equals("RESET")){
                    FetchData(MOVIES_URL);
                    if(menu_item!=null)
                    menu_item.setIcon(R.drawable.filter_icon);
                }

            }
        }

    }

    //Store ArrayList<Movie> movies to be restored during Orientation Changes.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("popular_movie",movies);
    }


    //Hides Gridview and shows ProgressBar while API Call is going on
    private void hideElements(){
        gridView.setVisibility(View.INVISIBLE);
        pbar.setVisibility(View.VISIBLE);
    }

    //Brings back Gridview and hides ProgressBar once API call is complete.
    private void showElements(){
        gridView.setVisibility(View.VISIBLE);
        pbar.setVisibility(View.INVISIBLE);
    }

    private void NoInternetElements(){
        rl_internet.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        pbar.setVisibility(View.INVISIBLE);
    }

    private void RetryVolleyElements(String error){
        rl_volley.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        pbar.setVisibility(View.INVISIBLE);
        volley_error_msg.setText(error);
    }


}
