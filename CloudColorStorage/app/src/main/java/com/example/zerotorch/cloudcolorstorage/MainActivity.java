package com.example.zerotorch.cloudcolorstorage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    // Google App Engine - Base URL
    static final public String API_URL = "http://poolj-cs496-colors.appspot.com/";

    // Google Signin Variables
    private SignInButton login;
    private TextView welcomeText;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    // Request Codes
    static class REQUEST_CODE {
        public static final int COLOR_PICKER = 0;
        public static final int GET_COLOR = 1;
        public static final int ADD_COLOR = 2;
        public static final int UPDATE_COLOR = 3;
        public static final int DELETE_COLOR = 4;
        public static final int GET_USER = 5;
        public static final int ADD_USER = 6;
        public static final int UPDATE_USER = 7;
        public static final int DELETE_USER = 8;
        public static final int LOGIN = 100;
    }

    // Result Codes
    static class RESULT_CODE {
        public static final int RESULT_OK = 0;
        public static final int RESULT_CANCELED = 1;
        public static final int RESULT_DELETE = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set onClick Events
        findViewById(R.id.activity_login_googleSignIn).setOnClickListener(this);
        findViewById(R.id.activity_login_btn_signOut).setOnClickListener(this);
        findViewById(R.id.activity_login_btn_disconnect).setOnClickListener(this);

        // Google Sign In
        //http://stackoverflow.com/questions/34099208/google-sign-in-requestidtoken-returns-null
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        welcomeText = (TextView) findViewById(R.id.activity_login_tV_welcome);
        login = (SignInButton) findViewById(R.id.activity_login_googleSignIn);
        login.setSize(SignInButton.SIZE_WIDE);
        login.setScopes(googleSignInOptions.getScopeArray());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("SignIn","onConnectionFailed - connectionResult: " + connectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE.LOGIN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // Check sign in status
            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                welcomeText.setText("Welcome, " + googleSignInAccount.getDisplayName() + "!");
                hideSignIn(true);
                // Set onClick for image
                findViewById(R.id.activity_login_img_cloud).setOnClickListener(this);

                // Run Async Login Verification
                API_Validate_Login api_validate_login = new API_Validate_Login(this);
                api_validate_login.execute(googleSignInAccount.getIdToken());
            }
            else {  // Sign in unsuccessful
                hideSignIn(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        TextView tV_userID = (TextView) findViewById(R.id.activity_login_tV_userID);
        switch(view.getId()) {
            case (R.id.activity_login_googleSignIn):  // Sign in button
                signIn();
                break;
            case (R.id.activity_login_btn_signOut):  // Sign out button
                signOut();
                Toast.makeText(this,"Successfully signed out!",Toast.LENGTH_SHORT).show(); // Toast Signed out
                findViewById(R.id.activity_login_img_cloud).setOnClickListener(null);  // Disable onClick
                // Clear userID
                tV_userID = (TextView) findViewById(R.id.activity_login_tV_userID);
                tV_userID.setText(null);
                break;
            case (R.id.activity_login_btn_disconnect):  // Disconnect button
                disconnect();
                // Toast Signed out
                Toast.makeText(this,"Successfully disconnected!",Toast.LENGTH_SHORT).show();
                findViewById(R.id.activity_login_img_cloud).setOnClickListener(null);  // Disable onClick
                // Clear userID
                tV_userID.setText(null);
                break;
            case (R.id.activity_login_img_cloud):  // Cloud image
                // Get userID from textView
                TextView userID = (TextView) findViewById(R.id.activity_login_tV_userID);
                String sub = userID.getText().toString();

                if (userID != null) {
                    Intent userActivity = new Intent(getApplicationContext(), UserInfoActivity.class);
                    userActivity.putExtra("sub", sub);
                    startActivity(userActivity);
                }
                else {
                    Toast.makeText(this,"Google account could not be validated!",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void signIn() {
        // Sign in using google accounts
        Intent signInIntnet = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntnet,REQUEST_CODE.LOGIN);
    }

    private void signOut() {
        // Sign out of google account
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        hideSignIn(false);
                    }
                }
        );

    }

    private void disconnect() {
        // Disconnect google account
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        hideSignIn(false);
                    }
                }
        );
    }

    // Updates the user interface based on success of sign in
    private void hideSignIn (boolean signedIn) {
        if (signedIn) {
            // Show welcome message
            findViewById(R.id.activity_login_tV_welcome).setVisibility(View.VISIBLE);

            // Update button visibility
            findViewById(R.id.activity_login_lL_buttonBar).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_login_googleSignIn).setVisibility(View.GONE);
        }
        else {
            // Hide welcome message
            findViewById(R.id.activity_login_tV_welcome).setVisibility(View.GONE);

            // Update button visibility
            findViewById(R.id.activity_login_lL_buttonBar).setVisibility(View.GONE);
            findViewById(R.id.activity_login_googleSignIn).setVisibility(View.VISIBLE);
        }
    }
}
