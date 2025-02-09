package com.example.android_timer

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale
import java.util.logging.Handler

class MainActivity : AppCompatActivity() {

    private lateinit var txt_time: TextView
    private lateinit var btn_start: Button
    private lateinit var img_light: ImageView
    private lateinit var spinner: Spinner
    private lateinit var imgbtn_save: ImageButton
    private lateinit var imgbtn_search: ImageButton
    private lateinit var imgbtn_clean: ImageButton
    private val TAG = "MainActivity"
    private var time_flag: Int = 0
    private var time: String = "00:00:00"
    private var nanoSeconds: Int = 0
    private var isRunning = false
    private var block_item: String = "2 x 2"
    private lateinit var db : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initView(){
        db = DBHelper(this).writableDatabase

        txt_time = findViewById(R.id.txt_time)
        btn_start = findViewById(R.id.btn_start)
        img_light = findViewById(R.id.img_light)
        spinner = findViewById(R.id.spinner)
        imgbtn_save = findViewById(R.id.imgbtn_save)
        imgbtn_search = findViewById(R.id.imgbtn_search)
        imgbtn_clean = findViewById(R.id.imgbtn_clean)
        txt_time.text = time

        val adapter = ArrayAdapter.createFromResource(this,R.array.block,R.layout.spinner_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                block_item = p0?.getItemAtPosition(p2).toString()
                Log.d(TAG, "onItemSelected: $block_item");
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        btn_start.setOnClickListener {
            Log.d(TAG, "initView: setOnClickListener")
            stopwatchSwitch()
            img_light.setImageResource(R.drawable.light_red)
        }

        btn_start.setOnLongClickListener{
            Log.d(TAG, "initView: setOnLongClickListener")
            time_flag = 1
            img_light.setImageResource(R.drawable.light_green)
            true
        }

        btn_start.setOnTouchListener { view, event ->
            when(event.action){
                MotionEvent.ACTION_UP ->{
                    Log.d(TAG, "initView: ACTION_UP")
                    stopwatchSwitch()
                }
            }
            false
        }

        imgbtn_save.setOnClickListener {
            Log.d(TAG, "initView: imgbtn_save")
            AlertDialog.Builder(this)
                .setTitle("紀錄時間")
                .setMessage("是否要存檔紀錄時間\n"+"Item: "+block_item+"\nTime: "+time)
                .setPositiveButton("確定"){_, _ ->
                    Log.d(TAG, "initView: imgbtn_save OK")
                    Log.d(TAG, "initView: block_item = $block_item;time = $time")
                    db.execSQL("INSERT INTO myTable(itemName,time) VALUES('${block_item}', '${time}')")
                }
                .setNegativeButton("取消"){_, _ ->
                    Log.d(TAG, "initView: imgbtn_save cancel")
                }
                .show()
        }

        imgbtn_search.setOnClickListener {
            Log.d(TAG, "initView: imgbtn_search")
            val intent = Intent(this, TimeListActivity::class.java)
            startActivity(intent)
        }

        imgbtn_clean.setOnClickListener {
            Log.d(TAG, "initView: Time Clean")
            AlertDialog.Builder(this)
                .setTitle("清除時間")
                .setMessage("是否要清除時間")
                .setPositiveButton("確定"){_, _ ->
                    Log.d(TAG, "initView: imgbtn_clean OK")
                    time = "00:00:00"
                    txt_time.text = time
                }
                .setNegativeButton("取消"){_, _ ->
                    Log.d(TAG, "initView: imgbtn_clean cancel")
                }
                .show()
        }
    }

    private var taskHandler = android.os.Handler()
    private val counter = object : Runnable {
        override fun run() {
            val min = nanoSeconds / 3600
            val sec = nanoSeconds % 3600 / 60
            val nano = nanoSeconds % 60

            time = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d", min,
                sec, nano)
            txt_time.text = time
            nanoSeconds++
            taskHandler.postDelayed(this, 10)
        }
    }

    fun stopwatchSwitch(){
        if (time_flag == 1){
            if (!isRunning) {
                taskHandler.post(counter)
                isRunning = true
                time_flag = 2
                btn_start.text = "Stop"
            }
        }else if (time_flag == 2){
            if (isRunning){
                taskHandler.removeCallbacks(counter)
                isRunning = false
                nanoSeconds = 0
                time_flag = 0
                btn_start.text = "Start"
            }
        }
    }

    fun show(){
        val cursor = db.rawQuery("SELECT * FROM myTABLE", null)
        cursor.moveToFirst()
        Log.d(TAG, "show: cunt = "+cursor.count)

        for (i in 0 until cursor.count) {
            Log.d(TAG, "show: id = "+cursor.getString(0))
            Log.d(TAG, "show: name = "+cursor.getString(1))
            Log.d(TAG, "show: time = "+cursor.getString(2))
            cursor.moveToNext()
            Log.d(TAG, "show:----------- ")
        }
        cursor.close()

    }
}