package com.batuhansubasi.akilliharita;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class BilgiEkrani implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public BilgiEkrani(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.marker_bilgi_ekrani, null);
        TextView adSoyad = view.findViewById(R.id.adSoyad);
        TextView marka = view.findViewById(R.id.marka);
        TextView model = view.findViewById(R.id.model);
        TextView plaka = view.findViewById(R.id.plaka);
        TextView puan = view.findViewById(R.id.puan);

        BilgiEkraniTaslagi infoWindowData = (BilgiEkraniTaslagi) marker.getTag();

        adSoyad.setText(infoWindowData.getAdSoyad());
        marka.setText(infoWindowData.getMarka());
        model.setText(infoWindowData.getModel());
        plaka.setText(infoWindowData.getPlaka());
        puan.setText(Double.toString(infoWindowData.getPuan()));

        return view;
    }
}
