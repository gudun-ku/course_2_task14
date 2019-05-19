package com.elegion.myfirstapplication.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(entity = Album.class, parentColumns = "id", childColumns = "album_id")})
public class Comment implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int mId;

    @ColumnInfo(name = "album_id")
    @SerializedName("album_id")
    private int albumId;

    @ColumnInfo(name = "text")
    @SerializedName("text")
    private String mText;

    @ColumnInfo(name = "author")
    @SerializedName("author")
    private String mAuthor;

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    private String mTimestamp;

    public Comment(int albumId, String text, String author) {
        this.albumId = albumId;
        this.mText = text;
        this.mAuthor = author;
    }

    public int getId() {
        return mId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getText() {
        return mText;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTimestamp() {
        return mTimestamp;
    }
}
