package com.example.punit.popularmovies.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.punit.popularmovies.Fragments.GenreFragment;
import com.example.punit.popularmovies.Fragments.PopularMoviesFragment;
import com.example.punit.popularmovies.Fragments.UpcomingMoviesFragment;
import com.example.punit.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar tbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private PopularMoviesFragment pfragment;
    private UpcomingMoviesFragment upcomingMoviesFragment;
    private GenreFragment genreFragment;
    private FragmentManager fmanager;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Toolbar setup
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Drawerlayout and ActionBarDrawertoggle setup
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        fmanager = getSupportFragmentManager();

        //Restore fragment's instance during orientation changes
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey("popular")) {
                pfragment = (PopularMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, "popular");
            }
            else if(savedInstanceState.containsKey("upcoming")) {
                upcomingMoviesFragment = (UpcomingMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, "upcoming");
            }
            else if(savedInstanceState.containsKey("genre")) {
                genreFragment = (GenreFragment) getSupportFragmentManager().getFragment(savedInstanceState, "genre");
            }
        }
        else {
            if (fmanager.findFragmentById(R.id.content) == null) {
               showPopularMoviesFragment();

            }
        }
        //Navigation Drawer Item Click Handling
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.upcoming:
                        showUpcomingMoviesFragment();
                        break;
                    case R.id.action:
                        showGenreFragment("28","ACTION");
                        break;
                    case R.id.adventure:
                        showGenreFragment("12","ADVENTURE");
                        break;
                    case R.id.animation:
                        showGenreFragment("16","ANIMATION");
                        break;
                    case R.id.comedy:
                        showGenreFragment("35","COMEDY");
                        break;
                    case R.id.crime:
                        showGenreFragment("80","CRIME");
                        break;
                    case R.id.drama:
                        showGenreFragment("18","DRAMA");
                        break;
                    case R.id.horror:
                        showGenreFragment("27","HORROR");
                        break;
                    case R.id.scifi:
                        showGenreFragment("878","SCI-FI");
                        break;
                    case R.id.family:
                        showGenreFragment("10751","FAMILY");
                        break;
                    case R.id.fantasy:
                        showGenreFragment("14","FANTASY");
                        break;
                }

                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pfragment = null;
        upcomingMoviesFragment = null;
        genreFragment = null;
    }


    //shows hamburger icon when main fragment is shown else shows back button for rest of fragments
    @Override
    protected void onPostCreate( Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(upcomingMoviesFragment!=null || genreFragment!=null){
            toggle.setDrawerIndicatorEnabled(false);
        }
        else {
            toggle.syncState();
        }
    }

    //Adding popular movies fragment to stack
    private void showPopularMoviesFragment(){

        if(pfragment == null){
            pfragment = new PopularMoviesFragment();
         }
        if(!pfragment.isVisible()){
            toggle.setDrawerIndicatorEnabled(true);
            fmanager.beginTransaction().add(R.id.content,pfragment).commit();
         }
    }

    //Adding Upcoming movies fragment to stack
    private void showUpcomingMoviesFragment(){
        if(upcomingMoviesFragment==null){
            upcomingMoviesFragment = new UpcomingMoviesFragment();
        }
        if(!upcomingMoviesFragment.isVisible()){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            fmanager.beginTransaction().replace(R.id.content,upcomingMoviesFragment).addToBackStack(null).commit();
        }
    }

    //Adding Genre Fragment to stack
    private void showGenreFragment(String id,String title){

        if(genreFragment==null){
            genreFragment = new GenreFragment();
        }
        if(!genreFragment.isVisible()){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            Bundle b = new Bundle();
            b.putString("id",id);
            b.putString("title",title);
            genreFragment.setArguments(b);
            fmanager.beginTransaction().replace(R.id.content,genreFragment).addToBackStack(null).commit();
        }
    }

    //Handling Back Pressed events from fragments
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            toggle.setDrawerIndicatorEnabled(true);
            getSupportFragmentManager().popBackStack();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else {
            super.onBackPressed();
        }
    }

    //Inflating Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Save Fragment's instance during orientation changes.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(pfragment!=null && pfragment.isVisible()) {
            fmanager.putFragment(outState, "popular",pfragment);
        }
        else if(upcomingMoviesFragment!=null && upcomingMoviesFragment.isVisible()){
            fmanager.putFragment(outState,"upcoming",upcomingMoviesFragment);
        }
        else if(genreFragment!=null && genreFragment.isVisible()){
            fmanager.putFragment(outState,"genre",genreFragment);
        }

    }
}
