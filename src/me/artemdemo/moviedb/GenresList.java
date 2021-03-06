package me.artemdemo.moviedb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenresList {

	static String strGenres = "[{\"id\":28,\"name\":\"Action\"},{\"id\":12,\"name\":\"Adventure\"},{\"id\":16,\"name\":\"Animation\"},{\"id\":35,\"name\":\"Comedy\"},{\"id\":80,\"name\":\"Crime\"},{\"id\":105,\"name\":\"Disaster\"},{\"id\":99,\"name\":\"Documentary\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":82,\"name\":\"Eastern\"},{\"id\":2916,\"name\":\"Erotic\"},{\"id\":10751,\"name\":\"Family\"},{\"id\":10750,\"name\":\"Fan Film\"},{\"id\":14,\"name\":\"Fantasy\"},{\"id\":10753,\"name\":\"Film Noir\"},{\"id\":10769,\"name\":\"Foreign\"},{\"id\":36,\"name\":\"History\"},{\"id\":10595,\"name\":\"Holiday\"},{\"id\":27,\"name\":\"Horror\"},{\"id\":10756,\"name\":\"Indie\"},{\"id\":10402,\"name\":\"Music\"},{\"id\":22,\"name\":\"Musical\"},{\"id\":9648,\"name\":\"Mystery\"},{\"id\":10754,\"name\":\"Neo-noir\"},{\"id\":1115,\"name\":\"Road Movie\"},{\"id\":10749,\"name\":\"Romance\"},{\"id\":878,\"name\":\"Science Fiction\"},{\"id\":10755,\"name\":\"Short\"},{\"id\":9805,\"name\":\"Sport\"},{\"id\":10758,\"name\":\"Sporting Event\"},{\"id\":10757,\"name\":\"Sports Film\"},{\"id\":10748,\"name\":\"Suspense\"},{\"id\":10770,\"name\":\"TV Movie\"},{\"id\":53,\"name\":\"Thriller\"},{\"id\":10752,\"name\":\"War\"},{\"id\":37,\"name\":\"Western\"}]";
	
	/**
	 * Return JSON array of genres
	 * 
	 * @return {JSONArray}
	 */
	public static JSONArray getGenres() {
		JSONArray jsonArrGenres = null;
		
		try {
			jsonArrGenres = new JSONArray( strGenres );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonArrGenres;
	};
	
	/**
	 * Return genre name by its index (position in the array)
	 * 
	 * @param {int} index
	 * @return {String}
	 */
	public static String getGenreNameByIndex( int index ) {
		String strName = "";
		JSONArray jsonArrGenres = getGenres();
		JSONObject objGenre;
		try {
			objGenre = jsonArrGenres.getJSONObject( index );
			strName = objGenre.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strName;
	};
	
}
