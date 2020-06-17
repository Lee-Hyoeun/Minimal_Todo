package com.todayschedule.practice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
/**
 * SQLiteOpenHelper를 상속받아 Memo DB남김
 * DB는 SQLite 테이블형식으로 구조로 DATA 저장
 * */
public class MemoDBHelper extends SQLiteOpenHelper {
    private static MemoDBHelper sInstance; // 싱글턴 인스턴스로 선언(싱글턴: 인스턴스를 하나만 만들어 사용하기위한 패턴)

    private static final int DB_VERSION=1;
    private static final String  DB_NAME="Memo.db";
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT ,%s CHAR[10], %s TEXT, %s TEXT)",
                    MemoContract.MemoEntry.TABLE_NAME,
                    MemoContract.MemoEntry._ID,
                    MemoContract.MemoEntry.DATE,
                    MemoContract.MemoEntry.COLUMN_NAME_TITLE,
                    MemoContract.MemoEntry.COLUMN_NAME_CONTENTS);

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MemoContract.MemoEntry.TABLE_NAME;

    public static MemoDBHelper getInstance(Context context) {
        if(sInstance==null){ // 인스턴스가 없으면 생성
            sInstance=new MemoDBHelper(context); }
        return sInstance;
    }

    private MemoDBHelper(@Nullable Context context){ // private로 하여 외부생성 X
        super(context, DB_NAME, null, DB_VERSION);
    }
    //create DB생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES); }

    //Upgrade DB버전 업데이트, oldversion을 지우고 새버전의 테이블을 생성
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db); }

    public void DropTable(SQLiteDatabase db) {
        db.delete(MemoContract.MemoEntry.TABLE_NAME, null, null);
    }
}


