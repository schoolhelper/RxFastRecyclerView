package tech.schoolhelper.rxfastrecyclerview

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class FastAdapterController<ENTITY : Any>(
    private val notifyDataSetChanged: () -> Unit,
    private val notifyItemMoved: (Int, Int) -> Unit,
    private val notifyItemRemoved: (Int) -> Unit,
    private val notifyItemRemovedRange: (Int, Int) -> Unit,
    private val notifyItemInserted: (Int) -> Unit,
    private val notifyItemInsertedRange: (Int, Int) -> Unit
) {

    val items: ArrayList<ENTITY> = ArrayList()

    private val changeEntitiesPublisher: PublishSubject<ChangeEntity<ENTITY>> =
        PublishSubject.create<ChangeEntity<ENTITY>>()

    fun getChangeEntitiesPublisher(): Observable<ChangeEntity<ENTITY>> {
        return changeEntitiesPublisher
    }

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
                        is InsertRange -> notifyItemInsertedRange(command.from, command.count)
                        is RemoveEntity -> notifyItemRemoved(command.position)
                        is RemoveRange -> notifyItemRemovedRange(command.from, command.count)
                        is ChangeEntity -> changeEntitiesPublisher.onNext(command)
                        is ChangeRange -> {
                            (0 until command.count)
                                .mapNotNull { index ->
                                    val newEntity =
                                        command.entities.getOrNull(index) ?: return@mapNotNull null
                                    ChangeEntity(command.from + index, newEntity)
                                }
                                .forEach(changeEntitiesPublisher::onNext)
                        }
                        is MoveEntity -> notifyItemMoved(command.fromPosition, command.toPosition)
                    }
                }
            }
        }
    }
}