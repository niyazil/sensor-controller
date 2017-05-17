package com.bluvision.buvisionsdksample.adapters;

import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.domainobjects.SBeacon;
import com.bluvision.buvisionsdksample.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class BeaconsListAdapter extends BaseAdapter {

    private List<Beacon> listaData;
    private Context context;
    private static LayoutInflater inflater = null;

    public BeaconsListAdapter(Context context, List<Beacon> listaData) {
        this.context = context;
        this.listaData = listaData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listaData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_beacon, null);
        }

        Holder holder = new Holder();

        holder.tvName = (TextView) convertView.findViewById(R.id.txtName);
        holder.txtTemp = (TextView) convertView.findViewById(R.id.txtTemp);
        holder.txtRSSI = (TextView) convertView.findViewById(R.id.txtRSSI);

        SBeacon beacon = (SBeacon) listaData.get(position);

        if(beacon==null){
            return null;
        }

        String name = "Unknown";

        if(beacon!=null && beacon.getDevice().getName()!=null && !beacon.getDevice().getName().isEmpty()){
            name = beacon.getDevice().getName();
        }


        holder.tvName.setText(beacon.getsId());
        holder.txtTemp.setText( String.valueOf(beacon.getTemperature())+" °C");
        holder.txtRSSI.setText( String.valueOf(beacon.getRssi())+ " dBm");

        return convertView;
    }


    public class Holder {

        TextView tvName;
        TextView txtTemp;
        TextView txtRSSI;
    }

}