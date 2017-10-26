package com.example.devinwells.poppies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.devinwells.poppies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {
    TextView mMovieDetailTextView;
    ImageView mMovieDetailImageView;
    TextView mMovieDetailPlot;
    RatingBar mMovieDetailUserRating;
    TextView mMovieReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieDetailTextView = (TextView) findViewById(R.id.tv_movie_detail_title);
        mMovieDetailImageView = (ImageView) findViewById(R.id.iv_movie_detail_poster);
        mMovieDetailPlot = (TextView) findViewById(R.id.tv_movie_detail_plot);
        mMovieDetailUserRating = (RatingBar) findViewById(R.id.rb_movie_detail_rating);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_detail_rd);

        Intent intentThatStartedActivity = getIntent();
        String title = "", imageUrl = "", plotSummary = "", releaseDate = "";
        double userRating = 0.0;

        if(intentThatStartedActivity.hasExtra("title") && intentThatStartedActivity.getStringExtra("title") != null){
            title = intentThatStartedActivity.getStringExtra("title");
        }
        if(intentThatStartedActivity.hasExtra("plotSynopsis") && intentThatStartedActivity.getStringExtra("plotSynopsis") != null){
            plotSummary = intentThatStartedActivity.getStringExtra("plotSynopsis");
        }
        if(intentThatStartedActivity.hasExtra("imagePath") && intentThatStartedActivity.getStringExtra("imagePath") != null){
            imageUrl = intentThatStartedActivity.getStringExtra("imagePath");
            imageUrl = NetworkUtils.buildImageSourceUrl(imageUrl, NetworkUtils.ImageSize.LARGE).toString();
        }
        if(intentThatStartedActivity.hasExtra("releaseDate") && intentThatStartedActivity.getStringExtra("releaseDate") != null){
            releaseDate = intentThatStartedActivity.getStringExtra("releaseDate");
        }
        if(intentThatStartedActivity.hasExtra("userRating")){
            userRating = intentThatStartedActivity.getDoubleExtra("userRating", 0.0);
        }

        mMovieDetailTextView.setText(title);
        mMovieDetailPlot.setText(plotSummary);
        mMovieDetailUserRating.setRating((float)userRating);
        mMovieReleaseDate.setText(releaseDate);
        Picasso.with(getBaseContext()).load(imageUrl).into(mMovieDetailImageView);
    }
}
