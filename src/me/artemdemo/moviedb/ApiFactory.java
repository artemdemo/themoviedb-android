package me.artemdemo.moviedb;


public class ApiFactory {

	static String strApiURL = "http://api.themoviedb.org/3/";
	static String strApiKey = "470fd2ec8853e25d2f8d86f685d2270e";
	
	public enum SearchType {
		BY_YEAR, BY_TITLE, BY_YEAR_AND_TITLE
	};
	
	/*
	 * getMoviesByYearUrl
	 * Fetch movies by year
	 */
	public static String getMoviesByYearUrl( int intYear, int intPage ) {
		//http://api.themoviedb.org/3/discover/movie?year=2010&with_genres=&api_key=470fd2ec8853e25d2f8d86f685d2270e&page=1
		String strUrl = strApiURL + "discover/movie?year=" + Integer.toString(intYear) + "&page=" + Integer.toString(intPage) + "&api_key=" + strApiKey;
		return strUrl;
	};
	
	public static String getMoviesByTitleUrl( String strTitle, int intPage ) {
		String strUrl = strApiURL + "search/movie?query=" + strTitle + "&page=" + Integer.toString(intPage) + "&api_key=" + strApiKey;
		return strUrl;
	};
	
	/*
	 * getMoviesByYearAndTitleUrl
	 * Fetch movies by year and title
	 */
	public static String getMoviesByYearAndTitleUrl( int intYear, String strTitle, int intPage ) {
		String strUrl = strApiURL + "search/movie?year=" + Integer.toString(intYear) + "&query=" + strTitle + "&page=" + Integer.toString(intPage) + "&api_key=" + strApiKey;
		return strUrl;
	};
	
	/*
	 * getMovieById
	 * Fetch single movie by its ID
	 */
	public static String getMovieById( String id ) {
		//http://api.themoviedb.org/3/movie/534?api_key=470fd2ec8853e25d2f8d86f685d2270e
		String strUrl = strApiURL + "movie/" + id + "?api_key=" + strApiKey;
		return strUrl;
	};
	
	
}
