package jbs.uw.homework253;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Contains code for explicit notification display
 */
public class ExplicitNotification extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_explicit_intent);
		CharSequence cs = "Inside Homework 253 explicit intent notification: ";
		int id=0;
      
		Bundle extras = getIntent().getExtras();
			if (extras == null) {
				cs = "error";
			} else {
			id = extras.getInt("notificationId");
			}
		TextView t = (TextView) findViewById(R.id.text1);
		cs = cs + "with id = " + id;
		t.setText(cs);
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// remove the notification with the specific id
		notificationManager.cancel(id);
	}
}
