package com.example.android.moviesremake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ForecastAdapterOnClickHandler,     // recycler view click handler
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,    // asynctask
        SharedPreferences.OnSharedPreferenceChangeListener{     // settings change

    private MovieAdapter mAdapter;
    private RecyclerView recycler;
    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;    // special ID for LoaderManager

    // COMPLETED (4) Add a private static boolean flag for preference updates and initialize it to false
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if there is no connection
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "Connection not available", Toast.LENGTH_SHORT).show();
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);  // loader

        // nastavenie recyclerview
        recycler = (RecyclerView) findViewById(R.id.rv_numbers);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        // vyplnit recyclerview datami
        mAdapter = new MovieAdapter(getBaseContext(), this);
        recycler.setAdapter(mAdapter);


        /**
         * Nasledujuce riadky nahradzuju tento riadok:  new FetchMovieTask().execute();
         */
        int loaderId = FORECAST_LOADER_ID;
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);


        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    /**
     * Override metoda z rozhrania, ktore sa nachadza v Adapteri
     * Urcuje, co sa ma stat po stlaceni na konkretny view
     */
    @Override
    public void onClick(Movie weatherForDay) {
        Context context = this;
        Intent intentToStartDetailActivity = new Intent(context, DetailActivity.class);
        intentToStartDetailActivity.putExtra("movies", weatherForDay);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     * It's the replacement of AsyncTask
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            /* This Movie array will hold and help cache our movie data */
            ArrayList<Movie> movies = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             * The same as onPreExecute() v AsyncTask
             */
            @Override
            protected void onStartLoading() {
                if (movies != null) {
                    deliverResult(movies);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             *
             * @return Movie data as an array of Movies.
             *         null if an error occurs
             */

            @Override
            public ArrayList<Movie> loadInBackground() {


                String locationQuery = NetworkUtils
                        .getSearchQuery(MainActivity.this);

                System.out.println(locationQuery);

                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);



                    return MovieJsonParser.getMovieDataFromJson(jsonWeatherResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Movie> data) {
                movies = data;
                super.deliverResult(movies);
            }



        };
    }

    /**
     * Called when a previously created loader has finished its load.
     * The same as onPostExecute in AsyncLoad
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mAdapter.setMoviesData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //need to set adapter for null and call LoaderManager again
            mAdapter.setMoviesData(null);
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * OnStart is called when the Activity is coming into view. This happens when the Activity is
     * first created, but also happens when the Activity is returned to from another Activity. We
     * are going to use the fact that onStart is called when the user returns to this Activity to
     * check if the location setting or the preferred units setting has changed. If it has changed,
     * we are going to perform a new query.
     */
    @Override
    protected void onStart() {
        super.onStart();

        /*
         * If the preferences for location or units have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now. Later in this course, we are going to show you more elegant ways to
         * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
         */
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d("", "onStart: preferences were updated");
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    //Override onDestroy and unregister MainActivity as a SharedPreferenceChangedListener
    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    // it's called always when settings are changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

}
