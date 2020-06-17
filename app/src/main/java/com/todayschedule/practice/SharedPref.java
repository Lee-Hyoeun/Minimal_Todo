package com.todayschedule.practice;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences : DB에 저장과 별도의 파일형태로 xml 프로세스내에 data 일시저장,
 * */
public class SharedPref {
    private SharedPreferences mySharedPref;
    private Context context;

    //context를 통하여 sharedpref생성, 데이터 저장,공유
    public SharedPref(Context context){
        mySharedPref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

}