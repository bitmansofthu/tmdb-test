package io.supercharge.tmdb.model;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.supercharge.tmdb.util.RetrofitFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by aquajava on 2018. 04. 09..
 */

public class MovieQuery {

    private static final String API_KEY = "43a7ea280d085bd0376e108680615c7f";

    public interface Callback {

        void onMovieDetails(MovieDetails details);

        void onError(Throwable t);

        void onCompleted();

    }

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
        @GET("search/movie?api_key=" + API_KEY)
        Observable<QueryResult> fetchQuery(@Query("query") String query, @Query("page") int page);

        @GET("movie/{movie_id}?api_key=" + API_KEY)
        Observable<MovieDetails> fetchDetails(@Path("movie_id") String movieId);
    }

    private Context context;

    private String query;
    private int page = 1;
    private int totalPages;

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

    public void send(final Callback callback) {
        Retrofit retrofit = RetrofitFactory.createForGson(context);
        final MovieQueryService service = retrofit.create(MovieQueryService.class);

        service.fetchQuery(query, page)
                .flatMap(new Function<QueryResult, ObservableSource<MovieResult>>() {
                    @Override
                    public ObservableSource<MovieResult> apply(QueryResult queryResult) throws Exception {
                        totalPages = queryResult.getTotalPages();

                        return Observable.fromArray(queryResult.getResults().toArray(new MovieResult[]{}));
                    }
                })
                .flatMap(new Function<MovieResult, ObservableSource<MovieDetails>>() {
                    @Override
                    public ObservableSource<MovieDetails> apply(MovieResult movieResult) throws Exception {
                        return service.fetchDetails(String.valueOf(movieResult.getId()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (callback != null) {
                            callback.onCompleted();
                        }
                    }
                })
                .subscribe(new Consumer<MovieDetails>() {
                    @Override
                    public void accept(MovieDetails movieDetails) throws Exception {
                        if (callback != null) {
                            callback.onMovieDetails(movieDetails);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null) {
                            callback.onError(throwable);
                        }
                    }
                });
    }

    public MovieQuery nextPage() {
        page++;

        return this;
    }
}
