package udacity.com.popularmoviedb;

/**
 * Created by arbalan on 8/14/16.
 */

public interface IConstants {
    // These are the names of the JSON objects that need to be extracted.
    String MOVIE_RESULTS = "results";
    String MOVIE_ID = "id";
    String MOVIE_TITLE = "original_title";
    String MOVIE_OVERVIEW = "overview";
    String MOVIE_POSTER_IMAGE = "poster_path";
    String MOVIE_VOTE_AVERAGE = "vote_average";
    String MOVIE_RELEASE_DATE = "release_date";
    String MOVIE_POPULARITY = "popularity";
    String MOVIE_DB_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
    String MOVIE_PARAMS = "movie_params";
    String YOUTUBE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    String BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    String PAGE_PARAM = "page";
    String API_KEY_PARAM = "api_key";
    String PAGE_QUERY_EXTRA = "page";
}
