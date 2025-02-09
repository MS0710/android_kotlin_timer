package com.example.android_timer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TimeAdapter(var context: Context, var groups: List<TimeRecord>): BaseAdapter() {

    override fun getCount(): Int {
        return groups.count()
    }

    override fun getItem(position: Int): TimeRecord {
        return groups[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {

        val groupView = LayoutInflater.from(context).inflate(R.layout.time_item, parent, false)

        var txt_timeItem_name: TextView = groupView.findViewById(R.id.txt_timeItem_name)
        var txt_timeItem_time: TextView = groupView.findViewById(R.id.txt_timeItem_time)
        var group = getItem(position)

        txt_timeItem_name.text = group.name
        txt_timeItem_time.text = group.time

        return groupView
    }

}