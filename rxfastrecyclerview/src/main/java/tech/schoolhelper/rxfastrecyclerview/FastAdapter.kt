package tech.schoolhelper.rxfastrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class FastAdapter<ENTITY : Any, ViewHolder : FastUpdateViewHolder<ENTITY>> : RecyclerView.Adapter<ViewHolder>() {
	
	protected val items: ArrayList<ENTITY> = ArrayList()
	
	private val changeEntitiesPublisher: PublishSubject<ChangeEntity<ENTITY>> = PublishSubject.create<ChangeEntity<ENTITY>>()
	
	fun updateContent(commands: ListAction<ENTITY>) {
		items.clear()
		items.addAll(commands.data)
		
		when (commands) {
			is InitListAction -> {
				notifyDataSetChanged()
			}
			is UpdateListAction -> {
				commands.changes.forEach { command ->
					when (command) {
						is InsertEntity -> notifyItemInserted(command.position)
						is RemoveEntity -> notifyItemRemoved(command.position)
						is ChangeEntity -> changeEntitiesPublisher.onNext(command)
						is MoveEntity -> notifyItemMoved(command.fromPosition, command.toPosition)
					}
				}
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(items[position], changeEntitiesPublisher)
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
	
	abstract fun setupListeners()
	
	fun bind(entity: ENTITY, changeEntitySubject: PublishSubject<ChangeEntity<ENTITY>>) {
		initEntity(entity)
		setupListeners()
		
		compositeDisposable.add(changeEntitySubject
				.filter { it.position == adapterPosition }
				.map { it.entity }
				.subscribe(this::initEntity, {}))
	}
	
	fun onRecycle() {
		this.compositeDisposable.clear()
	}
	
}