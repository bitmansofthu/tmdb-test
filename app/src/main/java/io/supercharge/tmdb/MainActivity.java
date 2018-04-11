package io.supercharge.tmdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import io.supercharge.tmdb.model.MovieDetails;
import io.supercharge.tmdb.model.MovieQuery;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movie_list)
    RecyclerView movieList;

    @BindView(R.id.search_field)
    EditText searchField;

    private MovieListAdapter listAdapter = new MovieListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnEditorAction(R.id.search_field)
    public boolean onSearchEditorAction(TextView tv, int actionId, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            MovieQuery.Builder bld = new MovieQuery.Builder(this)
                    .query(searchField.getText().toString());
            bld.build().send(new MovieQuery.Callback() {
                @Override
                public void onMovieDetails(MovieDetails details) {
                    listAdapter.addMovieDetails(details);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });

            return true;
        }
        return false;
    }

    class MovieListAdapter extends RecyclerView.Adapter<MovieItemViewHolder> {

        private List<MovieDetails> movieDetails = new ArrayList<>();

        @Override
        public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MovieItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public void addMovieDetails(MovieDetails details) {
            movieDetails.add(details);

            notifyDataSetChanged();
        }

        public void reset() {
            movieDetails.clear();

            notifyDataSetChanged();
        }
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder {
        public MovieItemViewHolder(View view) {
            super(view);
        }
    }
}
