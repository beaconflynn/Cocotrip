package com.zohan.cocotrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unstoppable.submitbuttonview.SubmitButton;

/**
 * Created by imthanapat on 30/1/2018 AD.
 */

public class LoginActivity extends AppCompatActivity{
    private static final String TAG ="LoginActivity";
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SubmitButton sBtnLoading, sBtnProgress;
    private LoginButton btnSignInFacebook;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignInGoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Submit Btn */
        sBtnLoading = (SubmitButton) findViewById(R.id.sbtn_loading);
        sBtnLoading.setOnClickListener(new SubmitButton.OnClickListener() {
            /* Submit Button Click */
            @Override
            public void onClick(View v) {
                //sBtnLoading.doResult(true);             // Right
                sBtnLoading.doResult(false);    // Worng
            }
        });

        /* Facebook : Authentication */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnSignInFacebook = (LoginButton) findViewById(R.id.btn_facebook);
        initialize();
        btnSignInFacebook.setReadPermissions("email","public_profile");
        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sBtnLoading.doResult(true);
                Log.d("auth", "onSuccess: ");
                Log.w(TAG, "Facebook Login Success Token:  " +
                        loginResult.getAccessToken().getToken());
                signInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Error");
                error.printStackTrace();
            }
        });

        /* Google(Gmail) : Authentication */
        btnSignInGoogle = (SignInButton) findViewById(R.id.btn_google);
        initGoogleLogin();
        btnSignInGoogle.setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    /* init Facebook Authen */
    public void initialize(){
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=  null){
                    Log.w(TAG, "onAuthStateChanged - signed_in" + firebaseUser.getUid());
                    Log.w(TAG, "onAuthStateChanged - signed_in" + firebaseUser.getEmail());

                    /* Add */
                    Toast.makeText(LoginActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };
    }

    private void initGoogleLogin(){
        /* web client : 578452117577-0sis74dcvraq7hqfd05ruenan4262sol.apps.googleusercontent.com */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("here", "Connection Failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            /* google authen */
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            /* facebook authen */
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            sBtnLoading.doResult(true);
        }else{

            Log.d("here", String.valueOf(result.getStatus()));
        }
    }


    private void signInFacebookFirebase(AccessToken accessToken){
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(LoginActivity.this, "Facebook Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Facebook Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Google Authentication Success", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Google Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}