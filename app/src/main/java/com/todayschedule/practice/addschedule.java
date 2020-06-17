package com.todayschedule.practice;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class addschedule extends AppCompatActivity {
    private TextView mDateText;
    private EditText mTitleText;
    private EditText mContentText;
    private String SeletedDate;
    private int mMemoID;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_search) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_actions, menu) ;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            setTheme(R.style.AppTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.addschedule);

        //mDateText = (TextView) findViewById(R.id.TextDate);
        mTitleText = (EditText) findViewById(R.id.Title_Edit);
        mContentText = (EditText) findViewById(R.id.Cotent_Edit);


        Intent intent = getIntent();
        if(intent != null) {
            SeletedDate = intent.getStringExtra("SelectedDate");
            //mDateText.setText(SeletedDate);

            mMemoID = intent.getIntExtra("id",-1);

            // 받아온 ID가 있으면 테이블 수정으로 간주, 타이틀과 컨텐츠 호출
            if(mMemoID!=-1){
                String title = intent.getStringExtra("title");
                String contents = intent.getStringExtra("contents");

                mTitleText.setText(title);
                mContentText.setText(contents);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = mTitleText.getText().toString();
        String contents = mContentText.getText().toString();

        if(TextUtils.isEmpty(title)) { // 제목이 비어있는지 판단
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
            return;
        }

        // 저장할 데이터를 content Values에 저장
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.DATE,SeletedDate);
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);

        // helper 인스턴스에서 쓰기가능한 데이터베이스를 가져옴
        SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();


        if (mMemoID == -1) {
            long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME, null, contentValues);

            if (newRowId == -1) {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }

            // 받아온 ID값이 있으므로 데이터를 업데이트(수정) 시킨다.
        }
        else {
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME,contentValues,MemoContract.MemoEntry._ID+"="+mMemoID,null);

            if(count==0) {
                Toast.makeText(this,"Failed to modify",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Modified",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }

        super.onBackPressed(); //뒤로 버튼 두번 눌러 종료하기
    }
}
