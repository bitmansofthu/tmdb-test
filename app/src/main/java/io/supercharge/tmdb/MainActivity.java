package io.supercharge.tmdb;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import io.supercharge.tmdb.model.MovieDetails;
import io.supercharge.tmdb.model.MovieQuery;
import io.supercharge.tmdb.util.PicassoHelper;
import io.supercharge.tmdb.util.RetrofitFactory;
import io.supercharge.tmdb.util.Utils;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_SEARCH_LEN = 3;

    @BindView(R.id.movie_list)
    RecyclerView movieList;

    @BindView(R.id.search_field)
    EditText searchField;

    @BindView(R.id.statusText)
    TextView statusText;

    private MovieListAdapter listAdapter = new MovieListAdapter();

    private MovieQuery query;
    private boolean downloading = false;
    private boolean downloadError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieList.setLayoutManager(mLayoutManager);
        movieList.setItemAnimator(new DefaultItemAnimator());
        movieList.setAdapter(listAdapter);
        movieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (!downloading) {
                        nextPage();
                    }
                }
            }
        });
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
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (searchField.getText().length() > MIN_SEARCH_LEN) {
                listAdapter.reset();
                hideKeyboard();

                MovieQuery.Builder bld = new MovieQuery.Builder(this)
                        .query(searchField.getText().toString());
                this.query = bld.build();
                fetchQuery();
            }

            return true;
        }
        return false;
    }

    private void fetchQuery() {
        if (downloading)
            return;

        showStatus(getString(R.string.status_loading), R.color.status_green);
        downloadError = false;
        downloading = true;

        this.query.send(new MovieQuery.Callback() {
            @Override
            public void onMovieDetails(MovieDetails details) {
                if (!downloadError) {
                    listAdapter.addMovieDetails(details);
                }
            }

            @Override
            public void onError(Throwable t) {
                showStatus(getString(R.string.status_error), R.color.status_red);

                downloading = false;
                downloadError = true;
                listAdapter.reset();
            }

            @Override
            public void onCompleted() {
                downloading = false;

                if (!downloadError) {
                    showStatus(null, 0);

                    listAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void nextPage() {
        if (this.query != null) {
            if (this.query.nextPage()) {
                fetchQuery();
            }
        }
    }

    private void showStatus(String text, int colorRes) {
        if (text != null) {
            statusText.setText(text);
            statusText.setBackgroundColor(getResources().getColor(colorRes));

            statusText.setVisibility(View.VISIBLE);
        } else {
            statusText.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    class MovieListAdapter extends RecyclerView.Adapter<MovieItemViewHolder> {

        private List<MovieDetails> movieDetails = new ArrayList<>();

        @Override
        public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item,
                    parent, false);

            return new MovieItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieItemViewHolder holder, int position) {
            MovieDetails details = movieDetails.get(position);

            holder.title.setText(details.getTitle());
            if (details.getPosterPath() != null) {
                String path = Utils.getFullPosterPath(details.getPosterPath(),
                        (int)getResources().getDimension(R.dimen.movie_image_width));
                PicassoHelper.downloadInto(path, holder.poster);
            }
            if (details.getBudget() != null) {
                holder.budget.setText(Utils.formatCurrency(details.getBudget()));
            }
        }

        @Override
        public int getItemCount() {
            return movieDetails.size();
        }

        public void addMovieDetails(MovieDetails details) {
            movieDetails.add(details);
        }

        public void reset() {
            movieDetails.clear();

            notifyDataSetChanged();
        }
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.poster) ImageView poster;
        @BindView(R.id.budget) TextView budget;

        public MovieItemViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }
}
