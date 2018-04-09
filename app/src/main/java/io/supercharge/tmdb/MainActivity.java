package io.supercharge.tmdb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnEditorAction(R.id.search_field)
    public boolean onSearchEditorAction(TextView tv, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            MovieQuery.Builder query = new MovieQuery.Builder(this);
            query.query(searchField.getText().toString());

            new DownloadTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, query.build());

            return true;
        }
        return false;
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

    class DownloadTask extends AsyncTask<MovieQuery, Throwable, List<MovieDetails>> {
        @Override
        protected List<MovieDetails> doInBackground(MovieQuery... queries) {
            try {
                return queries[0].send();
            } catch (Exception e) {
                publishProgress(e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Throwable... throwables) {
            // TODO report error
        }

        @Override
        protected void onPostExecute(List<MovieDetails> movieDetails) {
            if (movieDetails != null) {

            }
        }
    }

    class MovieListAdapter extends RecyclerView.Adapter<MovieItemViewHolder> {

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
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder {
        public MovieItemViewHolder(View view) {
            super(view);
        }
    }
}
