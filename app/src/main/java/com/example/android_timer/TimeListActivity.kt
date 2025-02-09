package com.example.android_timer

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TimeListActivity : AppCompatActivity() {
    private val TAG = "TimeListActivity"

    private lateinit var lv_timeRecord: ListView
    lateinit var timerAdapter: TimeAdapter
    private val timeRecordList = ArrayList<TimeRecord>()
    private lateinit var db : SQLiteDatabase
    private lateinit var spinner_timelist: Spinner
    private var block_item: String = "2 x 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_list)
        initView()
    }

    fun initView(){
        db = DBHelper(this).writableDatabase

        spinner_timelist = findViewById(R.id.spinner_timelist)
        lv_timeRecord = findViewById(R.id.lv_timeRecord)

        val spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.block,R.layout.spinner_item)
        spinner_timelist.adapter = spinnerAdapter

        timerAdapter = TimeAdapter(this, timeRecordList)
        lv_timeRecord.adapter = timerAdapter

        spinner_timelist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                block_item = p0?.getItemAtPosition(p2).toString()
                Log.d(TAG, "onItemSelected: $block_item");
                queryItem()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        lv_timeRecord.setOnItemLongClickListener { adapterView, view, position, l ->
            Log.d(TAG, "initView: Item = $position")
            var _id= timeRecordList.get(position)._id
            Log.d(TAG, "initView: Item _id = $_id")
            AlertDialog.Builder(this)
                .setTitle("刪除紀錄")
                .setMessage("是否要刪除紀錄時間\n"+"Item: "+timeRecordList.get(position).name+"\n"+
                        "Time: "+timeRecordList.get(position).time)
                .setPositiveButton("確定"){_, _ ->
                    Log.d(TAG, "initView: lv_timeRecord OK")
                    removeItem(_id)
                    queryItem()
                }
                .setNegativeButton("取消"){_, _ ->
                    Log.d(TAG, "initView: lv_timeRecord cancel")
                }
                .show()
            return@setOnItemLongClickListener true
        }

    }

    fun removeItem(_id: Int){
        Log.d(TAG, "removeItem: Item _id = $_id")
        db.execSQL("DELETE FROM myTable WHERE _id = '${_id}'")
    }

    fun queryItem(){
        val cursor = db.rawQuery("SELECT * FROM myTABLE WHERE itemName Like '${block_item}'", null)
        cursor.moveToFirst()
        timeRecordList.clear()
        Log.d(TAG, "queryItem: cunt = "+cursor.count)
        for (i in 0 until cursor.count){
            Log.d(TAG, "queryItem: id = "+cursor.getString(0))
            Log.d(TAG, "queryItem: name = "+cursor.getString(1))
            Log.d(TAG, "queryItem: time = "+cursor.getString(2))
            timeRecordList.add(TimeRecord(cursor.getInt(0),cursor.getString(1), cursor.getString(2)))
            cursor.moveToNext()
            Log.d(TAG, "queryItem:----------- ")
        }
        cursor.close()
        timerAdapter.notifyDataSetChanged()
    }

    fun show(){
        val cursor = db.rawQuery("SELECT * FROM myTABLE", null)
        cursor.moveToFirst()
        Log.d(TAG, "show: cunt = "+cursor.count)
        timeRecordList.clear()

        for (i in 0 until cursor.count) {
            Log.d(TAG, "show: id = "+cursor.getString(0))
            Log.d(TAG, "show: name = "+cursor.getString(1))
            Log.d(TAG, "show: time = "+cursor.getString(2))
            timeRecordList.add(TimeRecord(cursor.getInt(0),cursor.getString(1), cursor.getString(2)))
            cursor.moveToNext()
            Log.d(TAG, "show:----------- ")
        }
        cursor.close()
    }
}