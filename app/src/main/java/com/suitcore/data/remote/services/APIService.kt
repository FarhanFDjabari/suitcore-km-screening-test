package com.suitcore.data.remote.services

import com.suitcore.data.model.Place
import com.suitcore.data.model.User
import com.suitcore.data.remote.wrapper.MapBoxResults
import com.suitcore.data.remote.wrapper.Results
import com.suitcore.data.remote.wrapper.Result
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * Created by DODYDMW19 on 8/3/2017.
 */

interface APIService {

    @GET("users")
    fun getMembers(
            @Query("per_page") perPage: Int,
            @Query("page") page: Int): Single<Results<User>>

    @GET("users/{id}")
    fun getMember(
        @Path("id") id: Int
    ) : Deferred<Result<User>>

    @POST("login")
    @FormUrlEncoded()
    fun loginAsync(
        @Field("email") email: String,
        @Field("password") password: String
    ): Deferred<Result<String>>

    @GET("users")
    fun getMembersCoroutinesAsync(
            @Query("per_page") perPage: Int,
            @Query("page") page: Int): Deferred<Results<User>>

    @GET
    fun searchPlaceAsync(@Url url: String?): Deferred<MapBoxResults<Place>>
}
