package com.hansjin.mukja_android.Utils.Connections;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.Result;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.Model.itemScores;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public interface CSConnection {
    @GET("/foods")
    Observable<List<Food>> getAllFood();

    @GET("/foods/{id}/{user}")
    Observable<Food> getOneFood(@Path("id") String id, @Path("user") String user);

    @POST("/users")
    Observable<User> createUser(@Body User user);

    @GET("/pio/create_items")
    Observable<Result> createAllItems();

    @GET("/pio/buy/{user}/{food}")
    Observable<Result> buyItem(@Path("user") String user, @Path("food") String food);

    @GET("/pio/similar/{food}")
    Observable<List<itemScores>> similarResult(@Path("food") String food);

    @GET("/pio/recommendation/{user}")
    Observable<List<itemScores>> recommendationResult(@Path("user") String user);
}

