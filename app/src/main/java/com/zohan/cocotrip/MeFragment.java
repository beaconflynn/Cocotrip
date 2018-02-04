package com.zohan.cocotrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pea on 2/4/2018.
 */

public class MeFragment extends BaseFragment {
    private View cachedView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG ="MeFragment" ;
    private Button signOut;
    private CircleImageView profileImg;
    private TextView profileName;
    private TextView profileEmail;

    public static MeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (cachedView == null) {
            cachedView = inflater.inflate(R.layout.fragment_me, container, false);
            signOut = cachedView.findViewById(R.id.signOut);
            profileImg = cachedView.findViewById(R.id.profile_img);
            profileName = cachedView.findViewById(R.id.profile_name);
            profileEmail = cachedView.findViewById(R.id.profile_email);
        }
        return cachedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).signOut();
            }
        });
    }

    public void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Log.d("xv", firebaseUser.getDisplayName());
        if (firebaseUser !=  null){
            if(firebaseUser.getDisplayName() != null) {
                profileName.setText(firebaseUser.getDisplayName());
            }
            if(firebaseUser.getEmail() != null) {
                profileEmail.setText(firebaseUser.getEmail());
            }
            Picasso.with(getActivity().getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(profileImg);
        }else {
            Log.w(TAG, "Firebase Error");
        }
    }
}
