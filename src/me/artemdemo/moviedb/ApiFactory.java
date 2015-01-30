package me.artemdemo.moviedb;


public class ApiFactory {

	static String strApiURL = "http://api.themoviedb.org/3/";
	static String strApiKey = "470fd2ec8853e25d2f8d86f685d2270e";
	
	/*
	 * getMoviesByYear
	 * Fetch movies by year
	 */
	public static String getMoviesByYearUrl( int intYear ) {
		//http://api.themoviedb.org/3/discover/movie?year=2010&with_genres=&api_key=470fd2ec8853e25d2f8d86f685d2270e&page=1
		String strUrl = strApiURL + "discover/movie?year=" + Integer.toString(intYear) + "&with_genres=&api_key=" + strApiKey;
		
		return strUrl;
	};
	
	
}
