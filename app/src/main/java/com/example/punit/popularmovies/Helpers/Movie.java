package com.example.punit.popularmovies.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


//make it implement parcelable so as to able to pass movie object as bundle between activities
// AND
// implement serializable to able to save movie object as bundle during orientation changes.
public class Movie implements Parcelable,Serializable{

    private String id;
    private String title;
    private String poster;
    private String backdrop;
    private String release_date;
    private String plot;
    private String votes;
    private Double rating;

    public Movie(){}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }


    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
           parcel.writeString(getId());
           parcel.writeString(getPoster());
           parcel.writeString(getBackdrop());
           parcel.writeString(getTitle());
           parcel.writeString(getPlot());
           parcel.writeString(getRelease_date());
           parcel.writeString(getVotes());
           parcel.writeDouble(getRating());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Movie(Parcel in) {
        id = in.readString();
        poster = in.readString();
        backdrop = in.readString();
        title = in.readString();
        plot = in.readString();
        release_date = in.readString();
        votes = in.readString();
        rating = in.readDouble();

    }
}
