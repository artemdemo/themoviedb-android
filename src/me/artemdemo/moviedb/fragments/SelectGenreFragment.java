package me.artemdemo.moviedb.fragments;

import java.util.ArrayList;

import me.artemdemo.moviedb.GenresList;
import me.artemdemo.moviedb.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/*
 * @example http://www.101apps.co.za/articles/making-a-list-coding-multiple-choice-list-dialogs.html
 */

public class SelectGenreFragment extends DialogFragment {

	// array list to save the index of the selected genres
	private ArrayList<Integer> selectedItemsIndexList;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		selectedItemsIndexList = new ArrayList();
		// Get JSON array of genres from GenresList class
		JSONArray jsonArrGenres = GenresList.getGenres();
		// Genre items
		ArrayList<String> arrStrGenres = new ArrayList<String>();
		// Boolean for checked items
		ArrayList<Boolean> arrBoolGenres = new ArrayList<Boolean>();
		
		if ( jsonArrGenres != null ) {
			for (int i = 0; i < jsonArrGenres.length(); i++) {
				try {
					JSONObject objGenre = jsonArrGenres.getJSONObject(i);
					Boolean selectedGenre = false;
					arrStrGenres.add( objGenre.getString("name") );
					if ( MainActivity.selectedGenres != null ) {
						for (int intGenre : MainActivity.selectedGenres) {
							if ( intGenre == i ) {
								selectedGenre = true;
								selectedItemsIndexList.add(intGenre);
								break;
							}
						}
					}
					arrBoolGenres.add(selectedGenre);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Converting ArrayList of strings to primitive arrays
		String[] items = new String[arrStrGenres.size()];
		items = arrStrGenres.toArray(items);
		
		//Converting ArrayList of booleans to primitive arrays (it can't be done as easy as with strings arraylist)
		final boolean[] checkedItems = new boolean[arrBoolGenres.size()];
	    int index = 0;
	    for (Boolean object : arrBoolGenres) {
	    	checkedItems[index++] = object;
	    }
		
		AlertDialog.Builder theDialog = new AlertDialog.Builder( getActivity() );
		
		theDialog.setTitle("Select genre");
		theDialog.setMultiChoiceItems(items, checkedItems, 
				new DialogInterface.OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if (isChecked) {
							selectedItemsIndexList.add(which);
						} else if (selectedItemsIndexList.contains(which)) {
							selectedItemsIndexList.remove(Integer.valueOf(which));
						}
					}
				});
		
		theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity callingActivity = (MainActivity) getActivity();
		        //callingActivity.setGenres(selectedItemsIndexList);
			}
		});
		
		theDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		return theDialog.create();
	}
 
	
}
