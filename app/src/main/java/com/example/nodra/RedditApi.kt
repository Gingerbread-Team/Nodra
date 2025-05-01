package com.example.nodra
import retrofit2.http.GET
import retrofit2.http.Url

interface RedditApi {
    @GET
    suspend fun getPosts(@Url url: String): RedditResponse
}