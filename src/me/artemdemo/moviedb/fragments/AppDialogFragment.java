package me.artemdemo.moviedb.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class AppDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder theDialog = new AlertDialog.Builder( getActivity() );
		
		theDialog.setTitle("Alert");
		theDialog.setMessage("Are you sure you want to exit?");
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
