package meet.lunch.lunchmeet.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import meet.lunch.lunchmeet.R;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    private ProgressDialog mRegProgressDialog;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    /* *************************************
     *              FACEBOOK               *
     ***************************************/
    /* The login button for Facebook */
    private ImageButton mFacebookLoginButton;
    /* The callback manager for Facebook */
    private CallbackManager mFacebookCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mFacebookAccessTokenTracker;


    /* *************************************
     *              GOOGLE                 *
     ***************************************/
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
     * without waiting. */
    private boolean mGoogleLoginClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
     * sign-in. */
    private ConnectionResult mGoogleConnectionResult;

    /* The login button for Google */
    private ImageButton mGoogleLoginButton;

    /* *************************************
     *              TWITTER                *
     ***************************************/
    public static final int RC_TWITTER_LOGIN = 2;

    private ImageButton mTwitterLoginButton;

    /* *************************************
     *              PASSWORD               *
     ***************************************/
    private Button mPasswordLoginButton;
    private Button mSignUpSubmit;
    private AppCompatEditText mEmailEditText;
    private AppCompatEditText mPasswordEditText;

    private AppCompatEditText mEmailSignUpEditText;
    private AppCompatEditText mPasswordSignUpEditText;
    private AppCompatEditText mFirstNameSignUpEditText;
    private AppCompatEditText mLastNameSignUpEditText;
    private AppCompatEditText mPhoneNumberSignUpEditText;
    private AppCompatEditText mBioSignUpEditText;

    private CardView mSignUpCardView;
    private CardView mSignInCardView;

    private Button mSignUpButton;
    private Button mSignInButton;

    private boolean activityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        /* Load the view and display it */
        setContentView(R.layout.activity_login);

        /* *************************************
         *              FACEBOOK               *
         ***************************************/
        /* Load the Facebook login button and set up the tracker to monitor access token changes */
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = (ImageButton) findViewById(R.id.login_with_facebook);
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                LoginActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };

        /* *************************************
         *               GOOGLE                *
         ***************************************/
        /* Load the Google login button */
        mGoogleLoginButton = (ImageButton) findViewById(R.id.login_with_google);
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleLoginClicked = true;
                if (!mGoogleApiClient.isConnecting()) {
                    if (mGoogleConnectionResult != null) {
                        resolveSignInError();
                    } else if (mGoogleApiClient.isConnected()) {
                        getGoogleOAuthTokenAndLogin();
                    } else {
                    /* connect API now */
                        Log.d(TAG, "Trying to connect to Google API");
                        mGoogleApiClient.connect();
                    }
                }
            }
        });
        /* Setup the Google API object to allow Google+ logins */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        /* *************************************
         *                TWITTER              *
         ***************************************/
        mTwitterLoginButton = (ImageButton) findViewById(R.id.login_with_twitter);
        mTwitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithTwitter();
            }
        });

        /* *************************************
         *               PASSWORD              *
         ***************************************/
        mPasswordLoginButton = (Button) findViewById(R.id.login_with_password);
        mPasswordLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithPassword();
            }
        });

        mSignUpSubmit = (Button) findViewById(R.id.sign_up_submit);
        mSignUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


        mEmailEditText = (AppCompatEditText) findViewById(R.id.email);
        mPasswordEditText = (AppCompatEditText) findViewById(R.id.password);

        mEmailSignUpEditText = (AppCompatEditText) findViewById(R.id.email_sign_up);
        mPasswordSignUpEditText = (AppCompatEditText) findViewById(R.id.password_sign_up);
        mFirstNameSignUpEditText = (AppCompatEditText) findViewById(R.id.first_name_sign_up);
        mLastNameSignUpEditText = (AppCompatEditText) findViewById(R.id.last_name_sign_up);
        mPhoneNumberSignUpEditText = (AppCompatEditText) findViewById(R.id.phone_number_sign_up);
        mBioSignUpEditText = (AppCompatEditText) findViewById(R.id.bio_sign_up);

        mSignInCardView = (CardView) findViewById(R.id.sign_in_cardlist);
        mSignUpCardView = (CardView) findViewById(R.id.sign_up_cardlist);

        mSignInButton = (Button) findViewById(R.id.signInToggle);
        mSignUpButton = (Button) findViewById(R.id.signUpToggle);

        /* *************************************
         *               GENERAL               *
         ***************************************/

        /* Create the Firebase ref that is used for all authentication with Firebase */
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Signing you in...");
        mAuthProgressDialog.setCancelable(false);
        //mAuthProgressDialog.show();

        mRegProgressDialog = new ProgressDialog(this);
        mRegProgressDialog.setTitle("Loading");
        mRegProgressDialog.setMessage("Registering your new account...");
        mRegProgressDialog.setCancelable(false);

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
        }

        // if changing configurations, stop tracking firebase session.
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    /**
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Map<String, String> options = new HashMap<String, String>();
        if (requestCode == RC_GOOGLE_LOGIN) {
            /* This was a request by the Google API */
            if (resultCode != RESULT_OK) {
                mGoogleLoginClicked = false;
            }
            mGoogleIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if (requestCode == RC_TWITTER_LOGIN) {
            options.put("oauth_token", data.getStringExtra("oauth_token"));
            options.put("oauth_token_secret", data.getStringExtra("oauth_token_secret"));
            options.put("user_id", data.getStringExtra("user_id"));
            authWithFirebase("twitter", options);
        } else {
            /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* If a user is currently authenticated, display a logout menu */
        if (this.mAuthData != null) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signInOnClick(View v){
        mSignInButton.setTypeface(Typeface.DEFAULT_BOLD);
        mSignUpButton.setTypeface(Typeface.DEFAULT);
        mSignInCardView.setVisibility(View.VISIBLE);
        mSignUpCardView.setVisibility(View.GONE);
    }

    public void signUpOnClick(View v){
        mSignUpButton.setTypeface(Typeface.DEFAULT_BOLD);
        mSignInButton.setTypeface(Typeface.DEFAULT);
        mSignUpCardView.setVisibility(View.VISIBLE);
        mSignInCardView.setVisibility(View.GONE);
    }

    public void forgotPasswordOnClick(View view) {
        Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.mAuthData != null) {
            /* logout of Firebase */
            mFirebaseRef.unauth();
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Facebook/Google+ after logging out of Firebase. */
            if (this.mAuthData.getProvider().equals("facebook")) {
                /* Logout from Facebook */
                LoginManager.getInstance().logOut();
            } else if (this.mAuthData.getProvider().equals("google")) {
                /* Logout from Google+ */
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
            }
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }

    /**
     * This method will attempt to authenticate a user to firebase given an oauth_token (and other
     * necessary parameters depending on the provider)
     */
    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            mAuthProgressDialog.show();
            if (provider.equals("twitter")) {
                // if the provider is twitter, we pust pass in additional options, so use the options endpoint
                mFirebaseRef.authWithOAuthToken(provider, options, new StaleAuthResultHandler(provider));
            } else {
                // if the provider is not twitter, we just need to pass in the oauth_token
                mFirebaseRef.authWithOAuthToken(provider, options.get("oauth_token"), new StaleAuthResultHandler(provider));
            }
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null && !activityStarted) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
            activityStarted = true;
            finish();
        } else {

        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class FreshAuthResultHandler implements Firebase.AuthResultHandler {

        String firstName;
        String lastName;
        String phoneNumber;
        String bio;

        public FreshAuthResultHandler(String firstName, String lastName, String phoneNumber, String bio) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.bio = bio;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("firstName", firstName);
            map.put("lastName", lastName);
            map.put("phoneNumber", phoneNumber);
            map.put("bio", bio);
            mFirebaseRef.child("users").child(authData.getUid()).setValue(map);

            mAuthProgressDialog.hide();
            Log.i("fresh auth successful", "true");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.getMessage());
        }
    }

    /**
     * Utility class for authentication results
     */
    private class StaleAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public StaleAuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();
            showErrorDialog(firebaseError.getMessage());
        }
    }

    /* ************************************
     *             FACEBOOK               *
     **************************************
     */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mAuthProgressDialog.show();
            mFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new StaleAuthResultHandler("facebook"));
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (this.mAuthData != null && this.mAuthData.getProvider().equals("facebook")) {
                mFirebaseRef.unauth();
                setAuthenticatedUser(null);
            }
        }
    }

    /* ************************************
     *              GOOGLE                *
     **************************************
     */
    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mGoogleConnectionResult.hasResolution()) {
            try {
                mGoogleIntentInProgress = true;
                mGoogleConnectionResult.startResolutionForResult(this, RC_GOOGLE_LOGIN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mGoogleIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getGoogleOAuthTokenAndLogin() {
        mAuthProgressDialog.show();
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    token = GoogleAuthUtil.getToken(LoginActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mGoogleLoginClicked = false;
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */
                    mFirebaseRef.authWithOAuthToken("google", token, new StaleAuthResultHandler("google"));
                } else if (errorMessage != null) {
                    mAuthProgressDialog.hide();
                    showErrorDialog(errorMessage);
                }
            }
        };
        task.execute();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        /* Connected with Google API, use this to authenticate with Firebase */
        getGoogleOAuthTokenAndLogin();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mGoogleIntentInProgress) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            mGoogleConnectionResult = result;

            if (mGoogleLoginClicked) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(TAG, result.toString());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ignore
    }

    /* ************************************
     *               TWITTER              *
     **************************************
     */
    private void loginWithTwitter() {
        startActivityForResult(new Intent(this, TwitterOAuthActivity.class), RC_TWITTER_LOGIN);
    }

    /* ************************************
     *              PASSWORD              *
     **************************************
     */
    public void loginWithPassword() {
        String email = mEmailEditText.getEditableText().toString();
        String password = mPasswordEditText.getEditableText().toString();

        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(email, password, new StaleAuthResultHandler("password"));
    }

    public void registerUser(){
        final String email = mEmailSignUpEditText.getEditableText().toString();
        final String password = mPasswordSignUpEditText.getEditableText().toString();
        final String firstName = mFirstNameSignUpEditText.getEditableText().toString();
        final String lastName = mLastNameSignUpEditText.getEditableText().toString();
        final String phoneNumber = mPhoneNumberSignUpEditText.getEditableText().toString();
        final String bio = mBioSignUpEditText .getEditableText().toString();

        if(!email.isEmpty()&&!password.isEmpty()&&!firstName.isEmpty()&&!lastName.isEmpty()&&!phoneNumber.isEmpty()&&!bio.isEmpty()){
            mRegProgressDialog.show();

            mFirebaseRef.createUser(email,password,new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    mRegProgressDialog.hide();
                    mAuthProgressDialog.show();
                    mFirebaseRef.authWithPassword(email, password, new FreshAuthResultHandler(firstName,lastName,phoneNumber,bio));
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    mRegProgressDialog.hide();
                    showErrorDialog(firebaseError.getMessage());
                }
            });
        } else {
            showErrorDialog("Please fill out all fields.");
        }
    }
}
