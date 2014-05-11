package jbs.uw.homework252.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

//Provides easy connection and creation of Tasks database.
public class DatabaseConnector  {
	
	private static final String TAG = "DatabaseConnector";
	
	private static final String DATABASE_NAME = "TaskStash";	// database name
	private static final String DATABASE_TABLE = "Tasks";		// database table
      
	private SQLiteDatabase database; // for interacting with the database
	private DatabaseOpenHelper databaseOpenHelper; // creates the database

	// public constructor for DatabaseConnector
	public DatabaseConnector(Context context)  {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
	}

   	// open the database connection
   	public void open() throws SQLException  {
		Log.v(TAG, "open()");
		
		// create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
   	}

   	// close the database connection
   	public void close()  {
		Log.v(TAG, "close()");
		
   		if (database != null)
   			database.close(); 
   	} 

   	// inserts a new task in the database
   	public long insertTask(String title, String startDate, String description, String endDate)  {
   		Log.v(TAG, "insertTask()");
		
   		ContentValues newTask = new ContentValues();
   		newTask.put("title", title);
   		newTask.put("startDate", startDate);
   		newTask.put("description", description);
   		newTask.put("endDate", endDate);

   		open(); 
   		long rowID = database.insert(DATABASE_TABLE, null, newTask);
   		close(); 
   		return rowID;
   	} 

   	// updates an existing task in the database
   	public void updateTask(long id, String title, String startDate, String description, String endDate)  {
   		Log.v(TAG, "updateTask()");
		
   	    ContentValues editTask = new ContentValues();
   		editTask.put("title", title);
   		editTask.put("startDate", startDate);
   		editTask.put("description", description);
   		editTask.put("endDate", endDate);

   		open(); 
   		database.update(DATABASE_TABLE, editTask, "_id=" + id, null);
   		close(); 
   	} 

   	// return a Cursor with all task titles in the database
   	public Cursor getAllTasks()  {
   		Cursor cursor = database.query(DATABASE_TABLE, new String[] {"_id", "title"}, 
   				null, null, null, null, "title");
   		
   		Log.d(TAG, "getAllTasks()" + DatabaseUtils.dumpCursorToString(cursor));
   		
   		return cursor;
   	} 

    // return a Cursor containing specified task's information 
    public Cursor getOneTask(long id) {
    	Cursor cursor = database.query(DATABASE_TABLE, null, "_id=" + id, null, null, null, null);
   		
    	Log.d(TAG, "getOneTask()" + DatabaseUtils.dumpCursorToString(cursor));
   		
    	return cursor;
    } 

    // delete the task specified by the given String name
    public void deleteTask(long id) {
   		Log.v(TAG, "deleteTask()");
   		
    	open(); 
    	database.delete(DATABASE_TABLE, "_id=" + id, null);
    	close(); 
    } 
   
   	private class DatabaseOpenHelper extends SQLiteOpenHelper {
	   	// constructor
	   	public DatabaseOpenHelper(Context context, String title,
	   			CursorFactory factory, int version)  {
	   		super(context, title, factory, version);
	   	}

      	// creates the tasks table when the database is created
      	@Override
      	public void onCreate(SQLiteDatabase db) {
      		// query to create a new table named tasks
      		String createQuery = "CREATE TABLE Tasks" +
      							 "(_id integer primary key autoincrement," +
      							 "title TEXT, startDate TEXT, description TEXT, " +
      							 "endDate TEXT);";
                  
      		db.execSQL(createQuery); // execute query to create the database
      	} 

      	@Override
      	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       	}
    } 
} 


