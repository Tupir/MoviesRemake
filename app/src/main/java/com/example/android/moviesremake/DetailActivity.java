package com.example.android.moviesremake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private Movie mForecast;
    @Bind(R.id.overview) TextView textOverview;
    @Bind(R.id.vote) TextView textVote;
    @Bind(R.id.release) TextView textRelease;
    @Bind(R.id.imageView1) ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this); // before setText

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movies")) {
                mForecast = intentThatStartedThisActivity.getParcelableExtra("movies");
                Picasso.with(this).load(mForecast.getImage()).into(imageView);
                textRelease.setText(mForecast.getRelease());
                textVote.setText(mForecast.getVote().toString());
                textOverview.setText(mForecast.getOverview());
            }
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Back button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
