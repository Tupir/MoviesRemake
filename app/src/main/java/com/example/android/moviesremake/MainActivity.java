package com.example.android.moviesremake;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ForecastAdapterOnClickHandler {

    private MovieAdapter mAdapter;
    private RecyclerView mNumbersList;
    private ProgressBar mLoadingIndicator;


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
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mNumbersList.setLayoutManager(layoutManager);
        mNumbersList.setHasFixedSize(true);

        // vyplnit recyclerview datami
        mAdapter = new MovieAdapter(getBaseContext(), this);
        mNumbersList.setAdapter(mAdapter);

        new FetchMovieTask().execute();
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


    public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        // for loader while waiting/loading for data
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {


//            if (params.length == 0) {
//                return null;
//            }

//            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl("");

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);



                return MovieJsonParser.getMovieDataFromJson(jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAdapter.setMoviesData(movies);

        }
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
            mAdapter.setMoviesData(null);
            new FetchMovieTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
