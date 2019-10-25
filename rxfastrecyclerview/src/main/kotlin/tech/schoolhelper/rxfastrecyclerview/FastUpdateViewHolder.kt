package tech.schoolhelper.rxfastrecyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class FastUpdateViewHolder<ENTITY : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
	
	private val compositeDisposable = CompositeDisposable()
	
	/**
		* Don't set up listeners or observables into this method
		* @see [setupListeners]
		* @see [setupObservables]
		*/
	abstract fun initEntity(entity: ENTITY)
	
	/**
		* This methods called one time when holder created
		*/
	protected open fun setupListeners(entity: ENTITY) = Unit
	
	/**
		* This methods called one time when holder created
		*/
	protected open fun setupObservables(compositeDisposable: CompositeDisposable) = Unit
	
	fun bind(entity: ENTITY, changeEntitySubject: Observable<ChangeEntity<ENTITY>>) {
		onRecycle()
		
		initEntity(entity)
		setupListeners(entity)
		
		compositeDisposable.add(changeEntitySubject
				.filter { it.position == adapterPosition }
				.map { it.entity }
				.subscribe(this::initEntity) { Log.e(TAG, "Something went wrong", it) })
		
		setupObservables(compositeDisposable)
	}
	
	internal fun onRecycle() {
		this.compositeDisposable.clear()
	}
	
	companion object {
		private const val TAG = "FAST_UPDATE_VIEW_HOLDER"
	}
	
}