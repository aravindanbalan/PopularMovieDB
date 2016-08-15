package udacity.com.popularmoviedb.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.models.Review;
import udacity.com.popularmoviedb.models.Trailer;

import static udacity.com.popularmoviedb.IConstants.*;

/**
 * Created by arbalan on 8/13/16.
 */

public class DataParser {

    public static List<Movie> getMovieDataFromJson(String resultJson)
            throws JSONException {

        if(TextUtils.isEmpty(resultJson)){
            return null;
        }

        List<Movie> movieList = new ArrayList<>();

        JSONObject forecastJson = new JSONObject(resultJson);
        JSONArray movieArray = forecastJson.getJSONArray(RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject movieJson = movieArray.getJSONObject(i);
            if (movieJson != null) {
                Movie movie = new Movie();

                //Assumption : I assume that all below fields returned by the API are defined as required fields in its design.
                //As I do not have additional details about the API defining what fields are optional and required.
                String id = movieJson.getString(ID);
                String poster_url = movieJson.getString(MOVIE_POSTER_IMAGE);
                String overview = movieJson.getString(MOVIE_OVERVIEW);
                double vote_avg = movieJson.getDouble(MOVIE_VOTE_AVERAGE);
                double popularity = movieJson.getDouble(MOVIE_POPULARITY);
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

    public static List<Trailer> getTrailerDataFromJson(String resultJson)
            throws JSONException {

        if(TextUtils.isEmpty(resultJson)){
            return null;
        }

        List<Trailer> trailerList = new ArrayList<>();

        JSONObject forecastJson = new JSONObject(resultJson);
        JSONArray trailerArray = forecastJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject trailerJson = trailerArray.getJSONObject(i);
            if (trailerJson != null) {
                Trailer trailer = new Trailer();

                //Assumption : I assume that all below fields returned by the API are defined as required fields in its design.
                //As I do not have additional details about the API defining what fields are optional and required.
                String id = trailerJson.getString(ID);
                String trailerName = trailerJson.getString(TRAILER_NAME);
                String youtube_key = trailerJson.getString(TRAILER_YOUTUBE_KEY);

                trailer.setId(id);
                trailer.setName(trailerName);
                trailer.setYoutubeKey(youtube_key);
                trailerList.add(trailer);
            }
        }
        return trailerList;
    }

    public static List<Review> getReviewDataFromJson(String resultJson)
            throws JSONException {

        if(TextUtils.isEmpty(resultJson)){
            return null;
        }

        List<Review> reviewList = new ArrayList<>();

        JSONObject forecastJson = new JSONObject(resultJson);
        JSONArray trailerArray = forecastJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject reviewJson = trailerArray.getJSONObject(i);
            if (reviewJson != null) {
                Review review = new Review();

                //Assumption : I assume that all below fields returned by the API are defined as required fields in its design.
                //As I do not have additional details about the API defining what fields are optional and required.
                String id = reviewJson.getString(ID);
                String author = reviewJson.getString(REVIEW_AUTHOR);
                String content = reviewJson.getString(REVIEW_CONTENT);
                String url = reviewJson.getString(REVIEW_URL);

                review.setId(id);
                review.setReviewAuthor(author);
                review.setReviewContent(content);
                review.setReviewUrl(url);
                reviewList.add(review);
            }
        }
        return reviewList;
    }
}
