package com.bluvision.buvisionsdksample.fragments;

import com.bluvision.beeks.sdk.constants.BeaconType;
import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.domainobjects.ConfigurableBeacon;
import com.bluvision.beeks.sdk.domainobjects.EddystoneURLBeacon;
import com.bluvision.beeks.sdk.domainobjects.IBeacon;
import com.bluvision.beeks.sdk.domainobjects.SBeacon;
import com.bluvision.beeks.sdk.interfaces.BeaconConfigurationListener;
import com.bluvision.beeks.sdk.util.BeaconManager;
import com.bluvision.buvisionsdksample.BluvisionSampleSDKApplication;
import com.bluvision.buvisionsdksample.R;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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


public class BeaconDetail extends BaseFragment implements BeaconConfigurationListener {

    private View rootView;


    private BeaconManager mBeaconManager;
    private Beacon mBeacon;
    private SBeacon sBeacon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mBeaconManager = ((BluvisionSampleSDKApplication) getActivity().getApplication())
                .getBeaconManager();

        if(mBeacon!=null) {

            ((TextView) rootView.findViewById(R.id.txtName)).setText(mBeacon.getDevice().getName());
            ((TextView) rootView.findViewById(R.id.txtMacAddress))
                    .setText(mBeacon.getDevice().getAddress());


            if(mBeacon.getBeaconType()==BeaconType.S_BEACON){
                ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
                sBeacon = (SBeacon)mBeacon;
            }

            ConcurrentHashMap<BeaconType,Beacon> beacons = mBeacon.getAssociations();
            for (Beacon beaconAssociated : beacons.values()){

                if(beaconAssociated.getBeaconType()==BeaconType.S_BEACON){
                    ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
                    sBeacon = (SBeacon)beaconAssociated;
                }

            }


            if(mBeacon.getBeaconType()==BeaconType.I_BEACON){
                Toast.makeText(getActivity(),((IBeacon)mBeacon).getUuid(),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),String.valueOf(((IBeacon)mBeacon).getMajor()),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), String.valueOf(((IBeacon) mBeacon).getMinor()), Toast.LENGTH_LONG).show();
            }


            if(mBeacon.getBeaconType()==BeaconType.EDDYSTONE_URL_BEACON){
                Toast.makeText(getActivity(),((EddystoneURLBeacon)mBeacon).getURL(),Toast.LENGTH_LONG).show();
            }

        }


        ((Button)rootView.findViewById(R.id.btnConnect)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sBeacon!=null){

                            sBeacon.setBeaconConfigurationListener(BeaconDetail.this);

                            String password = ((EditText)rootView.findViewById(R.id.txtPassword)).getText().toString();

                            String desiredSID="A127870322513F6A";
                            String currentSID=sBeacon.getsId();
                            Log.e("SID", currentSID);

                            if(currentSID.equals(desiredSID)){
                                //if password is empty tries to connect without a password
                                sBeacon.connect(getActivity(),password);
                                Log.e("Found?","Yes!");
                            }
                            else{
                                Log.e("Found?","No...");
                            }






                        }

                    }
                });


        ((Button)rootView.findViewById(R.id.btnDisconnect)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sBeacon!=null){
                            sBeacon.alert(true, true);
                            sBeacon.disconnect();


                        }

                    }
                });

        ((Button)rootView.findViewById(R.id.assign)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                try {
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
                        String currentPower=cell.getContents();

                        Log.e("SID",currentSID);
                        Log.e("Power",currentPower);

                        excelContents=excelContents+currentSID+" "+currentPower+"\n";
                    }
                    //show in a textView to make sure app also has access to excel sheet
                    displayExcel(excelContents);
                    //Connect to beacon
                    //Blink beacon
                    //Allocate power

                }catch(Exception e){

                }
                    }
                });

        ((Button)rootView.findViewById(R.id.record)).setOnClickListener(
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

                            //get SID and temperature of currently selected beacon
                            String currentSID=sBeacon.getsId();
                            String currentTemperature=String.valueOf(sBeacon.getTemperature());


                            //What is the difference??
                            Log.e("getTemperature()",String.valueOf(sBeacon.getTemperature()));
                            Log.e("getTemperatureFromScan",String.valueOf(sBeacon.getTemperatureFromScanRecord()));

                            Label label2 = new Label(0, 1, currentSID);
                            Label label3 = new Label(1, 1, currentTemperature);

                            Log.e("Where are we?","2");
                            try {
                                sheet.addCell(label0);
                                sheet.addCell(label1);
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


                            workbook.write();
                            try {
                                workbook.close();
                                Log.e("Where are we?","6");
                            } catch (WriteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.e("Where are we?","7");
                            }
                            //createExcel(excelSheet);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.e("Where are we?","8");
                        }

                    }
                });


        return rootView;
    }




    public void setBeacon(Beacon beacon) {
        mBeacon = beacon;
    }


    @Override
    public void onFailedToUpdateFirmware(int i) {

    }

    @Override
    public void onConnect(boolean connected, boolean authenticated) {


        Log.e("Connect", "connected:" + connected + " authenticated:" + authenticated);

        if(connected && authenticated){

            sBeacon.alert(true, true);
            sBeacon.readStatus();
            sBeacon.readTemperature();
            sBeacon.setIntervalTxPower((byte)2,(byte)3,(float)0.5,(float)1);
            Log.e("LOOK! battery",String.valueOf(sBeacon.getBattery()));
            Log.e("LOOK! temperature",String.valueOf(sBeacon.getTemperatureFromScanRecord()));
            Log.e("LOOK! SID",sBeacon.getsId());
            Log.e("LOOK! contents", String.valueOf(sBeacon.describeContents()));
            Log.e("LOOK! device type", String.valueOf(sBeacon.getDeviceType()));

            ConfigurableBeacon configurableBeacon = (ConfigurableBeacon)sBeacon;

            configurableBeacon.readIBeaconUUID();

                    ((Button) rootView.findViewById(R.id.btnConnect)).setEnabled(false);
            ((Button)rootView.findViewById(R.id.btnDisconnect)).setEnabled(true);
            Toast.makeText(getActivity(),"Connected", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getActivity(),"Connection failed", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDisconnect() {

        ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
        ((Button)rootView.findViewById(R.id.btnDisconnect)).setEnabled(false);
        Log.e("Connect", "Disconnected");
        Toast.makeText(getActivity(),"Disconnected", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCommandToNotConnectedBeacon() {

    }

    @Override
    public void onReadConnectionSettings(int i, int i1, int i2, int i3) {

    }

    @Override
    public void onSetConnectionSettings(int i, int i1, int i2, int i3) {

    }

    @Override
    public void onFailedToReadConnectionSettings() {

    }

    @Override
    public void onFailedToSetConnectionSettings() {

    }

    @Override
    public void onReadTemperature(double v) {

    }

    @Override
    public void onFailedToReadTemperature() {

    }

    @Override
    public void onConnectionExist() {

    }

    @Override
    public void onReadIBeaconUUID(UUID uuid) {

        Toast.makeText(getActivity(),"UUID: " + uuid.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSetIBeaconUUID(UUID uuid) {

    }

    @Override
    public void onFailedToReadIBeaconUUID() {

    }

    @Override
    public void onFailedToSetIBeaconUUID() {

    }

    @Override
    public void onReadIBeaconMajorAndMinor(int i, int i1) {

    }

    @Override
    public void onSetIBeaconMajorAndMinor(int i, int i1) {

    }

    @Override
    public void onFailedToReadIBeaconMajorAndMinor() {

    }

    @Override
    public void onFailedToSetIBeaconMajorAndMinor() {

    }

    @Override
    public void onReadEddystoneUID(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void onSetEddystoneUID(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void onFailedToReadEddystoneUID() {

    }

    @Override
    public void onFailedToSetEddystoneUID() {

    }

    @Override
    public void onReadEddystoneURL(String s) {

    }

    @Override
    public void onSetEddystoneURL(String s) {

    }

    @Override
    public void onFailedToReadEddystoneURL() {

    }

    @Override
    public void onFailedToSetEddystoneURL() {

    }

    @Override
    public void onReadDeviceStatus(float v, float v1, short i) {

    }

    @Override
    public void onFailedToReadDeviceStatus() {

    }

    @Override
    public void onReadFrameTypeIntervalTxPower(byte b, byte b1, byte b2, float v, float v1) {

    }

    @Override
    public void onSetFrameTypeIntervalTxPower(byte b, byte b1, byte b2, float v, float v1) {

    }

    @Override
    public void onFailedToReadFrameTypeIntervalTxPower() {

    }

    @Override
    public void onFailedToSetFrameTypeIntervalTxPower() {

    }

    @Override
    public void onSetFrameTypeConnectionRates(byte b, byte b1, byte b2) {

    }

    @Override
    public void onReadFrameTypeConnectionRates(byte b, byte b1, byte b2) {

    }

    @Override
    public void onFailedToReadFrameTypeConnectionRates() {

    }

    @Override
    public void onFailedToSetFrameTypeConnectionRates() {

    }

    @Override
    public void onReadAdvertisementSettings(float v, float v1, float v2) {

    }

    @Override
    public void onSetAdvertisementSettings(float v, float v1, float v2) {

    }

    @Override
    public void onFailedToReadAdvertisementSettings() {

    }

    @Override
    public void onFailedToSetAdvertisementSettings() {

    }

    @Override
    public void onSetAccelerometerConfiguration() {

    }

    @Override
    public void onFailedToSetAccelerometerConfiguration() {

    }

    @Override
    public void onSetPassword(boolean b) {

    }

    @Override
    public void onUpdateFirmware(double v) {

    }

    public void displayExcel(String text){
        TextView excelContents=(TextView) rootView.findViewById(R.id.excelFileContents);
        excelContents.setText(text);
    }
}
