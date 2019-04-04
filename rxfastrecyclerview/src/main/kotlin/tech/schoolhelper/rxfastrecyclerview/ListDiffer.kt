package tech.schoolhelper.rxfastrecyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import io.reactivex.ObservableTransformer

sealed class UpdateEntityCommand<E : Any>
/**
	* InsertEntity command which say, you need to add a entity to your collection into some position
	* [position] - position of entity in new collection
	* [entity] - item for insert
	*/
data class InsertEntity<E : Any>(val position: Int, val entity: E) : UpdateEntityCommand<E>()

data class InsertRange<E : Any>(val from: Int, val count: Int, val entities: List<E>) : UpdateEntityCommand<E>()

data class RemoveEntity<E : Any>(val position: Int, val entity: E) : UpdateEntityCommand<E>()
data class RemoveRange<E : Any>(val from: Int, val count: Int, val entities: List<E>) : UpdateEntityCommand<E>()

/**
	* ChangeEntity command which say, you need to update a entity inside the collection on a position
	* [position] - position of entity for update
	* [entity] - a new entity
	*/
data class ChangeEntity<E : Any>(val position: Int, val entity: E) : UpdateEntityCommand<E>()

data class ChangeRange<E : Any>(val from: Int, val count: Int, val entities: List<E>) : UpdateEntityCommand<E>()

data class MoveEntity<E : Any>(val fromPosition: Int, val toPosition: Int) : UpdateEntityCommand<E>()

sealed class ListAction<E : Any>(open val data: List<E>)
data class InitListAction<E : Any>(override val data: List<E>) : ListAction<E>(data)
data class UpdateListAction<E : Any>(
		override val data: List<E>,
		val changes: List<UpdateEntityCommand<E>>) : ListAction<E>(data)

private class DiffCallback<E : Any>(
		private val oldList: List<E>,
		private val newList: List<E>,
		private val areItemTheSame: (E, E) -> Boolean,
		private val areContentTheSame: (E, E) -> Boolean) : DiffUtil.Callback() {
	
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return areItemTheSame(oldList[oldItemPosition], newList[newItemPosition])
	}
	
	override fun getOldListSize(): Int = oldList.size
	
	override fun getNewListSize(): Int = newList.size
	
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return areContentTheSame(oldList[oldItemPosition], newList[newItemPosition])
	}
}

abstract class ListDiffer<E : Any> {
	
	abstract fun areItemTheSame(old: E, new: E): Boolean
	
	open fun areContentTheSame(old: E, new: E): Boolean {
		return old == new
	}
	
	fun transformToDiff(): ObservableTransformer<List<E>, ListAction<E>> {
		return ObservableTransformer { observable ->
			observable.scan(InitListAction(emptyList())) { old: ListAction<E>, newList: List<E> ->
				val updateActions = mutableListOf<UpdateEntityCommand<E>>()
				
				DiffUtil.calculateDiff(DiffCallback(old.data, newList, ::areItemTheSame, ::areContentTheSame), true)
						.dispatchUpdatesTo(object : ListUpdateCallback {
							override fun onChanged(position: Int, count: Int, payload: Any?) {
								if (count == 1) {
									updateActions.add(ChangeEntity(position, newList[position]))
								} else {
									updateActions.add(ChangeRange(position, count, newList.subList(position, position + count)))
								}
							}
							
							override fun onMoved(fromPosition: Int, toPosition: Int) {
								updateActions.add(MoveEntity(fromPosition, toPosition))
							}
							
							override fun onInserted(position: Int, count: Int) {
								if (count == 1) {
									updateActions.add(InsertEntity(position, newList[position]))
								} else {
									updateActions.add(InsertRange(position, count, newList.subList(position, position + count)))
								}
							}
							
							override fun onRemoved(position: Int, count: Int) {
								if (count == 1) {
									updateActions.add(RemoveEntity(position, old.data[position]))
								} else {
									updateActions.add(RemoveRange(position, count, old.data.subList(position, position + count)))
								}
							}
						})
				return@scan UpdateListAction(newList, updateActions)
			}
		}
	}
}