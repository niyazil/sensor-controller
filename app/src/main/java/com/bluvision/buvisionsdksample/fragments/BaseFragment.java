package com.bluvision.buvisionsdksample.fragments;

import com.bluvision.beeks.sdk.domainobjects.Beacon;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by Leandro Salas on 10/15/15.
 */
public class BaseFragment extends Fragment {

    protected FragmentInterface mInterface;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            mInterface = (FragmentInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BottomMenuInterface");
        }
    }


    public interface FragmentInterface {

        void swapFragment(Class fragmentToOpen, boolean addToBackStack, boolean newInstance);

        Fragment getFragment(Class nameOfFragment, boolean newInstance);

        void onBeaconSelectedFromList(Beacon beeacon);

    }
}
