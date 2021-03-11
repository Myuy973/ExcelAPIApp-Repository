package com.example.excelapiapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList

class SheetListAdapter(
        private val sheetList: List<String>
): RecyclerView.Adapter<SheetListAdapter.ViewHolder>() {

    private var listener: ((Int?) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int?) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sheetButton: Button = view.findViewById<Button>(R.id.sheetNameButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.buttoncard_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.d("value", "sheetList: ${sheetList[position]}")
        val contentList = sheetList[position].split(",")
        val sheetName = contentList[0]
        val sheetNum = contentList[1]
        holder.sheetButton.text = "$sheetName [$sheetNum]"
        holder.sheetButton.setOnClickListener {
            listener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = sheetList.size

}