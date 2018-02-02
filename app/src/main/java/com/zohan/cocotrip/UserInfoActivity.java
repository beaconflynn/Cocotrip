package com.zohan.cocotrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by imthanapat on 31/1/2018 AD.
 */

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG ="MeActivity" ;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button signOut;
    private CircleImageView profileImg;
    private TextView profileName;
    private TextView profileEmail;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        signOut = (Button)findViewById(R.id.signOut);
        profileImg = (CircleImageView) findViewById(R.id.profile_img);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        init();
        initGoogleLogin();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut(){
        if (firebaseAuth.getInstance() != null){
            FirebaseAuth currentUser = firebaseAuth.getInstance();
            Log.d("here", "signOut: ");
            Intent i = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

            /* Sign out */
            currentUser.signOut();
            LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    public void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=  null){
                    if(firebaseUser.getDisplayName() != null) {
                        profileName.setText(firebaseUser.getDisplayName());
                    }
                    if(firebaseUser.getEmail() != null) {
                        profileEmail.setText(firebaseUser.getEmail());
                    }
                    Picasso.with(UserInfoActivity.this).load(firebaseUser.getPhotoUrl()).into(profileImg);
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

