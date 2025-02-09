package com.example.android_timer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):
    SQLiteOpenHelper(context, name, null, version) {

    companion object {
        val name = "mdatabase.db"
        val version = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        //TODO("Not yet implemented")
        val sql = "CREATE TABLE if not exists myTABLE(_id integer primary key autoincrement, "+
                "itemName text NOT null," +
                "time text NOT NULL)"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldversion: Int, newVersion: Int) {
        //TODO("Not yet implemented")
        db.execSQL("DROP TABLE IF EXISTS myTABLE")
        onCreate(db)
    }
}