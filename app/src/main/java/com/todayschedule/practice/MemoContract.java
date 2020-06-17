package com.todayschedule.practice;

import android.provider.BaseColumns;

/**
 * DB 스키마를 정의하는 클래스
 *
 * */
public class MemoContract
{
    /**생성자의 접근지정자를 private으로해서 초기화방지(인스턴스화 방지)  */
    private  MemoContract(){}

    /** MemoContract 내부 클래스에 BaseColumns 인터페이스를 통해 스키마를 정의*/
    public static class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME="memo"; //테이블이름
        public static final String DATE="date";       //테이블날짜
        public static final String COLUMN_NAME_TITLE="title";   //column 정의
        public static final String COLUMN_NAME_CONTENTS="contents";
    }
}
