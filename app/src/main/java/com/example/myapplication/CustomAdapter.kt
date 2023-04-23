import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemsViewModel
import com.example.myapplication.R
import com.github.mikephil.charting.charts.PieChart

class CustomAdapter(private val mList: List<ItemsViewModel>, private val onClickListener: OnClickListener) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private lateinit var boarder_background : GradientDrawable
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        boarder_background =
            ResourcesCompat.getDrawable(parent.context.resources, R.drawable.border_background, null) as GradientDrawable

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(itemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = itemsViewModel.text



        boarder_background.setColor(itemsViewModel.colour)
        holder.frameView.background = boarder_background
        //holder.frameView.setBackgroundColor(itemsViewModel.colour)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(itemsViewModel)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val frameView: FrameLayout = itemView.findViewById(R.id.frameView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    class OnClickListener(val clickListener: (meme: ItemsViewModel) -> Unit) {
        fun onClick(item: ItemsViewModel) = clickListener(item)
    }
}
