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
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownServiceException;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.HashMap;
import java.util.Date;

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
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.RowsExceededException;
import java.text.DateFormat;

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



// callback method
        Log.e("onCreate","Before initializing session");
        initialize_session();
         Log.e("onCreate","After initializing session");



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
                        downloadFiles(rootView.findViewById(R.id.readResults));

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
                                Label label2 = new Label(2, 0, "RSSI");
                                Label label3 = new Label(3, 0, "Timestamp");

                                try{
                                    sheet.addCell(label0);
                                    sheet.addCell(label1);
                                    sheet.addCell(label2);
                                    sheet.addCell(label3);
                                }catch(Exception e){
                                    Log.e("Headings","Couldn't add");
                                }


                                for(int i=0;i<beaconList.size();i++){
                                //get SID and temperature of currently selected beacon
                                SBeacon currentBeacon=(SBeacon) beaconList.get(i);
                                String currentSID=currentBeacon.getsId();
                                Float currentTemperature=currentBeacon.getTemperature();
                                String currentRSSI=String.valueOf(currentBeacon.getRssi());
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                Log.e("Get SID and temperature",currentSID+": "+currentTemperature);
                                Log.e("getTemperature()",currentSID+": "+currentTemperature);


                                Label label4 = new Label(0, i+1, currentSID);
                                Number number1 = new Number(1, i+1, currentTemperature);
                                Label label6 = new Label(2, i+1, currentRSSI);
                                Label label7 = new Label(3, i+1, currentDateTimeString);
                                Log.e("Current row",String.valueOf(i+1));

                                Log.e("Where are we?","2");
                                try {
                                    sheet.addCell(label4);
                                    sheet.addCell(number1);
                                    sheet.addCell(label6);
                                    sheet.addCell(label7);
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
                        uploadFiles(rootView.findViewById(R.id.recordReadings));
                    }
                });

        /*((Button) rootView.findViewById(R.id.loginBtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mDBApi.getSession().startOAuth2Authentication(getContext());
                    }
                }
        );*/


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

   /*
     Asynchronous method to upload any file to dropbox*/
    public class Upload extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            Toast.makeText(getContext(), "OnPreExecute", Toast.LENGTH_LONG).show();
            Log.e("OnPreExecute","yes");
        }

    protected String doInBackground(Void... arg0) {

        DropboxAPI.Entry response = null;


            // Define path of file to be upload
            File sdCard = Environment.getExternalStorageDirectory();
            String filePath=sdCard.getAbsolutePath() + "/newfolder/sensorReadings.xls";
            File file = new File(filePath);
            FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);


        } catch (IOException e){

            e.printStackTrace();
            Log.e("Upload IOException","Yes");
        } catch(UnresolvedAddressException e){
            Log.e("UnresolvedException","Yes");
        } catch(Exception e){
            Log.e("Exception","Yes");
        }

        try{
            Log.e("doInBackground","1");

            response = mDBApi.putFileOverwrite("/sensorReadings.xls", inputStream, file.length(), null);
            Log.e("DbExampleLog", "The uploaded file's rev is: " + response.rev);

            //Log.e("After upload", "Here");
            Log.e("doInBackground","2");
            //Log.e("DbExampleLog", "The uploaded file's rev is: " + response.rev);
        }catch(DropboxException e){
            Log.e("DropboxException", "Here");
            e.printStackTrace();
        }


        return response.rev;
        //return null;

    }


    protected void onPostExecute(String result) {

        Log.e("Post execute upload","Here");
        if(result.isEmpty() == false){

        Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

            Log.e("DbExampleLog", "The uploaded file's rev is: " + result);
      }else{
           Log.e("Empty","Yes");
       }
    }



}


/*    *//**//**
     * Callback register method to execute the upload method
     * @param view
     */
    public void uploadFiles(View view){

        new Upload().execute();
    }


/**
     *  Initialize the Session of the Key pair to authenticate with dropbox
     *
     */

    protected void initialize_session(){
        Log.e("initialize_session","1");
        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        Log.e("initialize_session","2");
        //Pass app key pair to the new DropboxAPI object
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        // MyActivity below should be your activity class name
        // start session

        mDBApi.getSession().startOAuth2Authentication(getActivity());

        Log.e("initialize_session","3");

    }



/*
  Asynchronous method to download any file to dropbox*/

    public class Download extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            Toast.makeText(getContext(), "OnPreExecute", Toast.LENGTH_LONG).show();
            Log.e("OnPreExecute","yes");

        }

        protected String doInBackground(Void... arg0) {

            //DropboxAPI.Entry response = null;



                // Define path of file to be download

                File sdCard = Environment.getExternalStorageDirectory();
            String filePath=sdCard.getAbsolutePath() + "/newfolder/power1.xls";
                File file = new File(filePath);
                FileOutputStream outputStream = null;
                // get the file from dropbox
                try {
                    Log.e("Trying download","1");
                    outputStream = new FileOutputStream(file);
                    Log.e("Trying download","2");

                } catch(UnresolvedAddressException e){
                    Log.e("UnresolvedException","Yes");
                } catch(FileNotFoundException e){
                    Log.e("FileNotFoundException","Yes");
                } catch (IOException e){
                    e.printStackTrace();
                    Log.e("Download IOException","Yes");
                }
                catch(Exception e){
                    Log.e("Exception","Yes");
                    e.printStackTrace();
                }

            DropboxAPI.DropboxFileInfo info=null;

            try{
                info = mDBApi.getFile("power1.xls", null, outputStream, null);

                Log.e("Trying download","3");
                //response.rev=info.getMetadata().rev;
              Log.e("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
               // Log.e("DbExampleLog", "The file's rev is: " + response.rev);
            }catch(DropboxException d){
                d.printStackTrace();
                Log.e("Download DropboxExcept","Yes");
            }catch(Exception e) {
                Log.e("Exception for download", "Here");
                e.printStackTrace();
            }
            //Log.e("response.rev",response.rev);
           // return response.rev;
            return info.getMetadata().rev;
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.isEmpty() == false){

                //Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                Log.e("DbExampleLog", "The downloaded file's rev is: " + result);

                File sdCard = Environment.getExternalStorageDirectory();
                String filePath=sdCard.getAbsolutePath() + "/newfolder/power1.xls";
                File file = new File(filePath);
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);


                } catch (IOException e){

                    e.printStackTrace();
                    Log.e("Upload IOException","Yes");
                } catch(UnresolvedAddressException e){
                    Log.e("UnresolvedException","Yes");
                } catch(Exception e){
                    Log.e("Exception","Yes");
                }
                try {
                    Workbook wb = Workbook.getWorkbook(inputStream);

                    Sheet sheet=wb.getSheet(0);
                    int numRows=sheet.getRows();
                    int numCol=sheet.getColumns();

                    //Read SID and read power allocation from file and store
                    String excelContents="";
                    for(int row=0;row<numRows;row++) {
                        Cell cell = sheet.getCell(0, row);
                        String currentSID = cell.getContents();
                        cell = sheet.getCell(1, row);
                        String currentPower = cell.getContents();

                        //Fill in beaconHashMap with beacon SIDs and allocated powers

                        beaconHashMap.put(currentSID, Integer.valueOf(currentPower));
                        Log.e("Current SID and power", String.valueOf(beaconHashMap.get(currentSID)));

                    }
                    beacon.beaconHashMap=beaconHashMap;
                }catch(Exception e){
                    Log.e("Reading downloaded file","Exception");
                }
            }
        }



    }



/*    *//*
*/
/**
     * Callback register method to execute the download method
     * @param view
     */

    public void downloadFiles(View view){

        new Download().execute();
    }






}
