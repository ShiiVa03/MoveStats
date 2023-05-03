package com.example.myapplication

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.RecyclerView

data class ItemViewModel(
    val name: String,
    val time: String

)


class CustomAdapterLeaderboard(private val mList: List<ItemViewModel>) : RecyclerView.Adapter<CustomAdapterLeaderboard.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_leaderboard, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.numberText.text = (position + 1).toString()
        holder.textViewPlayer.text = itemsViewModel.name
        holder.textViewTime.text = itemsViewModel.time + "\nseconds"


        if (position == 0) {
            holder.frameView.setBackgroundColor(Color.parseColor("#3674ff"))
        }else if(position == 1){
            holder.frameView.setBackgroundColor(Color.parseColor("#295ac8"))
        }else if(position ==2 ){
            holder.frameView.setBackgroundColor(Color.parseColor("#2e3192"))
        }else{
            holder.frameView.setBackgroundColor(Color.parseColor("#000000"))
            holder.numberText.setTextColor(Color.parseColor("#a09ea6"))
            holder.textViewPlayer.setTextColor(Color.parseColor("#a09ea6"))
            holder.textViewTime.setTextColor(Color.parseColor("#a09ea6"))
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val frameView: ConstraintLayout = itemView.findViewById(R.id.linearCoLead)
        val numberText: TextView = itemView.findViewById(R.id.numbertext)
        val textViewPlayer: TextView = itemView.findViewById(R.id.textViewplayer)
        val textViewTime : TextView = itemView.findViewById(R.id.textView19)
    }

}