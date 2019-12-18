package com.batuhansubasi.akilliharita;

import android.util.Log;
import android.widget.Toast;

public class BilgiEkraniTaslagi {
    private String adSoyad, marka, model, plaka;
    private double puan;
    public String getAdSoyad() {
        return this.adSoyad;
    }
    public String getMarka() {
        return this.marka;
    }
    public String getModel() {
        return this.model;
    }
    public String getPlaka() {
        return this.plaka;
    }
    public double getPuan() {
        return this.puan;
    }
    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }
    public void setMarka(String marka) {
        this.marka = marka;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }
    public void setPuan(double puan) {
        this.puan = puan;
    }
}
