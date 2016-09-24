package udacity.com.popularmoviedb.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import udacity.com.popularmoviedb.models.Movie;

import static udacity.com.popularmoviedb.IConstants.*;

/**
 * Created by arbalan on 8/13/16.
 */

public class MovieDataParser {

    public static List<Movie> getMovieDataFromJson(String resultJson)
            throws JSONException {

        List<Movie> movieList = new ArrayList<>();

        JSONObject forecastJson = new JSONObject(resultJson);
        JSONArray movieArray = forecastJson.getJSONArray(MOVIE_RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject movieJson = movieArray.getJSONObject(i);
            if (movieJson != null) {
                Movie movie = new Movie();

                //Assumption : I assume that all below fields returned by the API are defined as required fields in its design.
                //As I do not have additional details about the API defining what fields are optional and required.
                String id = movieJson.getString(MOVIE_ID);
                String poster_url = movieJson.getString(MOVIE_POSTER_IMAGE);
                String overview = movieJson.getString(MOVIE_OVERVIEW);
                double vote_avg = movieJson.getDouble(MOVIE_VOTE_AVERAGE);
                double popularity = movieJson.getDouble(MOVIE_POPULARITY);
                Log.i("MovieDataParser", "*********** popu : " + popularity);
                String title = movieJson.getString(MOVIE_TITLE);
                String release_date = movieJson.getString(MOVIE_RELEASE_DATE);

                movie.setId(id);
                movie.setMovieOverview(overview);
                movie.setPosterUrl(poster_url);
                movie.setTitle(title);
                movie.setVoteAverage(vote_avg);
                movie.setVoteAverage(popularity);
                movie.setMovieReleaseDate(release_date);
                movieList.add(movie);
            }
        }
        return movieList;
    }
}
