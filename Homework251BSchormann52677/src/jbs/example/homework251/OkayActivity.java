package jbs.example.homework251;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Brett Schormann
 * @version 1.0
 * Display message passed from MainActivity and displays Exit button.
 * Disables the Back button
 *
 */
public class OkayActivity extends Activity {
	
	private TextView textMessage; 
	private ImageView buttonExit;
	
	/**
	 * 
	 * Called when the activity is started from the MainActivity.
	 * Hides the ActionBar.
	 * Displays the message passed from the MainActivity.
	 * When the Exit button is pressed, the process is killed which
	 * returns control to the MainActivity.
	 * 	 
	 * @param Bundle savedInstanceState See {@link http://developer.android.com/reference/android/app/Activity.html}.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// hide action bar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
		
	    setContentView(R.layout.activity_okay);	
		
	    // get message passed from MainActivity and display
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    textMessage = (TextView)findViewById(R.id.textMessage);
		textMessage.setText(message);
		
		buttonExit = (ImageView)findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(new OnClickListener()  {
			@Override
			public void onClick(View v)  {
				// kill process and go back to MainActivity
				android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
			}
		});
	}
	
	/**
	 * 
	 * Called when a key down event has occurred. If the key code is KEYCODE_BACK
	 * then true is returned and the event is ignored.
	 * @param keyCode 	The value in event.getKeyCode().
	 * @param event 	Description of the key event.
	 * @return true if the event is handled. Otherwise allows the OS to handle.
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	 return true;
	     }
	     return super.onKeyDown(keyCode, event);    
	}
}
