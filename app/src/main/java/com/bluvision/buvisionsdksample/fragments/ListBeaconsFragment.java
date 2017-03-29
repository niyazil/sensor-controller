package com.bluvision.buvisionsdksample.fragments;

import com.bluvision.beeks.sdk.constants.BeaconType;
import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.domainobjects.SBeacon;
import com.bluvision.beeks.sdk.interfaces.BeaconListener;
import com.bluvision.beeks.sdk.util.BeaconManager;
import com.bluvision.buvisionsdksample.BluvisionSampleSDKApplication;
import com.bluvision.buvisionsdksample.Extra.BeaconDetailReduced;
import com.bluvision.buvisionsdksample.MainActivity;
import com.bluvision.buvisionsdksample.R;
import com.bluvision.buvisionsdksample.adapters.BeaconsListAdapter;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;



import java.util.Locale;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.Date;
import jxl.*;
import jxl.write.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.RowsExceededException;


public class ListBeaconsFragment extends BaseFragment implements BeaconListener {

    private BeaconManager mBeaconManager;

    private View rootView;

    private BeaconsListAdapter mBeaconsListAdapter;

    private List<Beacon> beaconList = new ArrayList<>();
    private ListView lstBeacons;

    private boolean scaning = false;
    private BeaconDetailReduced beacon=new BeaconDetailReduced();
    private HashMap<String,Integer> beaconHashMap = new HashMap<>();

    final static private String APP_KEY = "m4256mepdf15tpv";
    final static private String APP_SECRET = "dlduhmug8qy77q5";
    private DropboxAPI<AndroidAuthSession> mDBApi;




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
        //mBeaconManager.addRuleToIncludeScanByType(BeaconType.S_BEACON);
        mBeaconManager.addRuleRestrictionToIncludeSID("7159F19768D2A171");
        mBeaconManager.addRuleRestrictionToIncludeSID("A127870322513F6A");
        mBeaconManager.addRuleRestrictionToIncludeSID("FBB44C2E84AB40E3");
        mBeaconManager.addRuleRestrictionToIncludeSID("582A8CF7C8193BFA");
        mBeaconManager.addRuleRestrictionToIncludeSID("46532D736FC97E89");
        mBeaconManager.addRuleRestrictionToIncludeSID("8E453771B5785ED8");
        mBeaconManager.addRuleRestrictionToIncludeSID("994C2A3D97C3972D");


        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);







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
                            stopScan();
                           // mBeaconManager.stop();
//                            SBeacon beacon0=(SBeacon) beaconList.get(0);
//                            SBeacon beacon1=(SBeacon) beaconList.get(1);
//                            Log.e("First beacon",String.valueOf(beacon0.getsId()));
//                            Log.e("Second beacon",String.valueOf(beacon1.getsId()));

                            /*BeaconDetailReduced beacon0=new BeaconDetailReduced();
                            beacon0.sBeacon=(SBeacon) beaconList.get(0);
                            Log.e("First beacon",String.valueOf(beacon0.sBeacon.getsId()));
                            beacon0.sBeacon.setBeaconConfigurationListener(beacon0);

                            //Connect
                            beacon0.sBeacon.connect(getActivity(),null);*/

                            //Disconnect
                            /*beacon0.sBeacon.disconnect();
                            SystemClock.sleep(10000);*/

                            /*BeaconDetailReduced beacon1=new BeaconDetailReduced();
                            beacon1.sBeacon=(SBeacon) beaconList.get(1);
                            Log.e("Second beacon",String.valueOf(beacon1.sBeacon.getsId()));
                            beacon1.sBeacon.setBeaconConfigurationListener(beacon1);

                            //Connect
                            beacon1.sBeacon.connect(getActivity(),null);
                            //Disconnect
                            //beacon1.sBeacon.disconnect();*/


                            //Assign second beacon to the same variable
                           /* beacon0.sBeacon=(SBeacon) beaconList.get(1);
                            //Connect
                            beacon0.sBeacon.connect(getActivity(),null);*/
                            beacon.beaconList=beaconList;
                            beacon.beaconBirth();
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


        ((Button)rootView.findViewById(R.id.readResults)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    Log.e("Does it work?","Yes");

                        try{

                            //Open excel file
                            AssetManager am = getActivity().getAssets();
                            InputStream is = am.open("power1.xls");
                            Workbook wb=Workbook.getWorkbook(is);
                            Sheet sheet=wb.getSheet(0);
                            int numRows=sheet.getRows();
                            int numCol=sheet.getColumns();

                            //Read SID and read power allocation from file and store
                            String excelContents="";
                            for(int row=0;row<numRows;row++){
                                Cell cell=sheet.getCell(0,row);
                                String currentSID=cell.getContents();
                                cell=sheet.getCell(1,row);
                                String currentPower= cell.getContents();

                                //Fill in beaconHashMap with beacon SIDs and allocated powers

                                beaconHashMap.put(currentSID,Integer.valueOf(currentPower));
                                Log.e("Current SID and power",String.valueOf(beaconHashMap.get(currentSID)));

                            }


                        }catch(Exception e){
                            Log.e("Read exception","Yes");
                        }
                        beacon.beaconHashMap=beaconHashMap;




                        }

                    }
                );

        ((Button)rootView.findViewById(R.id.recordReadings)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String Fnamexls="sensorReadings"  + ".xls";
                        File sdCard = Environment.getExternalStorageDirectory();
                        File directory = new File (sdCard.getAbsolutePath() + "/newfolder");
                        directory.mkdirs();
                        File file = new File(directory, Fnamexls);

                        WorkbookSettings wbSettings = new WorkbookSettings();

                        wbSettings.setLocale(new Locale("en", "EN"));

                        WritableWorkbook workbook;

                        Log.e("Where are we?","1");



                            try {
                                int a = 1;
                                workbook = Workbook.createWorkbook(file, wbSettings);
                                //workbook.createSheet("Report", 0);
                                WritableSheet sheet = workbook.createSheet("First Sheet", 0);
                                Label label0 = new Label(0, 0, "SID");
                                Label label1 = new Label(1, 0, "Temperature");

                                try{
                                    sheet.addCell(label0);
                                    sheet.addCell(label1);
                                }catch(Exception e){
                                    Log.e("Headings","Couldn't add");
                                }

                                for(int i=0;i<beaconList.size();i++){
                                //get SID and temperature of currently selected beacon
                                SBeacon currentBeacon=(SBeacon) beaconList.get(i);
                                String currentSID=currentBeacon.getsId();
                                String currentTemperature=String.valueOf(currentBeacon.getTemperature());
                                Log.e("Get SID and temperature",currentSID+": "+currentTemperature);


                                Label label2 = new Label(0, i+1, currentSID);
                                Label label3 = new Label(1, i+1, currentTemperature);
                                Log.e("Current row",String.valueOf(i+1));

                                Log.e("Where are we?","2");
                                try {
                                    sheet.addCell(label2);
                                    sheet.addCell(label3);
                                    Log.e("Where are we?","3");
                                } catch (RowsExceededException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Where are we?","4");
                                } catch (WriteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Where are we?","5");
                                }



                                }

                                workbook.write();
                                try {
                                    workbook.close();
                                    Log.e("Where are we?","6");
                                } catch (WriteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Where are we?","7");
                                }
                            }catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.e("Where are we?","8");
                            }




                    }
                });

        ((Button) rootView.findViewById(R.id.loginBtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mDBApi.getSession().startOAuth2Authentication(getContext());
                    }
                }
        );


        return rootView;
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
    public void onPause() {
        super.onPause();

        //mBeaconManager.removeBeaconListener(this);

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
