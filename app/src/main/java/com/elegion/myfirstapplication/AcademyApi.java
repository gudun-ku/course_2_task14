package com.elegion.myfirstapplication;

import com.elegion.myfirstapplication.model.Album;
import com.elegion.myfirstapplication.model.Comment;
import com.elegion.myfirstapplication.model.Song;
import com.elegion.myfirstapplication.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by marat.taychinov
 * Updated by aleks beloushkin
 */

public interface AcademyApi {

    @POST("registration")
    Completable registration(@Body User user);

    @GET("user")
    Single<User> authentication();

    @GET("albums")
    Single<List<Album>> getAlbums();

    @GET("albums/{id}")
    Single<Album> getAlbum(@Path("id") int id);

    @GET("songs")
    Call<List<Song>> getSongs();

    @GET("songs/{id}")
    Call<Song> getSong(@Path("id") int id);

    // comments (all)
    @GET("comments")
    Single<List<Comment>> getComments();

    // comment (by id)
    @GET("comments/{id}")
    Single<Comment> getComment(@Path("id") int commentId);

    // comments to albums
    @GET("albums/{id}/comments")
    Single<List<Comment>> getAlbumComments(@Path("id") int albumId);

    @APIDataEnvelope(hasEnvelope = false)
    @POST("comments")
    // post a comment
    Single<Comment> postComment(@Body Comment comment);

}
