package io.supercharge.tmdb.model;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.supercharge.tmdb.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by aquajava on 2018. 04. 09..
 */

public class MovieQuery {

    public static class Builder {

        MovieQuery movieQuery;

        public Builder(Context context) {
            movieQuery = new MovieQuery(context);
        }

        public Builder query(String query) {
            movieQuery.setQuery(query);

            return this;
        }

        public MovieQuery build() {
            return movieQuery;
        }
    }

    interface MovieQueryService {
        @GET("search/movie?query={query}&page={page}")
        Call<QueryResult> fetchQuery(@Path("query") String query, @Path("page") int page);
    }

    interface MovieDetailsService {
        @GET("movie/{movie_id}")
        Call<MovieDetails> fetchDetails(@Path("movie_id") String movieId);
    }

    private Context context;

    private String query;
    private int page = 1;

    private MovieQuery(Context context) {
        this.context = context;
    }

    void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public List<MovieDetails> send() throws IOException {
        List<MovieDetails> detailResults = new ArrayList<>();

        Retrofit retrofit = RetrofitFactory.createForGson(context);
        final MovieQueryService queryService = retrofit.create(MovieQueryService.class);
        final MovieDetailsService detailsService = retrofit.create(MovieDetailsService.class);

        Response<QueryResult> queryResultResponse = queryService.fetchQuery(query, page).execute();

        if (queryResultResponse.isSuccessful()) {
            for (MovieResult movie : queryResultResponse.body().getResults()) {
                Response<MovieDetails> response = detailsService.fetchDetails(String.valueOf(movie.getId())).execute();

                if (response.isSuccessful()) {
                    detailResults.add(response.body());
                }
            }

            return detailResults;
        }

        return null;
    }

    public void nextPage() {
        page++;
    }

    public void reset() {

    }
}
