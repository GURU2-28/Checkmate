package com.example.guru_chcekmate

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//SQL문으로 DB작성함

//테이블명 : tb_todo
//6개 칼럼 제시
//프라이머리 키 : _id
//completed 칼럼에는 할 일의 상태 저장(0->미처리, 1->처리)
class DBHelper(context: Context): SQLiteOpenHelper(context, "tododb", null, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
        val memoSql="create table tb_todo (" + "_id integer primary key autoincrement,"+
                "title," +
                "content," +
                "date," +
                "completed)"

        p0?.execSQL(memoSql)
    }



    //데이터 변경시 업데이트하는 메소드
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("drop table tb_todo")
        onCreate(p0)
    }
}