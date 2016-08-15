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
    String REVIEW_AUTHOR = "author";

    String SORT_ORDER_ID_KEY = "sort_order_key";
    String MOVIE_ID_KEY = "movieId_key";
    String TRAILER_KEY = "trailer_key";
    String REVIEW_KEY = "review_key";
    String YES = "Y";
    String NO = "N";
    String FAV_ADDED = " added to favorites";
    String FAV_REMOVED = " removed from favorites";

    String MOVIE_DB_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
    String MOVIE_PARAMS = "movie_params";
    String YOUTUBE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    String YOUTUBE_INTENT = "vnd.youtube:";
    String BASE_URL = "http://api.themoviedb.org/3/movie/";
    String RESOURCE_TRAILER = "/videos";
    String RESOURCE_REVIEW = "/reviews";
    String PAGE_PARAM = "page";
    String API_KEY_PARAM = "api_key";
    String PAGE_QUERY_EXTRA = "page";

    String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    String BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
}
