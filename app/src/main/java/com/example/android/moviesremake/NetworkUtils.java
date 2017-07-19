package com.example.android.moviesremake;

import android.net.Uri;

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
    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/popular";
    final static String PARAM_QUERY = "api_key";
    final static String PARAM_KEY = "c88f3eabe09958ae472c9cd7e20b38aa";

    /**
     * Builds the URL.
     * Vysklada URL adresu
     */
    public static URL buildUrl(String githubSearchQuery) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
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
}