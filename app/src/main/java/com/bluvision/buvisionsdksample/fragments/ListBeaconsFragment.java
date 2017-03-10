package com.bluvision.buvisionsdksample.fragments;

import com.bluvision.beeks.sdk.constants.BeaconType;
import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.interfaces.BeaconListener;
import com.bluvision.beeks.sdk.util.BeaconManager;
import com.bluvision.buvisionsdksample.BluvisionSampleSDKApplication;
import com.bluvision.buvisionsdksample.R;
import com.bluvision.buvisionsdksample.adapters.BeaconsListAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leandro Salas on 10/15/15.
 */
public class ListBeaconsFragment extends BaseFragment implements BeaconListener {

    private BeaconManager mBeaconManager;

    private View rootView;

    private BeaconsListAdapter mBeaconsListAdapter;

    private List<Beacon> beaconList = new ArrayList<>();
    private ListView lstBeacons;

    private boolean scaning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Foo", "onCreate ");

        mBeaconManager = ((BluvisionSampleSDKApplication) getActivity().getApplication())
                .getBeaconManager();

        // Add this class to listen when a beacon was found.
        mBeaconManager.addBeaconListener(this);

        /** Add rules to specify what type of beacons you want to discover
         *  in this sample app we are discovering Sbeacons and IBeacons.
         *  you can remove the ones you don't want to discover.
         */
        mBeaconManager.addRuleToIncludeScanByType(BeaconType.S_BEACON);
        //mBeaconManager.addRuleRestrictionToIncludeSID("CDDF3C6FAE7A57F8");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        setRetainInstance(true);

        rootView = inflater.inflate(R.layout.fragment_beacons, container, false);

        lstBeacons = (ListView) rootView.findViewById(R.id.lstBeacons);

        ((FloatingActionButton) rootView.findViewById(R.id.action_start)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!scaning) {
                            startScan();
                        }

                    }
                }
        );

        ((FloatingActionButton) rootView.findViewById(R.id.action_stop)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (scaning) {
                            //stopScan();
                            mBeaconManager.stop();
                        }

                    }
                }
        );

        mBeaconsListAdapter = new BeaconsListAdapter(getActivity(), beaconList);
        lstBeacons.setAdapter(mBeaconsListAdapter);

        lstBeacons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                stopScan();
                Beacon beacon = beaconList.get(position);
                mInterface.onBeaconSelectedFromList(beacon);


            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        mBeaconManager.removeBeaconListener(this);

    }

    public void startScan() {
        mBeaconManager.startScan();
        scaning = true;
    }

    public void stopScan() {
        mBeaconManager.stopScan();
        scaning = false;
    }

    /**
     * Start of BeaconListener methods
     * This listener is used to listen when a beacon is discovered
     */

    @Override
    public void onBeaconFound(final Beacon beacon) {
        //beacon is a generic beacon you can transform it to its respective Beacon (Eddystone, IBeacon, SBeacon)

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(beacon!=null) {
                    beaconList.add(beacon);
                    mBeaconsListAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public void bluetoothIsNotEnabled() {
        Toast.makeText(getActivity(),"Please activate your Bluetooth connection", Toast.LENGTH_LONG).show();
    }

}
