package jbs.uw.homework252.view;

import jbs.uw.homework252.MainActivity;
import jbs.uw.homework252.R;
import jbs.uw.homework252.model.DatabaseConnector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Allows user to add a new task or edit an existing one.
 */
public class AddEditTaskFragment extends Fragment {
	
	private static final String TAG = "AddEditTaskFragment";

	// callback method implemented by MainActivity  
	public interface AddEditFragmentListener {
		// called after edit completed so task can be redisplayed
		public void onAddEditCompleted(long rowID);
	}
   
	private AddEditFragmentListener listener; 
   
	private long rowID; // the task being edited
	private Bundle taskInfoBundle; // arguments for editing a task - null if new task
	
	// EditTexts for task information
	private EditText titleEditText;
	private EditText startDateEditText;
	private EditText descriptionEditText;
	private EditText endDateEditText;

	/**
	 * Set AddEditFragmentListener when Fragment attached.
	 * 
	 * @param activity
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.v(TAG, "onAttach()");
		
		super.onAttach(activity);
		listener = (AddEditFragmentListener) activity; 
	}

	// remove AddEditFragmentListener when Fragment detached
	@Override
	public void onDetach() {
		Log.v(TAG, "onAttach()");
		
		super.onDetach();
		listener = null; 
	}
   
	/**
	 * Called when Fragment's view needs to be created
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView()");
		
		super.onCreateView(inflater, container, savedInstanceState);    
		setRetainInstance(true); // save fragment across configuration changes
		setHasOptionsMenu(true); // fragment has menu items to display
      
		// inflate GUI and get references to EditTexts
		View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
		titleEditText = (EditText) view.findViewById(R.id.titleEditText);
		startDateEditText = (EditText) view.findViewById(R.id.startDateEditText);
		descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
		endDateEditText = (EditText) view.findViewById(R.id.endDateEditText);

		taskInfoBundle = getArguments(); // null if creating new task

		if (taskInfoBundle != null) {
			rowID = taskInfoBundle.getLong(MainActivity.ROW_ID);
			titleEditText.		setText(taskInfoBundle.getString("title"));  
			startDateEditText.	setText(taskInfoBundle.getString("startDate"));  
			descriptionEditText.setText(taskInfoBundle.getString("description"));  
			endDateEditText.	setText(taskInfoBundle.getString("endDate"));  
		} 
      
		// set Save Task Button's event listener 
		Button saveTaskButton = (Button) view.findViewById(R.id.saveTaskButton);
		saveTaskButton.setOnClickListener(saveTaskButtonClicked);
		return view;
	}

	/*
	 * Responds to event generated when user saves a task
	 */
	OnClickListener saveTaskButtonClicked = new OnClickListener()  {
		@Override
		public void onClick(View v)  {
			Log.v(TAG, "onClick() in saveTaskButtonClicked");
			
			// test to see if trying to save task without entering a title
			if (titleEditText.getText().toString().trim().length() != 0) {
				// AsyncTask to save task, then notify listener 
	            AsyncTask<Object, Object, Object> saveInfoTask = 	
	            		new AsyncTask<Object, Object, Object>()  {
	            	@Override
	                protected Object doInBackground(Object... params)  {
	                    saveTask(); // save task to the database
	                    return null;
	                } 
	      
	                @Override
	                protected void onPostExecute(Object result)  {
	                	// hide soft keyboard
	                    InputMethodManager imm = (InputMethodManager) 
	                    		getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	                    listener.onAddEditCompleted(rowID);	
	                } 
	            }; 
	               
	            // save the task to the database using a separate thread
	            saveInfoTask.execute((Object[]) null); 
			} else {
				// required task title is blank, so display error dialog
				DialogFragment errorSaving = new DialogFragment() {
					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage(R.string.error_message);
						builder.setPositiveButton(R.string.ok, null);                     
						return builder.create();
					}               
				};
				errorSaving.show(getFragmentManager(), "error saving task");
			} 
		} 
	}; 

	/**
	 * Saves task information to the database
	 */
	private void saveTask() {
		// get DatabaseConnector to interact with the SQLite database
		DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

		if (taskInfoBundle == null) {
			// insert the task information into the database
			rowID = databaseConnector.insertTask(
					titleEditText.getText().toString(),
					startDateEditText.getText().toString(), 
					descriptionEditText.getText().toString(), 
					endDateEditText.getText().toString());
		} else {
			databaseConnector.updateTask(rowID,
					titleEditText.getText().toString(),
					startDateEditText.getText().toString(), 
					descriptionEditText.getText().toString(), 
					endDateEditText.getText().toString());
		}
	} 
} 

