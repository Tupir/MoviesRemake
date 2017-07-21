package com.example.android.moviesremake;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by PepovPC on 7/13/2017.
 * Trieda na vytvorenie internetoveho spojenia
 *
 */

public class NetworkUtils {

    // url
    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String PARAM_QUERY = "api_key";
    final static String PARAM_KEY = "c88f3eabe09958ae472c9cd7e20b38aa";

    /**
     * Builds the URL.
     * Vysklada URL adresu
     */
    public static URL buildUrl(String searchQuery) {

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(searchQuery.toString())
                .appendQueryParameter(PARAM_QUERY, PARAM_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println(url.toString());
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * Vrati response zo stranky (cely JSON)
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    public static String getSearchQuery(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferredUnits = prefs.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);
        String imperial = context.getString(R.string.pref_units_imperial);
        String userPrefersMetric;
        if (metric.equals(preferredUnits)) {
            userPrefersMetric = defaultUnits;
        } else {
            userPrefersMetric = imperial;
        }
        return userPrefersMetric;
    }


}