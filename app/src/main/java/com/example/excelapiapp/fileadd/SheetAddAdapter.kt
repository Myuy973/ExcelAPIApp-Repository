package com.example.excelapiapp.fileadd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excelapiapp.R
import com.example.excelapiapp.fileadd.FileSheetCreateFragment
import androidx.fragment.app.Fragment


class SheetAddAdapter(
    private var sheetNameList: List<String>
): RecyclerView.Adapter<SheetAddAdapter.ViewHolder>() {

    var listener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sheetName: TextView = view.findViewById<TextView>(R.id.sheetName)
        val removeButton: Button = view.findViewById<Button>(R.id.sheetRemoveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetAddAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sheetcard_layout, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SheetAddAdapter.ViewHolder, position: Int) {
        holder.sheetName.text = sheetNameList[position]
        holder.removeButton.setOnClickListener {
//             = sheetNameList.drop(position)
            listener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = sheetNameList.size
}