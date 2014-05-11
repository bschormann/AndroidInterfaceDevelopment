package jbs.uw.homework252.view;

import jbs.uw.homework252.MainActivity;
import jbs.uw.homework252.R;
import jbs.uw.homework252.model.DatabaseConnector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Displays one task's details
 */
public class TaskDetailsFragment extends Fragment {
	
	private static final String TAG = "TaskDetailsFragment";
	
	
 	// Callback methods implemented by MainActivity <br/>   
	public interface TaskDetailsFragmentListener {
		// called when a task is deleted
		public void onTaskDeleted();
		// called to pass Bundle of task's info for editing
		public void onEditTask(Bundle arguments);
	}
	
	// current activated item position. Only used on tablets.
	private int activatedPosition = ListView.INVALID_POSITION;
	// the serialization (saved instance state) Bundle key representing the
	// activated item position. Only used on tablets.	
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private TaskDetailsFragmentListener listener;
   
	private long rowID = -1; // selected task's rowID
	private TextView titleTextView; 		// displays task title 
	private TextView startDateTextView; 	// displays task's start date
	private TextView descriptionTextView; 	// displays task's description
	private TextView endDateTextView; 		// displays task's end date
   
	
	/**
	 * Sets DetailsFragmentListener when fragment attached
	 * 
	 * @param activity	MainActivity
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.v(TAG, "onAttach()");
		
		super.onAttach(activity);
		listener = (TaskDetailsFragmentListener) activity;
	}
   
   /**
    * Removes DetailsFragmentListener when fragment detached
    */
   @Override
   public void onDetach() {
		Log.v(TAG, "onDetach()");
		
		super.onDetach();
		listener = null;
   }

   /**
    * Called when DetailsFragmentListener's view needs to be created
    * 
    * @param inflater
    * @param container
    * @param savedInstanceState
    */
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
		Log.v(TAG, "onCreateView()");

		super.onCreateView(inflater, container, savedInstanceState);  
		setRetainInstance(true); // save fragment across configuration changes

		// if TaskDetailsFragment is being restored, get saved row ID
		if (savedInstanceState != null) {
			rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
		} else {
			// get Bundle of arguments then extract the task's row ID
			Bundle arguments = getArguments();         
			if (arguments != null) {
				rowID = arguments.getLong(MainActivity.ROW_ID);
			}
		}
         
		// inflate TaskDetailsFragment's layout
		View view = inflater.inflate(R.layout.fragment_details, container, false);               
		setHasOptionsMenu(true); // this fragment has menu items to display

		// get the EditTexts
		titleTextView = 		(TextView) view.findViewById(R.id.titleTextView);
		startDateTextView = 	(TextView) view.findViewById(R.id.startDateTextView);
		descriptionTextView = 	(TextView) view.findViewById(R.id.descriptionTextView);
		endDateTextView = 		(TextView) view.findViewById(R.id.endDateTextView);
		return view;
   	}
   
   	/**
   	 * Called when the TaskDetailsFragment resumes.
   	 * Triggers update of the database.
   	 */
   	@Override
   	public void onResume() {
		Log.v(TAG, "onResume()");
		
		super.onResume();
		new LoadTasksTask().execute(rowID); // load task at rowID
   	} 

   	/**
   	 * Saves the selected task's row ID and activated position
   	 * when the configuration of the device changes during execution.
   	 * 
   	 * @param outState
   	 */
   	@Override
    public void onSaveInstanceState(Bundle outState)  {
		Log.v(TAG, "onSaveInstanceState()");
		
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.ROW_ID, rowID);
        outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
    }

   	/**
   	 * Display this fragment's menu items.
   	 * 
   	 * @param menu
   	 * @param inflater
   	 */
   	@Override
   	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 	{
		Log.v(TAG, "onCreateOptionsMenu()");
		
		super.onCreateOptionsMenu(menu, inflater);
      	inflater.inflate(R.menu.fragment_details_menu, menu);
   	}

   	/**
   	 * Handles menu item selections.
   	 * 
   	 * @param menu
   	 */
   	@Override
   	public boolean onOptionsItemSelected(MenuItem item)  {
		Log.v(TAG, "onOptionsItemSelected()");
		
		switch (item.getItemId()) {
		
         	case R.id.action_edit: 
            // create Bundle containing task data to edit
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence("title", 			titleTextView.getText());
            arguments.putCharSequence("startDate", 		startDateTextView.getText());
            arguments.putCharSequence("description", 	descriptionTextView.getText());
            arguments.putCharSequence("endDate", 		endDateTextView.getText());
            listener.onEditTask(arguments); // pass Bundle to listener
            return true;
            
         	case R.id.action_delete:
            deleteTask();
            return true;
		}
		return super.onOptionsItemSelected(item);
   	} 
   
   	/**
   	 * Performs database query in separate thread.
   	 * Activated by onResume().
   	 */
   	private class LoadTasksTask extends AsyncTask<Long, Object, Cursor> {
   		DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

   		// open database & get Cursor representing specified task's data
   		@Override
   		protected Cursor doInBackground(Long... params) {
   			databaseConnector.open();
   			return databaseConnector.getOneTask(params[0]);
   		} 

   		// use the Cursor returned from the doInBackground method
   		@Override
   		protected void onPostExecute(Cursor result) {
   			super.onPostExecute(result);
   			result.moveToFirst(); // move to the first item 
   
   			// get the column index for each data item
   			int titleIndex = 		result.getColumnIndex("title");
   			int startDateIndex = 	result.getColumnIndex("startDate");
   			int descriptionIndex = 	result.getColumnIndex("description");
   			int endDateIndex = 		result.getColumnIndex("endDate");
   
   			// fill TextViews with the retrieved data
   			titleTextView.		setText(result.getString(titleIndex));
   			startDateTextView.	setText(result.getString(startDateIndex));
   			descriptionTextView.setText(result.getString(descriptionIndex));
   			endDateTextView.	setText(result.getString(endDateIndex));
   
   			result.close(); // close the result cursor
   			databaseConnector.close(); // close database connection
   		} 
   	} 

   	/**
   	 * Delete a task and trigger dialog for user to confirm
   	 */
   	private void deleteTask()  {         
		Log.v(TAG, "deleteTask()");
		
	   // use FragmentManager to display the confirmDelete DialogFragment
	   confirmDelete.show(getFragmentManager(), "confirm delete");
   	} 

   	/**
   	 * DialogFragment to confirm deletion of task
   	 */
   	private DialogFragment confirmDelete = new DialogFragment() {
   		// create an AlertDialog and return it
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      
            builder.setTitle(R.string.confirm_title); 
            builder.setMessage(R.string.confirm_message);
      
            // provide an OK button that simply dismisses the dialog
            builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    final DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
      
                    // AsyncTask deletes task and notifies listener
                    AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>()  {
                    	@Override
                        protected Object doInBackground(Long... params)  {
                    		databaseConnector.deleteTask(params[0]); 
                            return null;
                        } 
      
                    	@Override
                        protected void onPostExecute(Object result) {                                 
                            listener.onTaskDeleted();
                        }
                    }; 
                    // execute the AsyncTask to delete task at rowID
                    deleteTask.execute(new Long[] { rowID });               
                } 
            } 
            ); 
            
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create(); 
        }
    }; 
} 