package com.example.punit.popularmovies.Helpers;

import java.io.Serializable;

public class Cast implements Serializable {
    private String cast_name;
    private String cast_image;

    public String getCast_name() {
        return cast_name;
    }

    public void setCast_name(String cast_name) {
        this.cast_name = cast_name;
    }

    public String getCast_image() {
        return cast_image;
    }

    public void setCast_image(String cast_image) {
        this.cast_image = cast_image;
    }
}
