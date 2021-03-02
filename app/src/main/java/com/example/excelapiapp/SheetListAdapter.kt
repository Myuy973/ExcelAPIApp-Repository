package com.example.excelapiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList

class SheetListAdapter(
        private val sheetList: RealmList<String>
): RecyclerView.Adapter<SheetListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sheetButton: Button = view.findViewById<Button>(R.id.sheetNameButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.buttoncard_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.sheetButton.text = sheetList[position]
    }

    override fun getItemCount(): Int = sheetList.size


}