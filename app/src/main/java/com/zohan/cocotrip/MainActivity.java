package com.zohan.cocotrip;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavSwitchController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;
import com.ncapdevi.fragnav.tabhistory.FragNavTabHistoryController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener{
    private static final String TAG ="MainActivity" ;
    private final int INDEX_NEWS = FragNavController.TAB1;
    private final int INDEX_NEARME = FragNavController.TAB2;
    private final int INDEX_MAPS = FragNavController.TAB3;
    private final int INDEX_COUNTRY = FragNavController.TAB4;
    private final int INDEX_ME = FragNavController.TAB5;
    private FragNavController mNavController;
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGoogleLogin();
        boolean initial = savedInstanceState == null;
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        if (initial) {
            bottomBar.selectTabAtPosition(INDEX_NEWS);
        }
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container)
                .transactionListener(this)
                .rootFragmentListener(this, 5)
                .popStrategy(FragNavTabHistoryController.UNIQUE_TAB_HISTORY)
                .switchController(new FragNavSwitchController() {
                    @Override
                    public void switchTab(int index, FragNavTransactionOptions transactionOptions) {
                        bottomBar.selectTabAtPosition(index);
                    }
                })
                .build();



        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_news:
                        mNavController.switchTab(INDEX_NEWS);
                        break;
                    case R.id.tab_nearme:
                        mNavController.switchTab(INDEX_NEARME);
                        break;
                    case R.id.tab_maps:
                        mNavController.switchTab(INDEX_MAPS);
                        break;
                    case R.id.tab_country:
                        mNavController.switchTab(INDEX_COUNTRY);
                        break;
                    case R.id.tab_me:
                        mNavController.switchTab(INDEX_ME);
                        break;
                }
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }, initial);

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                /* Pressed Again */
                mNavController.clearStack();
                //Toast.makeText(getApplicationContext(),"show" + tabId, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mNavController.popFragment()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case INDEX_NEWS:
                return NewsFragment.newInstance(0);

            case INDEX_NEARME:
                return NearMeFragment.newInstance(0);
            case INDEX_MAPS:
                return MapsFragment.newInstance(0);
            case INDEX_COUNTRY:
                return CountryFragment.newInstance(0);
            case INDEX_ME:
                return MeFragment.newInstance(0);
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNavController.popFragment();
                break;
        }
        return true;
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

    public void signOut(){
        if (firebaseAuth.getInstance() != null){
            FirebaseAuth currentUser = firebaseAuth.getInstance();
            Log.d("here", "signOut: ");
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

            /* Sign out */
            currentUser.signOut();
            LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }
}
