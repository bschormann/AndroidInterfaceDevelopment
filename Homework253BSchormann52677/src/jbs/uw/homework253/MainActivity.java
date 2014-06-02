package jbs.uw.homework253;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Satisfies requirements of UW Homework 253
 * To use a layout with buttons for selecting type of notification
 * to use set useTestLayout = true;
 */
public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();   

	/*******************************************/
    private static boolean useTestLayout = false;
	/*******************************************/
    
    private AlertDialog notificationToUseDialog;
    private boolean useExplicitIntentNotification;
    
    private Ringtone ringtone;   
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private Timer timer = new Timer();	
    private TextView textCounter;
	private int timeCounter = 0;    
	private static Button playButton;
	
	private NotificationManager notificationManager;
	private int explicitNotificationId = 111;
	private int numberOfExplicitMessages = 0;
	private int implicitNotificationId = 112;
	private int numberOfImplicitMessages = 0;
   
	/** 
	 * Called when the activity is first created.
	 * If in test mode
	 * 		1. Modifies layout to eliminate "use notification buttons"
	 * 		2. Displays a dialog for user to select type of notification
     * 		to use 	   
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    Log.v(TAG, "onCreate()");
       
       	// some code to shed buttons when not testing
	    if (!useTestLayout) {
       		Button button = (Button)findViewById(R.id.explicitNotification);
       		button.setVisibility(View.GONE);
       		button = (Button)findViewById(R.id.implicitNotification);
       		button.setVisibility(View.GONE); 
       		
			// display dialog to select notification type if not in test
			// strings to show in dialog with radio buttons
			final CharSequence[] items = {"Use explicit notification", "Use implicit notification"};
				            
		    // create and build a dialog to select notification type
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Select the notification type to use:");
		        
		    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		        
		        public void onClick(DialogInterface dialog, int item) {
			        switch(item) {
			            case 0:
			                // use explicit dialog
			            	useExplicitIntentNotification = true;
			            	break;
			            case 1:
			            	// use implicit dialog
			            	useExplicitIntentNotification = false;
			               break;
			        }
				     notificationToUseDialog.dismiss();    
				}
			});
		    notificationToUseDialog = builder.create();
		    notificationToUseDialog.show();
		}       

       	// Mike's approach - used to test preliminary versions - overkill for this 
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.buttonLinearLayout);
		int childCount = ll.getChildCount(); 
       	for (int i = 0; i < childCount; i++) {
       		ll.getChildAt(i).setOnClickListener(this);
       	}       
       	textCounter = (TextView)findViewById(R.id.counter);       	
	}
	
	/**
	 * Initializes the thread which executes every 5 seconds.
	 * Thread will cause beep every 5 seconds in addition to
	 * counting the number of beeps.
	 */
	private void doTimerTask(){
		Log.v(TAG, "doTimerTask()"); 
		timerTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						timeCounter++;
						// update TextView
						textCounter.setText("Timer: " + timeCounter);
						// play beep
						try {
							Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
							ringtone.play();
						} catch (Exception e) {
							e.printStackTrace();
						}
   	                Log.v(TAG, "TimerTask run");
   	            }
				});
			}
		};
		// again
		// public void schedule (TimerTask task, long delay, long period) 
		timer.schedule(timerTask, 0, 5000);  // 
	}
   
	/**
	 * If not in test mode then causes either an explicit or
	 * implicit intent notification.
	 *  
	 * If in test mode then notifications are started using
	 * buttons from the menu.
	 * 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {                
           	case R.id.Start:
           		startTask();
    			if (!useTestLayout) {
    				if (useExplicitIntentNotification) {
    					useExplicitIntentNotification();
    				} else {
    					useImplicitIntentNotification();
    				}
    			}
           		break;   
           	/*****************************************
           	Next two only in test layout
           	******************************************/
           	case R.id.explicitNotification:           	
           		useExplicitIntentNotification();
           		break;
           	case R.id.implicitNotification:
           		useImplicitIntentNotification();
           		break;
           	/******************************************/
		}       
	}
   
	/**
     * Used to control start and stop of timer task.
     * Changes text of button to Start<-->Stop
     */
	private void startTask() {
		playButton = (Button)findViewById(R.id.Start);

		String buttonText = playButton.getText().toString().trim();
		if (buttonText.equals("Start")) {
			playButton.setText("Stop");
			doTimerTask();
		} else {	// must be "Stop"
			playButton.setText("Start");
			stopTask();
		}
	}   
	/**
	 * Responds to "stop" command.
	 */
	private void stopTask(){
		Log.v(TAG, "stopTask()");     	
		if(timerTask != null){
			textCounter.setText("Timer canceled: " + timeCounter);

			Log.v(TAG, "Beep stopped");
			timerTask.cancel();
		}
	} 
	
	/**
	 * Code implements explicit intent
	 */
	private void useExplicitIntentNotification() {
		Log.v(TAG, "useExplicitIntentNotification()");
		// invoke default notification service
		Notification.Builder  mBuilder = new Builder(this);	
 
		mBuilder.setContentTitle("New Message with explicit intent");
		mBuilder.setContentText("New message from UW Homework 253 received");
		mBuilder.setTicker("Explicit: New Message Received!");
		mBuilder.setSmallIcon(R.drawable.ic_launcher);

		// increase notification number every time a new notification arrives 
		mBuilder.setNumber(++numberOfExplicitMessages);
      
	    // create an explicit intent 
		Intent resultIntent = new Intent(this, ExplicitNotification.class);
		resultIntent.putExtra("notificationId", explicitNotificationId);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// add the back stack for the intent
		stackBuilder.addParentStack(ExplicitNotification.class);

		// add the intent that starts the activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT); 
		// start the activity when the user clicks the notification text
		mBuilder.setContentIntent(resultPendingIntent);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// pass the Notification object to the system 
		notificationManager.notify(explicitNotificationId, mBuilder.build());
	}

	private void useImplicitIntentNotification() {
		Log.v(TAG, "useImplicitIntentNotification()");
		// invoke default notification service
		Notification.Builder  mBuilder = new Notification.Builder(this);	

		mBuilder.setContentTitle("New Message with implicit intent");
		mBuilder.setContentText("New message from UW Homework 253 received...");
		mBuilder.setTicker("Implicit: New Message Received!");
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
      
		Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

		String[] events = new String[3];
		events[0] = new String("1) Message for implicit intent");
		events[1] = new String("2) big view Notification");
		events[2] = new String("3) from UW Homework 253");

		// set a title for the inbox style big view
		inboxStyle.setBigContentTitle("More Details:");
		// move events into the big view
		for (int i=0; i < events.length; i++) {
			inboxStyle.addLine(events[i]);
		}
		mBuilder.setStyle(inboxStyle);
        
		// increase notification number every time a new notification arrives
		mBuilder.setNumber(++numberOfImplicitMessages);

		// when the user presses the notification, auto-remove it
		mBuilder.setAutoCancel(true);
      
		// creates an implicit intent 
		Intent resultIntent = new Intent("jbs.uw.homework253.TEL_INTENT", 
				Uri.parse("tel:123456789"));
		resultIntent.putExtra("from", "UW Homework 253");
      
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// add the back stack for the intent
		stackBuilder.addParentStack(ImplicitNotification.class);

		// add the intent that starts the activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(resultPendingIntent);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(implicitNotificationId, mBuilder.build());   
	}  
}