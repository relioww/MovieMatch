package com.relioww.moviematch.films;

import android.graphics.Bitmap;

public class FilmItem {
    private int id;
    private String name;
    private Bitmap image;
    private int year;

    public FilmItem(int id, String name, Bitmap image, int year) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
