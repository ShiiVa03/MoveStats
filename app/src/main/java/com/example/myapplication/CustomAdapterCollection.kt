import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemsViewModel
import com.example.myapplication.R


class CustomAdapterCollection(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapterCollection.ViewHolder>() {
    var selectedItem : Int = -1
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_collection, parent, false)

        return ViewHolder(view)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val itemsViewModel = mList[position]
        holder.frameView.setOnClickListener {
            typeClick(position)
            onItemClickListener?.onItemClick(position)
        }


        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(itemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = itemsViewModel.text
        if (position == selectedItem) {
            holder.frameView.setBackgroundResource(R.drawable.round_selected)
            holder.textView.setTextColor(Color.parseColor("#214497"))
        }else{
            holder.frameView.setBackgroundResource(R.drawable.round_gray)
            holder.textView.setTextColor(Color.parseColor("#a3a4a7"))
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val frameView: ConstraintLayout = itemView.findViewById(R.id.linearColl)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewCollection)
        val textView: TextView = itemView.findViewById(R.id.textViewCollection)
    }
    private fun typeClick(selectedPosition: Int) {
        if (selectedItem != selectedPosition) {
            // Update the selected item position and notify the adapter of the changes
            val previousSelectedItem = selectedItem
            selectedItem = selectedPosition
            notifyItemChanged(previousSelectedItem)
            notifyItemChanged(selectedItem)
        }
    }
}
