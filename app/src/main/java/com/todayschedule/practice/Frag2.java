package com.todayschedule.practice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Frag2 extends Fragment {
    public static final int REQUEST_CODE_INSERT = 1000;
    private View view;
    private FloatingActionButton fab;
    private CalendarView mCalendarView;
    private TextView mTextDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d"); // 날짜 포맷
    private String mTime;
    private RecyclerView recyclerView;
    private TextAdapter textAdapter;
    private MemoDBHelper dbHelper;
    private ArrayList<String> list;
    private TextView textview;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag2, container, false);
        list = new ArrayList<>();

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mTextDate = (TextView) view.findViewById(R.id.whenDate);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler1);
        textview=(TextView) view.findViewById(R.id.scheduleText);

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar(); //액션바
        actionBar.setTitle("Minimal_ToDo"); //액션바 타이틀 지정


        Date date = new Date();
        mTime = mFormat.format(date);
        mTextDate.setText(mTime); // 현재 날짜로 설정 //캘린더 일정창 날짜(2020/mm/dd)

        fab.setOnClickListener(new View.OnClickListener() // 일정 추가 버튼 클릭시
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), addschedule.class); // 일정 추가 액티비티 생성
                intent.putExtra("SelectedDate", mTime);
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });


        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                mTime = year + "/" + (month + 1) + "/" + dayOfMonth;
                getMemoCursor(); // 선택한 날짜의 메모 가져옴
                mTextDate.setText(mTime); // 선택한 날짜로 설정
            }
        });

        // dbHelper 인스턴스 저장
        dbHelper = MemoDBHelper.getInstance(getActivity());

        // 리사이클러뷰 LinearLayoutManager 객체 지정
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터 객체 생성
        textAdapter = new TextAdapter(list);

        // DB 에서 list 값저장
        getMemoCursor();

        // recyclerView 어댑터 객체 지정
        recyclerView.setAdapter(textAdapter);

        // 메모 클릭 이벤트(수정)
        textAdapter.setOnItemClickListener(new TextAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View v, int pos)
            {
                Intent intent = new Intent(getActivity(), addschedule.class);
                intent.putExtra("SelectedDate", mTime);

                String[] params = {mTime};
                Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
                cursor.moveToPosition(pos);

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);

                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        // 일정 삭제(롱클릭)
        textAdapter.setOnItemLongClickListener(new TextAdapter.OnItemLongClickListener()
        {
            @Override
            public void onItemLongClick(View v, int pos)
            {

                String[] params = {mTime};
                Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
                cursor.moveToPosition(pos);
                final int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));

                builder.setTitle("Schedule delete");
                builder.setMessage("Wanna delete?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME,MemoContract.MemoEntry._ID+"="+id,null);

                        if(deletedCount==0) {
                            Toast.makeText(getActivity(), "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            getMemoCursor();
                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.show();
            }
        });

        return view;
    }

    // 커서의 데이터를 Arraylist에 저장하는 메서드
    private void getMemoCursor() {
        String[] params = {mTime};
        list.clear();

        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);

        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }

        // 리스트가 없으면 일정없음 텍스트 표시
        if(list.size()>0) {
            textview.setVisibility(view.GONE);
        }
        else {
            textview.setVisibility(view.VISIBLE);
        }

        textAdapter.notifyDataSetChanged();
        ((MainActivity)MainActivity.mContext).show();

    }

    // 일정 추가후 갱신
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INSERT) {
            getMemoCursor();
        }
    }
}


