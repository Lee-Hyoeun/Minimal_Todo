package com.todayschedule.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
{
    public static Context mContext;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d"); // 날짜 포맷
    private Frag2 frag2;
    private String mTime;
    private MemoDBHelper dbHelper;
    private StringBuffer mDialogContent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date();
        mTime = mFormat.format(date);

        mDialogContent = new StringBuffer();

        mContext = this;

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId()) {
                    case R.id.action_add:
                        setFrag(1);
                        break;
                }
                return true;
            }
        });

        frag2 = new Frag2();
        setFrag(1); // 첫 프래그먼트 화면을 지정
    }


    // 프레그먼트 교체
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 1:
                ft.replace(R.id.Main_Frame, frag2);
                ft.commit();
                break;
        }
    }


    public void show() {
        mDialogContent.setLength(0);

        dbHelper = MemoDBHelper.getInstance(this);
        String[] params = {mTime};
        Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
        cursor.moveToFirst();


        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.getCount() == 1) {
                mDialogContent.append(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
            }
            else {
                mDialogContent.append(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
                mDialogContent.append(cursor.getCount() - 1);
            }
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Minimal_Todo");
        builder.setContentText(mDialogContent.toString());

        Intent intent = new Intent(this, MainActivity.class);   //intent : 액티비티+데이터 bundle

        PendingIntent pendingIntent = PendingIntent.getActivity(this,  //pending intent : 외부애플리케이션에 권한허가 전달
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pendingIntent);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);

        builder.setAutoCancel(false);
    }
}
