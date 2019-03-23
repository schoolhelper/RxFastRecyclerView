package tech.schoolhelper.rxfastrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class FastAdapter : RecyclerView.Adapter<FastUpdateViewHolder>() {
}

abstract class FastUpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}