package tech.schoolhelper.rxfastrecyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

interface IDiffCalculator<E : Any> {
    fun calculateDiff(
        old: List<E>,
        new: List<E>,
        areItemTheSame: (E, E) -> Boolean,
        areContentTheSame: (E, E) -> Boolean
    ): UpdateListAction<E>
}

class DefaultDiffCalculator<E : Any> : IDiffCalculator<E> {
    override fun calculateDiff(
        old: List<E>,
        new: List<E>,
        areItemTheSame: (E, E) -> Boolean,
        areContentTheSame: (E, E) -> Boolean
    ): UpdateListAction<E> {
        val updateActions = mutableListOf<UpdateEntityCommand<E>>()

        DiffUtil.calculateDiff(
            DiffCallback(
                old,
                new,
                areItemTheSame,
                areContentTheSame
            ), true
        )
            .dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onChanged(
                    position: Int,
                    count: Int,
                    payload: Any?
                ) {
                    if (count <= 0) {
                        return
                    }
                    if (count == 1) {
                        if (position < new.size && position >= 0) {
                            updateActions.add(ChangeEntity(position, new[position]))
                        }
                    } else {
                        updateActions.add(
                            ChangeRange(
                                position,
                                count,
                                new.subList(
                                    position.coerceAtMost(new.size),
                                    (position + count).coerceAtMost(new.size)
                                )
                            )
                        )
                    }
                }

                override fun onMoved(
                    fromPosition: Int,
                    toPosition: Int
                ) {
                    updateActions.add(MoveEntity(fromPosition, toPosition))
                }

                override fun onInserted(
                    position: Int,
                    count: Int
                ) {
                    if (count <= 0) {
                        return
                    }
                    if (count == 1) {
                        updateActions.add(InsertEntity(position, new[position]))
                    } else {
                        updateActions.add(
                            InsertRange(
                                position,
                                count,
                                new.subList(
                                    position.coerceAtMost(new.size),
                                    (position + count).coerceAtMost(new.size)
                                )
                            )
                        )
                    }
                }

                override fun onRemoved(
                    position: Int,
                    count: Int
                ) {
                    if (count <= 0) {
                        return
                    }
                    if (count == 1) {
                        updateActions.add(RemoveEntity(position, old[position]))
                    } else {
                        updateActions.add(
                            RemoveRange(
                                position,
                                count,
                                old.subList(
                                    position.coerceAtMost(new.size),
                                    (position + count).coerceAtMost(new.size)
                                )
                            )
                        )
                    }
                }
            })

        return UpdateListAction(new, updateActions)
    }

}