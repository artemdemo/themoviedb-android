package me.artemdemo.moviedb;

import java.util.ArrayList;

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
		String[] items = new String[] {"one", "two", "three", "one", "two", "three", "one", "two", "three", "one", "two", "three"};
		boolean[] checkedItems = {false, false, false, false, false, false, false, false, false, false, false, false };
		
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
				/*
				 * Exiting application.
				 * Android's design does not favor exiting an application by choice, but rather manages it by the OS. 
				 * I can bring up the Home application by its corresponding Intent
				 */
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
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
