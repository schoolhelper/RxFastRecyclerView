package tech.schoolhelper.rxfastrecyclerview

import androidx.recyclerview.widget.RecyclerView

abstract class FastAdapter<ENTITY : Any, ViewHolder : FastUpdateViewHolder<ENTITY>> : RecyclerView.Adapter<ViewHolder>() {
	
	private val controller = FastAdapterController<ENTITY>(
			::notifyDataSetChanged,
			::notifyItemMoved,
			::notifyItemRemoved, ::notifyItemRangeRemoved,
			::notifyItemInserted, ::notifyItemRangeInserted)
	
	protected val items = controller.items
	
	fun updateContent(commands: ListAction<ENTITY>) {
		controller.updateContent(commands)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(items[position], controller.getChangeEntitiesPublisher())
	}
	
	override fun getItemCount(): Int = items.size
	
	override fun onViewRecycled(holder: ViewHolder) {
		super.onViewRecycled(holder)
		holder.onRecycle()
	}
}