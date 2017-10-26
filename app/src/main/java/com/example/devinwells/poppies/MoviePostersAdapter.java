package com.example.devinwells.poppies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devinwells.poppies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devinwells on 10/16/17.
 */

public class MoviePostersAdapter extends RecyclerView.Adapter<MoviePostersAdapter.MoviePostersViewHolder> {
    private List<JSONObject> movieInfo;
    final private  ListItemClickListener mOnCLickListener;
    int totalVideos = 0;
    int currentVisibleVideos = 0;
    int currentResultPage = 0;
    Context context;

    interface ListItemClickListener {
        void onListItemClick(int adapterPosition);
    }

    MoviePostersAdapter(Context context, ListItemClickListener clickListener){
        mOnCLickListener = clickListener;
        this.context = context;
    }


    @Override
    public MoviePostersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_poster_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviePostersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePostersViewHolder holder, int position) {
        JSONObject currentMovie = null;
        try {
            currentMovie = movieInfo.get(position);
            String posterPath = currentMovie.getString("poster_path");
            String imageUrl;

            if(position%4 == 0){
                imageUrl = NetworkUtils.buildImageSourceUrl(posterPath, NetworkUtils.ImageSize.SMALL).toString();
                holder.mMoviePosterTextView.setText(String.format("%.1f",currentMovie.getDouble("vote_average")));
            } else {
                imageUrl = NetworkUtils.buildImageSourceUrl(posterPath, NetworkUtils.ImageSize.MEDIUM).toString();
            }
            Picasso.with(this.context).load(imageUrl).into(holder.mMoviePosterImageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (movieInfo != null) {
            return movieInfo.size();
        }
        return 0;
    }

    public void addMovieInfo(JSONArray results) {
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        if(movieInfo != null && movieInfo.size() > 0){
            resultList.addAll(movieInfo);
        }

        for (int i=0; i<results.length(); i++) {
            try {
                resultList.add(results.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        movieInfo = resultList;
        notifyDataSetChanged();
    }

    public void resetMovieInfo() {
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        movieInfo = resultList;
        notifyDataSetChanged();
    }

    public JSONObject getMovieInfo(int index){
        return movieInfo.get(index);
    }

    public class MoviePostersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mMoviePosterImageView;
        public final TextView mMoviePosterTextView;

        MoviePostersViewHolder(View view){
            super(view);

            mMoviePosterImageView = (ImageView) view.findViewById(R.id.image_movie_poster);
            mMoviePosterTextView = (TextView) view.findViewById(R.id.tv_movie_poster_list_holder);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCLickListener.onListItemClick(getAdapterPosition());
        }
    }
}
