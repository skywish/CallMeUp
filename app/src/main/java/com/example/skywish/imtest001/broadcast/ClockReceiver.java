package com.example.skywish.imtest001.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.ui.ClockAlert;

/**
 * Created by DYY-PC on 2015/6/29.
 */
public class ClockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DBClock clock = (DBClock) intent.getSerializableExtra("clock");
        Intent i = new Intent(context, ClockAlert.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("clock", clock);
        context.startActivity(i);
    }
}
