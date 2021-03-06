package com.bluvision.buvisionsdksample.Extra;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.domainobjects.ConfigurableBeacon;
import com.bluvision.beeks.sdk.domainobjects.SBeacon;
import com.bluvision.beeks.sdk.interfaces.BeaconConfigurationListener;
import com.bluvision.buvisionsdksample.R;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


// This class is a reduced version of BeaconDetails. We need the BeaconCofigurationListener in order to connect to beacons,
//but we don't need the extra fragment methods in BeaconDetails. An instance of this class has an SBeacon as a an attribute.

public class BeaconDetailReduced extends Activity implements BeaconConfigurationListener {
public SBeacon sBeacon;
public int beaconListIndex=0;
public boolean globalAuth=false;
public List<Beacon> beaconList;
public HashMap<String,Integer> beaconHashMap;


    public BeaconDetailReduced() {

    }

    //For starting the circle of life
    public void beaconBirth(){
        sBeacon=(SBeacon) beaconList.get(beaconListIndex);
        Log.e("Beacon SID",sBeacon.getsId());
        sBeacon.setBeaconConfigurationListener(this); //sets the BeaconConfigurationListener to the BeaconDetailReduced object for which this method is called
        sBeacon.connect(getParent(),null);

    }



    @Override
    public void onConnect(boolean connected, boolean authenticated) {
        Log.e("Connect", "connected:" + connected + " authenticated:" + authenticated);

        if(connected && authenticated){
            globalAuth=true;
            sBeacon.alert(true, true);
            Log.e("Beacon", "Lights?");

            //get SID and corresponding power from hashmap
            String key = sBeacon.getsId();
            int power = beaconHashMap.get(key);
            sBeacon.setIntervalTxPower((byte) -5, (byte) power, (float) 1, (float) 1);
            Log.e("Beacon", "Power");

        }else{
            globalAuth=false;
            Log.e("Couldn't connect","Boohoo");
            //sBeacon.connect(getParent(),null);
        }

    }

    @Override
    public void onSetFrameTypeIntervalTxPower(byte b, byte b1, byte b2, float v, float v1) {
        Log.e("Setting power/interval","Success");
        sBeacon.disconnect();

    }

    @Override //I think it will already have disconnected at this point
    public void onDisconnect() {
        //if authorized you are allowed to disconnect otherwise you have to keep trying to connect!
        if(globalAuth) {
            Log.e("Beacon", "Disconnect");
            if (beaconListIndex < (beaconList.size() - 1)) {
                beaconListIndex++;
                Log.e("onDisconnect", "Incrementing list index");
                //SystemClock.sleep(5000);
                beaconBirth();
            } else {
                Log.e("onDisconnect", "Done");
                beaconListIndex = 0;
            }
        }else{
            Log.e("Unauthorized","Yes");
            sBeacon.connect(getParent(),null);
        }


    }

    @Override
    public void onCommandToNotConnectedBeacon() {
        Log.e("NotConnectedBeacon","The beacon is not connected!");




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
        Log.e("FailedToSet","Connection settings");

    }

    @Override
    public void onReadTemperature(double v) {

    }

    @Override
    public void onFailedToReadTemperature() {
        Log.e("Reading temperature","Failed");

    }

    @Override
    public void onConnectionExist() {
        Log.e("Multiple beacons","here");

    }

    @Override
    public void onReadIBeaconUUID(UUID uuid) {


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
        Log.e("Reading power/interval","Success");

    }



    @Override
    public void onFailedToReadFrameTypeIntervalTxPower() {

    }

    @Override
    public void onFailedToSetFrameTypeIntervalTxPower() {
        Log.e("Failed to set","FrameTypeIntervalTxPower");

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
        Log.e("Failed to set","FrameTypeConnectionRates");

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
        Log.e("Failed to set","AdvertisementSettings");

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
    @Override
    public void onFailedToUpdateFirmware(int i) {

    }



}
