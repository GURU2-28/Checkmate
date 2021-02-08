package com.example.guru_chcekmate


import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_chcekmate.R
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_add_todo2.*


class AddTodoActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo2)

        //activity_add_todo2 화면에 기본적으로 현재 날짜를 출력함
        val date =  Date()
        val sdFormat = SimpleDateFormat("yyyy-MM-dd")
        addDateView.text=sdFormat.format(date)

        //사용자가 날짜 클릭시 DatePickerDialgo(캘린더)를 출력하여 날짜 선택이 가능토록 함
        addDateView.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dateDialog = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    addDateView.text="$year-${monthOfYear+1}-$dayOfMonth"
                }
            },year,month,day).show()
        }
    }

    //AddTodoActivity2에 메뉴가 출력되게 함
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //사용자가 메뉴를 클릭했을 때 호출되는 메소드
    //사용자가 입력한 데이터를 db에 저장하고 화면을 MainActivity로 되돌림
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId==R.id.menu_add){
            if(addTitleEditView.text.toString() != null && addContentEditView.text.toString() != null){
                val helper = DBHelper(this)
                val db = helper.writableDatabase

                val contentValues=ContentValues()
                contentValues.put("title", addTitleEditView.text.toString())
                contentValues.put("content", addContentEditView.text.toString())
                contentValues.put("date", addDateView.text.toString());
                contentValues.put("completed", 0)

                db.insert("tb_todo", null, contentValues)

                db.close()

                setResult(Activity.RESULT_OK)

                finish()
            }else{
                Toast.makeText(this, "모든 데이터가 입력되지 않습니다.",
                Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}