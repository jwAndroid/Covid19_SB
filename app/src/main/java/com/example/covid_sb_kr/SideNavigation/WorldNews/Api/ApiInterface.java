package com.example.covid_sb_kr.SideNavigation.WorldNews.Api;


import com.example.covid_sb_kr.SideNavigation.WorldNews.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<News> getNews(

            @Query("country") String country ,
            @Query("apiKey") String apiKey

    );

    @GET("everything")
    Call<News> getNewsSearch(

            @Query("q") String keyword,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey

    );

    /*

    http://newsapi.org/v2/
    everything @GET
    ?q=tesla&from=2021-02-10 @Query
    &sortBy=publishedAt @Query
    &apiKey=7c2e7db937db4338831305b9baad2997 @Query
    ==> list.size()반환

    */


}
