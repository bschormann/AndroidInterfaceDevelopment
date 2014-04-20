package jbs.example.homework251;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Brett Schormann
 * @version 1.0
 * 
 * Displays opening window.
 * Checks input from user.
 * Starts OkayActivity.
 *
 */
public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "";
	
	private String emailOk;
	private String passwordOk;
	
	private ImageView buttonSignin;
	private EditText loginEmail;
	private EditText loginPassword;
	private TextView textMessage; 
	
	@SuppressWarnings("unused")
	private View focusView;
	private Handler textViewimeHandler;	// handler used to time messages
	private Intent intent;

	/**
	 * 
	 * Hides the Action Bar.
	 * Handles inputs from main window using checkInputs()after 
	 * the SignIn button is activated.
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

	    setContentView(R.layout.activity_main);
				
		buttonSignin = (ImageView)findViewById(R.id.buttonSignin);
		loginEmail = (EditText)findViewById(R.id.loginEmail);
		loginPassword = (EditText)findViewById(R.id.loginPassword);
		textMessage = (TextView)findViewById(R.id.textMessage);
				
		buttonSignin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)  {
				checkInputs();
			}
		});
	}
	
	/**
	 * 
	 * Called after the SignIn button is activated.
	 * Checks inputs (email address and password) for correctness.
	 * 
	 * If the correctness tests pass, the OkayActivity is started
	 * and passed a message with the email address and password. Upon
	 * return from the OkayActivity, the process is killed which
	 * will returns control to the OS. 
	 * 
	 * If the correctness tests don't pass, an error message is displayed
	 * for 5 seconds. 
	 * 
	 */
	private void checkInputs() {
		
		focusView = null;
		
		buttonSignin.setImageDrawable(getResources().getDrawable(R.drawable.signinbuttonselected));
		
		String email = loginEmail.getText().toString();
		String password = loginPassword.getText().toString();
		
		emailOk = getResources().getString(R.string.user_email);
		passwordOk = getResources().getString(R.string.user_password);
		
		if(email.equals(emailOk) && password.equals(passwordOk))  {
			startOkayActivity();	
			// kill process when OkayActivity exits
			android.os.Process.killProcess(android.os.Process.myPid());
	        System.exit(0);
		} else {
			textMessage.setTextColor(getResources().getColor(R.color.errorColor));
			if (TextUtils.isEmpty(email)) {
				textMessage.setText(getResources().getString(R.string.empty_email));
	            focusView = loginEmail;
	        } else if (!(email.contains("@"))) {
	        	textMessage.setText(getResources().getString(R.string.invalid_email));
	            focusView = loginEmail;
	        } else if(!(email.equals(emailOk)) && !(password.equals(passwordOk))) {
				textMessage.setText(getResources().getString(R.string.both_incorrect) +
						" " + emailOk + " and " + passwordOk);
				focusView = loginEmail;
			} else if(!(email.equals(emailOk))) {
				textMessage.setText(getResources().getString(R.string.email_incorrect) + 
						" " + emailOk);
				focusView = loginEmail;
			} else {
				textMessage.setText(getResources().getString(R.string.password_incorrect) + 
						" " + passwordOk);
				focusView = loginPassword;
			}
			textMessage.setVisibility(View.VISIBLE);
			// error message wiped out after 5 seconds
			textViewimeHandler = new Handler(); 
			final Runnable updateTimeTask = new Runnable() {
			    public void run() {
			    	textMessage.setVisibility(View.INVISIBLE);        
					buttonSignin.setImageDrawable(getResources().getDrawable(R.drawable.signinbutton));
			    }
			};
			textViewimeHandler.postDelayed(updateTimeTask, 5000); // 5 Seconds
		}
	}		
	
	/**
	 * 
	 * Starts the OkayActivity passing the user email address and password.
	 * 
	 */
	private void startOkayActivity() {
		intent = new Intent(this, OkayActivity.class);
	    String message = "Welcome " + emailOk + " (" + passwordOk + ") to CP250 - Homework 251";
	    intent.putExtra(EXTRA_MESSAGE, message);
	    startActivity(intent);
	}
}
