package com.example.android.moviesremake;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PepovPC on 7/17/2017.
 * Je to objekt Movie
 */

public class Movie implements Parcelable{

    private String image;
    private String title;
    private String overview;
    private Double vote;
    private String release;

    public Movie() {
        super();
    }

    public Movie(String image, String title, String overview, Double vote, String release) {
        this.image = image;
        this.title = title;
        this.overview = overview;
        this.vote = vote;
        this.release = release;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setVote(Double vote) {
        this.vote = vote;
    }

    public Double getVote() {
        return vote;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRelease() {
        return release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(vote);
        dest.writeString(release);
    }


    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel in) {
        image = in.readString();
        title = in.readString();
        overview = in.readString();
        vote = in.readDouble();
        release = in.readString();
    }


}
