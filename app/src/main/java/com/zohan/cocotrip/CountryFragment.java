package com.zohan.cocotrip;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by pea on 2/4/2018.
 */

public class CountryFragment extends BaseFragment {
    private View cachedView;

    public static CountryFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        CountryFragment fragment = new CountryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (cachedView == null) {
            cachedView = inflater.inflate(R.layout.fragment_country, container, false);
            //btn = cachedView.findViewById(R.id.button);
        }
        return cachedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(NewsFragment.newInstance(mInt + 1));
                }
            }
        });
        btn.setText(getClass().getSimpleName() + " " + mInt);
        */
    }
}
