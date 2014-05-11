package jbs.uw.homework252;

import jbs.uw.homework252.view.AddEditTaskFragment;
import jbs.uw.homework252.view.TaskDetailsFragment;
import jbs.uw.homework252.view.TaskListFragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

/*
Callback methods implemented by MainActivity  

	public interface AddEditTaskFragmentListener {
		// called after edit completed so task can be redisplayed
		public void onAddEditCompleted(long rowID);
	}
	
	public interface TaskDetailsFragmentListener {
		// called when a task is deleted
		public void onTaskDeleted();
		// called to pass Bundle of task's info for editing
		public void onEditTask(Bundle arguments);
		
	public interface TaskListFragmentListener { 
	 	// called when user selects a task 
		public void onTaskSelected(long rowID); 
		// called when user decides to add a task 
		public void onAddTask(); 
	}
*/


/**
 * Manages the fragments and coordinates the actions between
 * them. On phones one fragment is displayed at a time starting
 * with the <i>TaskListFragment</i>. On tablets the <i>TaskListFragment</i> is
 * on the left side of the layout and, depending on the context,
 * displays either the <i>TaskDetailsFragment</i> or the <i>AddEditTaskFragment</i> in
 * the right two-thirds of the layout.</p>
 * 
 * To communicate data between fragments, each fragment defines an <i>interface</i> 
 * which specifies a callback method implemented in the <i>MainActivity</i>. The callback
 * is triggered by a listener in each fragment.
 * 
 * @author BSchormann
 * @version 1.0
 *
 */
public class MainActivity extends Activity 
   	implements TaskListFragment.TaskListFragmentListener,
      		   TaskDetailsFragment.TaskDetailsFragmentListener, 
      		   AddEditTaskFragment.AddEditFragmentListener {
	
	private static final String TAG = "Main Activity";
	
	// keys for storing row ID in Bundle passed to a fragment
	public static final String ROW_ID = "row_id"; 
   
	TaskListFragment taskListFragment; // displays task list
   
	/**
	 * Displays <i>TaskListFragment</i> when <i>MainActivity's</i> first loads.
	 * <i>TaskListFragment</i> is used to tell the Main Activity
	 * when the user selects a list in the task list or adds
	 * a new task.</p>
	 * 
	 * If the activity is being restored after being shut down or 
	 * recreated from a configuration change, savedInstanceState
	 * will not be null because the <i>TaskListFragment</i> already
	 * exists on the phone and would have been retained. If we are 
	 * on a tablet then it was inflated as part of the <i>MainActivity's</i>. 
	 * 
	 * @param savedInstanceState	State of the application in a bundle.
	 * 								If no data was supplied, savedInstanceState 
	 * 								is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// return if Activity is being restored, no need to recreate GUI
		if (savedInstanceState != null) 
			return;

		// check whether layout contains fragmentContainer 
		if (findViewById(R.id.fragmentContainer) != null)  {
			// phone
			taskListFragment = new TaskListFragment();        
	        // add the fragment to the FrameLayout
	        FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.add(R.id.fragmentContainer, taskListFragment);
	        transaction.commit(); 
		} else {
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((TaskListFragment) getFragmentManager().findFragmentById(
					R.id.taskListFragment)).setActivateOnItemClick(true);
		}
	}
   
	/**
	 * Called when MainActivity resumes. 
	 * If taskListFragment is null (activity running on tablet);
	 * we get a reference to the taskListFragment which is in the 
	 * left side of the window. 
	 */
	@Override
	protected void onResume() {
		Log.v(TAG, "onResume()");

		super.onResume();     	
		if (taskListFragment == null) {
			Log.v(TAG, "onResume() - taskListFragment is null");
			
			taskListFragment = (TaskListFragment) getFragmentManager().findFragmentById(
					R.id.taskListFragment);      
		}
	}
   
	/**
	 * Displays the detailsFragment for selected task.<br/>
	 * If running on a phone then the taskListFragment in the 
	 * fragmentContainer is replaced with the detailsFragment that shows the 
	 * task's information.<br>
	 * If running on a tablet then the top fragment on the back stack 
	 * is removed and replaced with the details fragment in the right pane.
	 * 
	 *  @param rowID	Id of the row being selected.
	 */
	@Override
	public void onTaskSelected(long rowID) {
		Log.v(TAG, "onTaskSelected()");
 
		if (findViewById(R.id.fragmentContainer) != null) {
			// phone
			displayTask(rowID, R.id.fragmentContainer);
		} else {
			// tablet     
			getFragmentManager().popBackStack(); 		
			displayTask(rowID, R.id.rightPaneContainer);
		}
   	}

	/**
	 * Displays the taskDetailsFragment by bundling up the row of the
	 * task to be displayed and then using the FragmentManager to 
	 * display the details.
	 * 
	 * @param rowID		Id of the row being selected.
	 * @param viewID	Id of the container being displayed.
	 */
   	private void displayTask(long rowID, int viewID) {
		Log.v(TAG, "displayTask()");
		 
		TaskDetailsFragment detailsFragment = new TaskDetailsFragment();
      
		Bundle arguments = new Bundle();
		arguments.putLong(ROW_ID, rowID);
		detailsFragment.setArguments(arguments);
      
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(viewID, detailsFragment);
		transaction.addToBackStack(null);
		transaction.commit(); 
   	}
   
   	/**
   	 * Invoked by the listener to notify the MainActivity to display the 
   	 * AddEditFragment. If on phone the fragment is displayed in the 
   	 * left hand window otherwise it is displayed in the right hand window.
   	 */
   	@Override
   	public void onAddTask() {
		Log.v(TAG, "onAddTask()");
		 
   		if (findViewById(R.id.fragmentContainer) != null) {
   			displayAddEditFragment(R.id.fragmentContainer, null); 
   		} else {
   			displayAddEditFragment(R.id.rightPaneContainer, null);
   		}
   	}
   
  	/**
  	 * Displays fragment for adding a new task or editing an existing task.
  	 * 
  	 * @param viewID		Specifies where to attach the AddEditFragment
  	 * @param arguments		If null a new task is added, otherwise contains
  	 * 						the data to be displayed and edited.
  	 */
   	private void displayAddEditFragment(int viewID, Bundle arguments) {
		Log.v(TAG, "displayAddEditFragment()");
		
		AddEditTaskFragment addEditFragment = new AddEditTaskFragment();
      
		if (arguments != null) 
			// edit existing task
			addEditFragment.setArguments(arguments);
      
		// use a FragmentTransaction to display the AddEditTaskFragment
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(viewID, addEditFragment);
		transaction.addToBackStack(null);
		transaction.commit(); 
   	}
   
   	/**
   	 * Receives notification that a task has been deleted. If 
   	 * running on a tablet then the task list is updated 
   	 */
   	
   	@Override
   	public void onTaskDeleted() {
		Log.v(TAG, "onTaskDeleted()");
		 
   		getFragmentManager().popBackStack(); 
      
   		if (findViewById(R.id.fragmentContainer) == null) 
   			// tablet
   			taskListFragment.updateTaskList();
   	}

   	/**
   	 * Display the AddEditTaskFragment to edit an existing task
   	 * 
   	 * @param Bundle	Contains the tasks data.
   	 */
   	@Override
   	public void onEditTask(Bundle arguments) {
		Log.v(TAG, "onEditTask()");
		 
		if (findViewById(R.id.fragmentContainer) != null) {
    	   // phone
    	   displayAddEditFragment(R.id.fragmentContainer, arguments); 
		} else {
    	   // tablet
    	   displayAddEditFragment(R.id.rightPaneContainer, arguments);
		}
   	}

   /**
    * Update GUI after new task or updated task saved
    * 
    * @param rowID	The id of the row selected.		
    */
   	@Override
   	public void onAddEditCompleted(long rowID)   {
		Log.v(TAG, "onAddEditCompleted()");
		 
   		getFragmentManager().popBackStack(); 

   		if (findViewById(R.id.fragmentContainer) == null) {
   			// tablet
   			getFragmentManager().popBackStack(); 
   			taskListFragment.updateTaskList();	
   			// on tablet, display task that was just added or edited
   			displayTask(rowID, R.id.rightPaneContainer); 
   		}
   	}   
}

