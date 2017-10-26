package com.example.devinwells.poppies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.devinwells.poppies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviePostersAdapter.ListItemClickListener{
    ProgressBar mQueryProgressBar;
    TextView mErrorMessage;
    ImageView mImageView;
    RecyclerView mRecyclerView;
    MoviePostersAdapter mMoviePostersAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private NetworkUtils.SortType currentFilter = NetworkUtils.SortType.TOP_RATED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryProgressBar = (ProgressBar) findViewById(R.id.pb_movie_info);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mImageView = (ImageView) findViewById(R.id.image_movie_poster);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                makeMovieDbQuery(page);
            }
        };

        Context context = getBaseContext();

        mRecyclerView.setLayoutManager(layoutManager);
        mMoviePostersAdapter = new MoviePostersAdapter(context, this);
        mRecyclerView.setAdapter(mMoviePostersAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);

        makeMovieDbQuery(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();

        if(selectedItemId == R.id.filter_most_popular){
            if(currentFilter != NetworkUtils.SortType.POPULAR){
                currentFilter = NetworkUtils.SortType.POPULAR;
                resetPage();
            }
        } else if(selectedItemId == R.id.filter_top_rated){
            if(currentFilter != NetworkUtils.SortType.TOP_RATED){
                currentFilter = NetworkUtils.SortType.TOP_RATED;
                resetPage();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void resetPage(){
        makeMovieDbQuery(1);
        mMoviePostersAdapter.resetMovieInfo();
        scrollListener.resetState();
    }

    private void makeMovieDbQuery(int page) {
        showJsonDataView();
        URL url = NetworkUtils.buildMoviesUrl(currentFilter, page);
        new MovieDbQueryTask().execute(url);
    }

    void showJsonDataView(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorMessage(){
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    void parseResultInformation(String responseString){
        try {
            JSONObject response = new JSONObject(responseString);
            JSONArray movieDbResults = response.getJSONArray("results");
            mMoviePostersAdapter.totalVideos = response.getInt("total_results");
            mMoviePostersAdapter.currentVisibleVideos += movieDbResults.length();
            mMoviePostersAdapter.currentResultPage = response.getInt("page");


            if(movieDbResults.length() > 0){;
                mMoviePostersAdapter.addMovieInfo(movieDbResults);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(int adapterPosition) {
        Context context = this;
        JSONObject clickedMovie = mMoviePostersAdapter.getMovieInfo(adapterPosition);
        Class nextActivity = MovieDetail.class;
        Intent intent = new Intent(this, nextActivity);
        String clickedTitle = null;
        String imagePath = null;
        String plotSynopsis = null;
        Double userRating = null;
        String releaseDate = null;

        try{
            clickedTitle = clickedMovie.getString("title");
            imagePath = clickedMovie.getString("poster_path");
            plotSynopsis = clickedMovie.getString("overview");
            userRating = clickedMovie.getDouble("vote_average");
            releaseDate = clickedMovie.getString("release_date");
        } catch (JSONException e){
            e.printStackTrace();
        }

        intent.putExtra("title", clickedTitle);
        intent.putExtra("imagePath", imagePath);
        intent.putExtra("plotSynopsis", plotSynopsis);
        intent.putExtra("userRating", userRating);
        intent.putExtra("releaseDate", releaseDate);

        startActivity(intent);
    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            mQueryProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbSearchResults = null;
            try {
                movieDbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e){
                e.printStackTrace();
            }
            return movieDbSearchResults;
        }

        @Override
        protected void onPostExecute(String movieDbQueryResults) {
            mQueryProgressBar.setVisibility(View.INVISIBLE);
            if (movieDbQueryResults != null && !movieDbQueryResults.equals("")) {
                parseResultInformation(movieDbQueryResults);
                showJsonDataView();
            } else {
                showErrorMessage();
            }
        }
    }
}
