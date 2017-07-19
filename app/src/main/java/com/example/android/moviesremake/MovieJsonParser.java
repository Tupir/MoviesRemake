package com.example.android.moviesremake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PepovPC on 7/17/2017.
 * Trieda spracuje JSON a vysklada Movie objekt
 * Vrati list objektov Movie
 */

public class MovieJsonParser {

    public static ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {

        ArrayList<Movie> movies = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULT = "results";
        final String OWM_POSTER = "poster_path";
        final String OWM_TITLE = "original_title";
        final String OWM_OVERVIEW = "overview";
        final String OWM_VOTE = "vote_average";
        final String OWM_RELEASE = "release_date";


        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // JSONArray ked mas v JSONe znam "["
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_RESULT);
        movies.clear();
        Movie movie;

        for(int i = 0; i < weatherArray.length(); i++) {
            // JSONObject ked mas v JSONe znam "{"
            JSONObject movieForecast = weatherArray.getJSONObject(i);
            String pict = movieForecast.getString(OWM_POSTER);
            String title = movieForecast.getString(OWM_TITLE);
            String overview = movieForecast.getString(OWM_OVERVIEW);
            Double vote = movieForecast.getDouble(OWM_VOTE);
            String release = movieForecast.getString(OWM_RELEASE);

            String complete_url = "http://image.tmdb.org/t/p/w185/" + pict;
            movie = new Movie();
            movie.setImage(complete_url);
            movie.setTitle(title);
            movie.setOverview(overview);
            movie.setVote(vote);
            movie.setRelease(release);
            //Log.v("complete url", movie.getImage());
            movies.add(movie);
        }
        return movies;
    }



}
