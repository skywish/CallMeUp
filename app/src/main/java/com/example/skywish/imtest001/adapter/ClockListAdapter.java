package com.example.skywish.imtest001.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.broadcast.ClockReceiver;
import com.example.skywish.imtest001.ui.ClockAlert;
import com.leaking.slideswitch.SlideSwitch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by DYY-PC on 2015/7/3.
 */
public class ClockListAdapter extends BaseAdapter{
    Context context;
    ArrayList<DBClock> list;
    public ClockListAdapter(Context context, ArrayList<DBClock> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DBClock getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.clock_list_item, null);
            holder = new ViewHolder();
            holder.labelView = (TextView) convertView.findViewById(R.id.labelView);
            holder.sexImage = (ImageView) convertView.findViewById(R.id.sexImage);
            holder.timeView = (TextView) convertView.findViewById(R.id.timeView);
            holder.slideSwitch = (SlideSwitch) convertView.findViewById(R.id.switchBtn);
            final DBClock clock = getItem(position);
            if (clock.getIsOpen()) {
                holder.slideSwitch.setState(true);
            } else {
                holder.slideSwitch.setState(false);
            }
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        final DBClock clock = getItem(position);
        holder.labelView.setText("起床闹钟");
        final String minute, hour;
        if(clock.getMinute()<10){
            minute = "0" + String.valueOf(clock.getMinute());
        }else {
            minute = String.valueOf(clock.getMinute());
        }
        if(clock.getHour()<10){
            hour = "0"+String.valueOf(clock.getHour());
        }else {
            hour = String.valueOf(clock.getHour());
        }
        holder.timeView.setText(hour+":"+minute);

        //根据选项显示不同图标
        if (clock.getIsSystem()) {
            holder.sexImage.setImageResource(R.drawable.ic_system);
        } else {
            if (clock.getIsGirl()) {
                holder.sexImage.setImageResource(R.drawable.ic_female);
            } else {
                holder.sexImage.setImageResource(R.drawable.ic_male);
            }
        }
        holder.slideSwitch.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                long systemTime = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long selectTime = calendar.getTimeInMillis();
                if (systemTime > selectTime) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    selectTime = calendar.getTimeInMillis();
                }
                Long time = selectTime - systemTime;
                int hour = time.intValue() / 3600000;
                int minute = (time.intValue() % 3600000) / 60000 +1;
                if (!clock.getIsOpen()) {
                    if (clock.getIsSystem()) {
                        if (minute < 1) {
                            Toast.makeText(context, "还有不到1分钟,闹铃将会响起", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "还有" + hour + "小时" + minute + "分钟,闹铃将会响起",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (clock.getIsGirl()) {
                            if (minute < 1) {
                                Toast.makeText(context, "还有不到1分钟,将会有个神秘的她叫醒你",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "还有" + String.valueOf(hour) + "小时" +
                                        minute + "分钟，将会有个神秘的她叫醒你", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (minute < 1) {
                                Toast.makeText(context, "还有不到1分钟,将会有个神秘的她叫醒你",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "还有" + String.valueOf(hour) + "小时" +
                                        minute + "分钟，将会有个神秘的他叫醒你", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                clock.setIsOpen(true);
                Intent intent = new Intent(context, ClockAlert.class);
                intent.putExtra("clock", clock);
                PendingIntent sender = PendingIntent.getBroadcast(context, clock.getClockID().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, 0, sender);
                updateObject(clock);
//        switch (repeatTime) {
//            case 0:
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectTime, DAY, sender);
//                break;
//            case 1:
//                alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, sender);
//                break;
//            default:
//                alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, sender);
//                break;
//        }
            }

            @Override
            public void close() {
                Intent intent = new Intent(context, ClockReceiver.class);
                DBClock clock = getItem(position);
                PendingIntent sender = PendingIntent.getBroadcast(context, clock.getClockID().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager;
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(sender);
                clock.setIsOpen(false);
                updateObject(clock);
            }
        });
        return convertView;
    }
    private void updateObject(DBClock clock) {
        clock.update(context, clock.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "修改失败：" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class ViewHolder {
        SlideSwitch slideSwitch;
        TextView timeView;
        TextView labelView;
        ImageView sexImage;
    }
}
