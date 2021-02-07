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

        val date =  Date()
        val sdFormat = SimpleDateFormat("yyyy-MM-dd")
        addDateView.text=sdFormat.format(date)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

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