package com.example.guru_chcekmate;


import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.guru_chcekmate.MainActivity
import com.example.guru_chcekmate.R
import org.w3c.dom.Text
import java.util.*


lateinit var btn_edit:Button
lateinit var btn_exit:Button
lateinit var titleText:TextView


class Title : AppCompatActivity() {

    //타이틀 액티비티
    //exit_popup 타이틀에서 종료시 팝업용
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.GURU_Chcekmate)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        btn_edit=findViewById(R.id.btn_plan)
        btn_exit=findViewById(R.id.btn_exit)
        titleText=findViewById(R.id.titleText)

        val spannableString = SpannableString("체크메이트")
        spannableString.setSpan(RelativeSizeSpan(1.1f),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        titleText.text=spannableString

        btn_edit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btn_exit.setOnClickListener {
            showSettingPopup()
        }


    }

    // 종료버튼 클릭시 팝업창을 띄었을때 팝업창 화면 구성
    private fun showSettingPopup(){
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.exit_popup,null)
        val textView: TextView = view.findViewById(R.id.popuptext)
        val random = Random()
        val num=random.nextInt(5)
        when(num){
            0->textView.text="오늘의 식사는 내일로 미루지 않으면서 오늘 할 일은 내일로 미루는 사람이 많다."
            1->textView.text="<시간>은 '할 일'이 있어야 쓴다"
            2->textView.text="<할 일을 주는 것>이 '시간의 축복'이다."
            3->textView.text="순간 순간의 세월을 헛되이 보내지 마라."
            4->textView.text="당신은 지체할 수도 있지만 시간은 그러하지 않을 것이다"
        }

        val alterDialog = AlertDialog.Builder(this).
        setTitle("종료 알림창").setPositiveButton("종료"){
            dialogInterface: DialogInterface, i: Int ->finish()
        }.setNeutralButton("취소",null).create()

        alterDialog.setView(view)
        alterDialog.show()
    }
}