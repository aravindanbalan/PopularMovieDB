package udacity.com.popularmoviedb;

/**
 * Created by arbalan on 8/14/16.
 */

public interface IConstants {
    // These are the names of the JSON objects that need to be extracted.
    String RESULTS = "results";
    String ID = "id";
    String MOVIE_TITLE = "original_title";
    String MOVIE_OVERVIEW = "overview";
    String MOVIE_POSTER_IMAGE = "poster_path";
    String MOVIE_VOTE_AVERAGE = "vote_average";
    String MOVIE_RELEASE_DATE = "release_date";
    String MOVIE_POPULARITY = "popularity";

    String FAV_STATUS = "status";

    String TRAILER_NAME = "name";
    String TRAILER_YOUTUBE_KEY = "key";

    String REVIEW_URL = "url";
    String REVIEW_CONTENT = "content";
    String REVIEW_AUTHOR = "content";

    String MOVIE_ID_KEY = "movieId";

    String BASE_DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?";
    String MOVIE_DB_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
    String MOVIE_PARAMS = "movie_params";
    String YOUTUBE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    String YOUTUBE_INTENT = "vnd.youtube:";
    String BASE_URL = "http://api.themoviedb.org/3/movie/";
    String RESOURCE_TRAILER = "/videos";
    String RESOURCE_REVIEW = "/reviews";
    String PAGE_PARAM = "page";
    String SORT_PARAM = "sort_by";
    String API_KEY_PARAM = "api_key";
    String PAGE_QUERY_EXTRA = "page";
}
