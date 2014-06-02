package jbs.uw.homework253;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Contains code for implicit notification display
 */
public class ImplicitNotification extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_implicit_intent);

		String output = "Inside Homework 253 implicit intent notification: ";
		TextView dataIntent = (TextView) findViewById(R.id.text2);
    
		// take the data and the extras of the intent
		Uri url = getIntent().getData();
		Bundle extras = getIntent().getExtras();
      
		output = output + url.toString();
		// if there are extras, add them to the output string
		if(extras != null){
			output = output + " from " + extras.getString("from");
		}
		dataIntent.setText(output);
	}
}
