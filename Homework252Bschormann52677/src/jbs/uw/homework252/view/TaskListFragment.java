package jbs.uw.homework252.view;

import jbs.uw.homework252.R;
import jbs.uw.homework252.model.DatabaseConnector;
import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Displays the task list in a ListView and provides
 * a menu item for adding a new task.
 */
public class TaskListFragment extends ListFragment {
	
	private static final String TAG = "TaskListFragment";
	
	//Callback methods implemented by MainActivity    
	public interface TaskListFragmentListener { 
	 	// called when user selects a task 
		public void onTaskSelected(long rowID); 
		// called when user decides to add a task 
		public void onAddTask(); 
	}
	   	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	//The current activated item position. Only used on tablets.
	private int activatedPosition = ListView.INVALID_POSITION;
	
	private TaskListFragmentListener listener; 
   
	private ListView taskListView; // the ListActivity's ListView
	private CursorAdapter taskAdapter; // adapter for ListView
   
	/**
	 * Set TaskListFragmentListener when fragment attached. 
	 * 
	 * @param activity	MainActivity	
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.v(TAG, "onAttach()");

		super.onAttach(activity);
		listener = (TaskListFragmentListener) activity;
   	}

   	/**
   	 * Removes TaskListFragmentListener when Fragment detached
   	 */
   	@Override
   	public void onDetach() {
		Log.v(TAG, "onDetach()");

   		super.onDetach();
   		listener = null;
   	}

   	/**
   	 * 
   	 */
   	@Override
   	public void onViewCreated(View view, Bundle savedInstanceState)  {
		Log.v(TAG, "onViewCreated()");

		super.onViewCreated(view, savedInstanceState);
		setRetainInstance(true); // save fragment across configuration changes
		setHasOptionsMenu(true); // this fragment has menu items to display

		// set text to display when there are no tasks
		setEmptyText(getResources().getString(R.string.no_tasks));

		// get ListView reference and configure ListView
		taskListView = getListView(); 
		taskListView.setOnItemClickListener(viewTaskListener);      
      	// next line should cause item selected to be highlighted - it doesn't ???
		taskListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
      	
      	// restore the previously serialized activated item position.
 		if (savedInstanceState != null
 					&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
 			setActivatedPosition(savedInstanceState
 					.getInt(STATE_ACTIVATED_POSITION));
 		}
      
      	// map each task's title to a TextView in the ListView layout
      	String[] from = new String[] { "title" };
      	int[] to = new int[] { android.R.id.text1 };
      	taskAdapter = new SimpleCursorAdapter(getActivity(), 
      			android.R.layout.simple_list_item_1, null, from, to, 0);
      	setListAdapter(taskAdapter); // set adapter that supplies data
   	}

   	/**
   	 * Responds to the user touching a task's title in the ListView
   	 */
   	OnItemClickListener viewTaskListener = new OnItemClickListener()   {
  		@Override
   		public void onItemClick(AdapterView<?> parent, View view,  int position, long id)  {
   			Log.v(TAG, "viewTaskListener() - onItemClick");

   			listener.onTaskSelected(id); // pass selection to MainActivity
   		} 
   	}; 

   	/**
   	 * Uses a GetTasksTask (AsyncTask) to load complete list of tasks. 
   	 */
   	@Override
   	public void onResume()  {
		Log.v(TAG, "onResume()");
		super.onResume(); 
		new GetTasksTask().execute((Object[]) null);
   	}

   	/**
   	 * Contains interaction with the DatabaseConnector.
   	 */
   	private class GetTasksTask extends AsyncTask<Object, Object, Cursor> {
   		DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

   		// open database and return Cursor for all tasks
   		@Override
   		protected Cursor doInBackground(Object... params) {
   			databaseConnector.open();
   			return databaseConnector.getAllTasks(); 
   		} 

   		// use the Cursor returned from the doInBackground method
   		@Override
   		protected void onPostExecute(Cursor result) {
   			taskAdapter.changeCursor(result); // set the adapter's Cursor
   			databaseConnector.close();
   		} 
   	} 

   	/**
   	 * Closes Cursor and removes from taskAdapter 
   	 */
   	@Override
   	public void onStop() {
		Log.v(TAG, "onStop()");

		Cursor cursor = taskAdapter.getCursor(); // get current Cursor
   		taskAdapter.changeCursor(null); // adapter now has no Cursor  
   		if (cursor != null) 
   			cursor.close(); // release the Cursor's resources
      
   		super.onStop();
   	} 

   	/**
   	 * Display this fragment's menu items
   	 */
   	@Override
   	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.v(TAG, "onCreateOptionsMenu()");

   		super.onCreateOptionsMenu(menu, inflater);
   		inflater.inflate(R.menu.fragment_task_list_menu, menu);
   	}

   	/**
   	 * Handles the choice from the menu.
   	 */
   	@Override
   	public boolean onOptionsItemSelected(MenuItem item)  {
		Log.v(TAG, "onOptionsItemSelected()");

		switch (item.getItemId()) {
			case R.id.action_add:
            listener.onAddTask();
            return true;
		}
      
      return super.onOptionsItemSelected(item); 
   	}
   
   	/**
   	 * Creates and executes the GetTasksTask to update the tasks list.
   	 */
   	public void updateTaskList() {
		Log.v(TAG, "onCreateOptionsMenu()");

		new GetTasksTask().execute((Object[]) null);
   	}
   	
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	*/
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

   	/**
   	 * @param position
   	 */
	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(activatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		activatedPosition = position;
	}
}
 

