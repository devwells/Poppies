/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.devinwells.poppies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {
    //"w185", "w342", "w500", "w780", o
    public static final String SMALL_IMAGE_SIZE = "w185";
    public static final String MED_IMAGE_SIZE = "w342";
    public static final String LARGE_IMAGE_SIZE = "w780";

    public static final String POPULAR_URL = "popular";
    public static final String TOP_RATED_URL = "top_rated";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String BASE_API_URL = "http://api.themoviedb.org/3/movie/";

    public static final String API_KEY = "YOUR API KEY";

    //http://api.themoviedb.org/3/movie/popular?api_key
    //http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

    public enum ImageSize {SMALL, MEDIUM, LARGE};
    public enum SortType {POPULAR, TOP_RATED}

    /**
     *
     * @param imagePath
     * @param size
     * @return
     */
    public static URL buildImageSourceUrl(String imagePath, ImageSize size) {
        String imageSize;
        URL searchUrl = null;

        if(imagePath == null){
            return null;
        }

        switch (size){
            case SMALL:
                imageSize = SMALL_IMAGE_SIZE;
                break;
            case MEDIUM:
                imageSize = MED_IMAGE_SIZE;
                break;
            case LARGE:
            default:
                imageSize = LARGE_IMAGE_SIZE;
                break;
        }

        Uri builtUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                .appendPath(imageSize)
                .appendPath(imagePath.substring(1)) //need to remove the / in front of the path
                .build();

        try{
            searchUrl = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        return searchUrl;
    }

    /**
     *
     * @param sortType
     * @param pageNum
     * @return
     */
    public static URL buildMoviesUrl(SortType sortType, int pageNum) {
        String sortingParam;
        URL searchUrl = null;

        switch (sortType){
            case POPULAR:
                sortingParam = POPULAR_URL;
                break;
            case TOP_RATED:
            default:
                sortingParam = TOP_RATED_URL;
                break;
        }

        if(pageNum < 1){
            pageNum = 1;
        }

        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(sortingParam)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", Integer.toString(pageNum))
                .build();
        
        try{
            searchUrl = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        return searchUrl;
    }
    
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
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
