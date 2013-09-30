package com.skirrs;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SignInActivity extends Activity {
	
	/*
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	
	private View mLoginFormView;
	
	// JSON Parser object
    JSONParser jParser = new JSONParser();
    
    /*
     * Managing sessions and user sign-ins
     */
	SessionManager sessionManager;
	
	SkirrsProgressDialog signInDialog;

	UserDetailsTask userDetailsTask;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate(savedInstanceState);

		setContentView( R.layout.activity_signin );

		sessionManager = SessionManager.getInstance( getApplicationContext() );
		
		// Set up the login form.
		mEmailView    = ( EditText ) findViewById( R.id.email );
		mPasswordView = ( EditText ) findViewById( R.id.password );

		findViewById( R.id.sign_in_button ).setOnClickListener(
				
				new View.OnClickListener() {
					@Override
					public void onClick( View view ) {
						attemptLogin();
					}
				});

		LoginButton loginButton = (LoginButton) findViewById( R.id.sign_in_fb_button );

		if ( Session.getActiveSession() !=null ) {
			
			if ( Session.getActiveSession().isOpened() ) {
				/* 
				 * An active open session
				 * Hide the loginFrom
				 */
				mLoginFormView = ( View ) findViewById( R.id.login_form );
				mLoginFormView.setVisibility( View.INVISIBLE );
			}
		}
		
		Session.StatusCallback callback =
				
			    new Session.StatusCallback() {
			
			    @Override
			    public void call( Session session, 
			    				  SessionState state,
			    				  Exception exception ) {
			    	
			    	if ( state.isOpened() ) {

			    		loginWithFb( session );
			    		
			    	} if ( state.isClosed() ) {
			    		
			    		mLoginFormView = ( View ) findViewById( R.id.login_form );
						mLoginFormView.setVisibility( View.VISIBLE );
						
			    	}
			       
			    }
		};
		
		loginButton.setSessionStatusCallback( callback );
	}

	
	private void loginWithFb( Session session )
	{
		
		signInDialog = SkirrsProgressDialog.show( this,
												  "",
												  "",
												  true );
		
		/*
		 * Login successful
		 */
		Request request = Request.newMeRequest( session,
				  new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted( GraphUser user,
									 Response response ) {

				if ( user != null ) {
					
					System.out.println( "Hello "
							+ user.getName() + "!");
					
					mEmail = user.getUsername();
					
					userDetailsTask = new UserDetailsTask();
					userDetailsTask.execute( mEmail, user.getName() );
					
				} else {
					
					System.out.println( "User is null!" );
					
				}

			}

		});
		
		Request.executeBatchAsync( request );
		
	}
	
	
	public class UserDetailsTask extends AsyncTask< String, Void, Boolean> {

		@Override
		protected Boolean doInBackground( String... arg0 ) {
			
			// Building Parameters
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            httpParams.add( new BasicNameValuePair( "email_address", arg0[ 0 ] ) );
            httpParams.add( new BasicNameValuePair( "keyword", Util.KEYWORD_USER_DETAILS ) );
            
            String fullName = arg0[ 1 ];
            
            try {
            
            	// getting JSON string from URL
            	JSONObject json = jParser.makeHttpRequest( Util.url, 
            										   	   "POST",
            										   	   httpParams);
            	
            	if ( json != null && json.length() > 0 ) {
                    
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		/*
	            		 * User exists. Nothing to do. Redirect to HomeActivity
	            		 */
	            		System.out.println( "User exists. Launching HomeActivity" );
	            		return true;

	            	} else {

	            		System.out.println( "User does not exist. Registering..." );
	            		
	            		/*
	            		 * Register this user using the emailaddress from FB
	            		 */	            		
	            		Intent registerIntent =
	    						new Intent( getApplicationContext(),
	    									RegisterActivity.class );
	    				
	            		registerIntent.putExtra( "EMAIL_ADDRESS", mEmail );
	            		registerIntent.putExtra( "FULL_NAME",     fullName );
	            		registerIntent.putExtra( "FB_REGISTER",   true );
	            		
	    				startActivity( registerIntent );
	    				finish();

	            		return false;
	            	}
	            	
                } else {
                	System.out.println( "Failed to get user details" );
                	return false;
                }
            	
            } catch ( JSONException j ) {
            	System.out.println( "JSONException: " + j );
            	return false;
            }

		}
		
		
		@Override
		protected void onPostExecute( final Boolean success ) {
		
			signInDialog.dismiss();
			
			if ( success ) {
				
				/* 
				 * Add user data to SessionManager
				 */				
				if ( sessionManager != null ) {
					sessionManager.createLoginSession( mEmail );
				}
				
				/*
				 * Launch home screen activity here
				 */
				Intent homeActIntent =
						new Intent( getApplicationContext(),
									HomeActivity.class );
				
				homeActIntent.putExtra( "EMAIL_ADDRESS", mEmail );
				
				startActivity( homeActIntent );
				finish();
				
			} else {

			}
			
		}
		
		
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu( menu );
		getMenuInflater().inflate( R.menu.login, menu );
		return true;
	}

	
	/**
	 * 
	 * @param v
	 */
	public void launchRegisterActivity( View v ) {
		
		Intent intent = new Intent( this, RegisterActivity.class );
		
		/*
		 * Add email and password from login screen to the intent if the user
		 * has entered those values 
		 */		
		startActivity( intent );
		
		finish();
	}
	
	
	/**
	 * Attempts to sign in with the account info specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		
		if ( mAuthTask != null ) {
			return;
		}

		// Reset errors.
		mEmailView.setError( null );
		mPasswordView.setError( null );

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if ( TextUtils.isEmpty( mPassword ) ) {
			
			mPasswordView.setError( getString( 
					R.string.com_skirrs_signinactivity_error_field_required ) );
			
			focusView = mPasswordView;
			cancel = true;
			
		} else if ( mPassword.length() < 4 ) {
			
			mPasswordView.setError( getString( 
					R.string.com_skirrs_signinactivity_error_invalid_password ) );
			
			focusView = mPasswordView;
			cancel = true;
			
		}

		// Check for a valid email address.
		if ( TextUtils.isEmpty( mEmail ) ) {
			
			mEmailView.setError( getString( 
					R.string.com_skirrs_signinactivity_error_field_required ) );
			
			focusView = mEmailView;
			cancel = true;
			
		} else if ( !mEmail.contains( "@" ) ) {
			
			mEmailView.setError( getString( 
					R.string.com_skirrs_signinactivity_error_invalid_email ) );
			
			focusView = mEmailView;
			cancel = true;
			
		}

		if ( cancel ) {
			
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			
		} else {
			
			signInDialog = SkirrsProgressDialog.show( this,
													  "",
													  "",
													  true );		
			mAuthTask = new UserLoginTask();
			mAuthTask.execute( ( Void ) null );
		}
	}

	
	/**
	 * 
	 */
	public void attemptFbLogin()
	{
		
		signInDialog = SkirrsProgressDialog.show( this,
												  "",
												  "",
												  true );
		
		// start Facebook Login
	    Session.openActiveSession( this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call( Session session,
	    		  			SessionState state,
	    		  			Exception exception ) {
	    	  
				if ( session.isOpened() ) {

									}
	        
	      }
	      
	    });
	    
	}
	
	
	@Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) 
    {
        super.onActivityResult( requestCode, resultCode, data );
        Session.getActiveSession().onActivityResult( this,
        											 requestCode,
        											 resultCode,
        											 data );
    }
	
	
	/**
	 * Represents an asynchronous login task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground( Void... params ) {
			
			// Building Parameters
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            httpParams.add( new BasicNameValuePair( "email_address", mEmail ) );
            httpParams.add( new BasicNameValuePair( "password", mPassword ) );
            httpParams.add( new BasicNameValuePair( "keyword", "sign_in" ) );
            
            boolean signin = false;

            try {
            	
            	// getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest( Util.url, 
                										   "POST",
                										   httpParams);
            	
                if ( json != null && json.length() > 0 ) {
                
                	System.out.println( "json: " + json );
                	
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		/*
	            		 * Sign-in successful
	            		 */
	            		System.out.println( "Sign-in successful" );
	            		signin = true;   		
	            		
	            	} else {
	            		
	            		/*
	            		 * Incorrect user-name or password
	            		 */
	            		signin = false;
	            	}
                }
            	
            } catch ( JSONException j ) {
            	System.out.println( "JSONException: " + j );
            }
            
			return signin;
		}

		@Override
		protected void onPostExecute( final Boolean success ) {
			
			mAuthTask = null;
			
			signInDialog.dismiss();

			if ( success ) {
				
				/* 
				 * Add user data to SessionManager
				 */				
				if ( sessionManager != null ) {
					sessionManager.createLoginSession( mEmail );
				}
				
				/*
				 * Launch home screen activity here
				 */
				Intent homeActIntent = new Intent( getApplicationContext(), 
												   HomeActivity.class );
				
				homeActIntent.putExtra( "EMAIL_ADDRESS", mEmail );
				
				startActivity( homeActIntent );
				finish();
				
			} else {
				
				mPasswordView.setError( getString( 
				R.string.com_skirrs_signinactivity_error_incorrect_password ) );
				
				mPasswordView.requestFocus();
				
			}
		}

		@Override
		protected void onCancelled() {
			
			mAuthTask = null;
			
		}
		
	}

}
