package com.example.guru_chcekmate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_main.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //list 객체에 db에서 선택한 데이터 저장
    var list: MutableList<ItemVO> = mutableListOf()
    var adapater : MyAdapter? = null
    var touchHelper : ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectDB()

        fab.setOnClickListener{
            val intent = Intent(this, AddTodoActivity2::class.java)
            startActivityForResult(intent, 10)
        }
        touchHelper =
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                    override fun onMove(
                            p0: RecyclerView,
                            p1: RecyclerView.ViewHolder,
                            p2: RecyclerView.ViewHolder
                    ): Boolean {
                        val sourcePosition = p1.adapterPosition
                        val targetPosition = p2.adapterPosition
                        Collections.swap(list,sourcePosition,targetPosition)
                        adapater?.notifyItemMoved(sourcePosition,targetPosition)
                        return true
                    }

                    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

        touchHelper?.attachToRecyclerView(recyclerView)
    }

    //db에서 데이터를 선택하기위한 메소드
    private fun selectDB(){
        list = mutableListOf()
        val helper = DBHelper(this)
        val db = helper.readableDatabase
        val cursor = db.rawQuery("select * from tb_todo order by date desc", null)

        var preDate: Calendar? = null
        while(cursor.moveToNext()){
            val dbdate=cursor.getString(3)
            val date = SimpleDateFormat("yyyy-MM-dd").parse(dbdate)
            val currentDate = GregorianCalendar()
            currentDate.time = date

            if(!currentDate.equals(preDate)){
                val headerItem = HeaderItem(dbdate)
                list.add(headerItem)
                preDate=currentDate
            }

            val completed = cursor.getInt(4) != 0
            val dataitem = DataItem(cursor.getInt(0),cursor.getString(1),
            cursor.getString(2),completed)
            list.add(dataitem)
        }

        // Log.d("Guru", "list size ${list.size}")

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(list,this)
        recyclerView.addItemDecoration(MyDecoration())
    }

    // 리사이클러뷰에 신규 항목 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==10 && resultCode== Activity.RESULT_OK){
            selectDB()
        }
    }

    // 뷰홀더1 : 뷰를 준비함 adapter의 inner class로 옮겼습니다.
    /*class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view){
        val headerView = view.itemHeaderView
    }

    // 뷰홀더2: 데이타 준비함
    class DataViewHolder(view: View): RecyclerView.ViewHolder(view){
        val completedIconView = view.checkTodo
        val itemTitleView = view.itemTitleView
        val itemContentView = view.itemContentView
        val deleteButton = view.deleteButton
        val dehaze = view.dehaze
    }*/

    // 어댑터 설정
    inner class MyAdapter(val list: MutableList<ItemVO>, val activity: MainActivity)
        : RecyclerView.Adapter<MyAdapter.ViewHolder>(){

        //만일 항목타입이 다를 경우
        override fun getItemViewType(position: Int): Int {
            return list.get(position).type
        }

        //getItemViewType에서 결정한 항목타입 전달, 타입이 HEADER쪽인지 DATA쪽인지 판단
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == ItemVO.TYPE_HEADER) {
                // parent?.context : parent가 not null 이면 context가 리턴되고 null 이면 null이 리턴된다.
                val layoutInflater = LayoutInflater.from(parent?.context).inflate(R.layout.item_header, parent, false)
                return ViewHolder(layoutInflater)
            } else {
                val layoutInflater = LayoutInflater.from(parent?.context).inflate(R.layout.item_main, parent, false)
                return ViewHolder(layoutInflater)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val headerView = view.itemHeaderView
            val completedIconView = view.checkTodo
            val itemTitleView = view.itemTitleView
            val itemContentView = view.itemContentView
            val deleteButton = view.deleteButton
            val dehaze = view.dehaze
        }

        //항목 구성(HEADER, DATA)
        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //항목 타입 전달(HEADER, DATA)
            val itemVO = list[position]
            val viewHolder = holder

            if(itemVO.type==ItemVO.TYPE_HEADER){
                val headerItem = itemVO as HeaderItem
                viewHolder.headerView.setText(headerItem.data)
            }else{
                val dataItem = itemVO as DataItem
                viewHolder.itemTitleView.setText(dataItem.title)
                viewHolder.itemContentView.setText(dataItem.content)

                // 삭제 아이콘 클릭시
                viewHolder.deleteButton.setOnClickListener {
                    val helper = DBHelper(this@MainActivity)
                    val db=helper.writableDatabase
                    if(position != RecyclerView.NO_POSITION){
                        db.execSQL("delete from tb_todo WHERE title='"+viewHolder.itemTitleView.text.toString()+"';")
                        list.remove(list.get(position))
                        notifyDataSetChanged()
                    }
                    db.close()
                }
                //드래그 앤 드롭 구현 요소
                viewHolder.dehaze.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        activity.touchHelper?.startDrag(holder)
                    }
                    false
                }

                // 사용자가 체크박스 클릭시
                viewHolder.completedIconView.setOnCheckedChangeListener { buttonView, isChecked ->
                    val helper = DBHelper(this@MainActivity)
                    val db=helper.writableDatabase
                    if(isChecked) {
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(0, dataItem.id))
                        viewHolder.itemTitleView.apply {
                            setTextColor(Color.GRAY)
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            setTypeface(null, Typeface.ITALIC)
                        }
                        viewHolder.itemContentView.apply {
                            setTextColor(Color.GRAY)
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            setTypeface(null, Typeface.ITALIC)
                        }
                    }
                    else{
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(1, dataItem.id))
                        viewHolder.itemTitleView.apply {
                            setTextColor(Color.GRAY)
                            paintFlags=Paint.START_HYPHEN_EDIT_NO_EDIT
                            setTypeface(null, Typeface.BOLD)
                        }
                        viewHolder.itemContentView.apply {
                            setTextColor(Color.GRAY)
                            paintFlags=Paint.START_HYPHEN_EDIT_NO_EDIT
                            setTypeface(null, Typeface.NORMAL)
                        }
                    }

                    /*if(dataItem.completed){
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(0, dataItem.id))
                        viewHolder.completedIconView.setImageResource(R.drawable.icon)

                    }else{
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(1, dataItem.id))
                        viewHolder.completedIconView.setImageResource(R.drawable.icon_completed)
                    }
                    dataItem.completed = dataItem.completed*/
                    db.close()
                }
            }
        }
        override fun getItemCount(): Int {
            return list.size
        }
    }

    inner class MyDecoration(): RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val index = parent!!.getChildAdapterPosition(view)
            Log.d("Guru", "index $index.... list size: ${list.size}")
            val itemVO = list.get(index)
            if(itemVO.type == ItemVO.TYPE_DATA){
                view!!.setBackgroundColor(0xFFFFFFFF.toInt())
                ViewCompat.setElevation(view, 10.0f)
            }
            outRect!!.set(20,10,20,10)
        }
    }
}

abstract class ItemVO{
    abstract val type: Int
    companion object{
        val TYPE_HEADER = 0
        val TYPE_DATA = 1
    }
}

//ItemVO를 상속받아 날짜 데이타를 표현하는 클래스
class HeaderItem(var data: String): ItemVO(){
    override val type: Int
        get() = ItemVO.TYPE_HEADER
}

//ItemVO를 상속받아 todo데이터 표현을 위한 클래스
class DataItem(var id: Int, var title: String, var content: String, var completed: Boolean=false): ItemVO() {
    override val type: Int
        get() = ItemVO.TYPE_DATA
}