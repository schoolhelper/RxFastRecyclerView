package tech.schoolhelper.rxfastrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class FastAdapter<ENTITY : Any, ViewHolder : FastUpdateViewHolder<ENTITY>> : RecyclerView.Adapter<ViewHolder>() {
	
	private val controller = FastAdapterController<ENTITY>(::notifyDataSetChanged, ::notifyItemRemoved, ::notifyItemInserted, ::notifyItemMoved)
	
	protected val items = controller.items
	
	fun updateContent(commands: ListAction<ENTITY>) {
		controller.updateContent(commands)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(items[position], controller.changeEntitiesPublisher)
	}
	
	override fun getItemCount(): Int = items.size
	
	override fun onViewRecycled(holder: ViewHolder) {
		super.onViewRecycled(holder)
		holder.onRecycle()
	}
}

abstract class FastUpdateViewHolder<ENTITY : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
	
	protected val compositeDisposable = CompositeDisposable()
	
	abstract fun initEntity(entity: ENTITY)
	
	abstract fun setupListeners(entity: ENTITY)
	
	fun bind(entity: ENTITY, changeEntitySubject: Observable<ChangeEntity<ENTITY>>) {
		initEntity(entity)
		setupListeners(entity)
		
		compositeDisposable.add(changeEntitySubject
				.filter { it.position == adapterPosition }
				.map { it.entity }
				.subscribe(this::initEntity, {}))
	}
	
	fun onRecycle() {
		this.compositeDisposable.clear()
	}
	
}