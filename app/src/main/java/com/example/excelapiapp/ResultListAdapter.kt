package com.example.excelapiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultListAdapter(
        private val resultList: List<List<String>>
): RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val quizIndex = view.findViewById<TextView>(R.id.questionIndex)
        val resultImage = view.findViewById<ImageView>(R.id.imageView)
        val questionText = view.findViewById<TextView>(R.id.questionText)
        val choiceText = view.findViewById<TextView>(R.id.selectChoiceText)
        val answerText = view.findViewById<TextView>(R.id.answerText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.resultcard_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultListAdapter.ViewHolder, position: Int) {
        holder.quizIndex.text = "${position + 1}問目"
        holder.questionText.text = resultList[position][0]
        holder.choiceText.text = resultList[position][2]
        holder.answerText.text = resultList[position][1]
        if (resultList[position][2] == resultList[position][1]) {
            holder.resultImage.setImageResource(R.drawable.trueimage)
        } else {
            holder.resultImage.setImageResource(R.drawable.falseimage)
        }
    }

    override fun getItemCount(): Int = resultList.size


}