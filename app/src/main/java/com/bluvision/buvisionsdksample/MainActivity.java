package com.bluvision.buvisionsdksample;

import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.buvisionsdksample.fragments.BaseFragment;
import com.bluvision.buvisionsdksample.fragments.BeaconDetail;
import com.bluvision.buvisionsdksample.fragments.ListBeaconsFragment;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.TokenPair;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentInterface {

    private final int PERMISSION_REQUEST_COARSE_LOCATION = 1984;
    private final int PERMISSION_REQUEST_STORAGE=1994;
    private final int PERMISSION_REQUEST_INTERNET=1912;

    private HashMap<Class,Fragment> mFragmentHashMap = new HashMap<>();
    private BeaconDetail mBeaconDetail;

    final static private String APP_KEY = "m4256mepdf15tpv";
    final static private String APP_SECRET = "dlduhmug8qy77q5";
    private DropboxAPI<AndroidAuthSession> mDBApi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);





        ((Button) findViewById(R.id.loginBtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
                    }
                }
        );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("This app needs storage access");
                builder.setMessage("Please grant storage access so this app can record sensor readings");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_STORAGE);
                    }

                });
                builder.show();
            }

            if (checkSelfPermission(Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("This app needs internet access");
                builder.setMessage("Please grant internet access");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        requestPermissions(
                                new String[]{Manifest.permission.INTERNET},
                                PERMISSION_REQUEST_INTERNET);
                    }

                });
                builder.show();
            }
        }



        swapFragment(ListBeaconsFragment.class,true,false);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void swapFragment(Class fragmentToOpen, boolean addToBackStack, boolean newInstance) {

        Fragment fragment = getFragment(fragmentToOpen,newInstance );

        if(fragment!=null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.fragmentContainer, fragment, fragment.getClass().getName());
            if (addToBackStack) {
                ft.addToBackStack(fragment.getClass().getName());
            }
            ft.commitAllowingStateLoss();
        }else{
            Log.e("Fragment", "Fragment is null");
        }

    }

    @Override
    public Fragment getFragment(Class nameOfFragment, boolean newInstance) {

        Fragment fragment = mFragmentHashMap.get(nameOfFragment);
        if(fragment==null || newInstance){
            try {
                mFragmentHashMap.put(nameOfFragment, (Fragment)nameOfFragment.newInstance());
                fragment = mFragmentHashMap.get(nameOfFragment);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return fragment;
    }

    @Override
    public void onBeaconSelectedFromList(Beacon beacon) {

        mBeaconDetail = (BeaconDetail) getFragment(BeaconDetail.class,true);
        mBeaconDetail.setBeacon(beacon);

        swapFragment(BeaconDetail.class,true,false);


    }


}
