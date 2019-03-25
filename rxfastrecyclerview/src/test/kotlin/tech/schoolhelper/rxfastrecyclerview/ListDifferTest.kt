package tech.schoolhelper.rxfastrecyclerview

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test

private data class TestEntity(
		val id: Int,
		val content: String
)

class ListDifferTest {
	
	private val differ: ListDiffer<TestEntity> = object : ListDiffer<TestEntity>() {
		override fun areItemTheSame(old: TestEntity, new: TestEntity): Boolean {
			return old.id == new.id
		}
		
		override fun areContentTheSame(old: TestEntity, new: TestEntity): Boolean {
			return old.content == new.content
		}
	}
	
	private val entity1 = TestEntity(1, "content1")
	private val entity2 = TestEntity(2, "content2")
	private val entity3 = TestEntity(31, "content31")
	private val insertData1 = TestEntity(3, "content3")
	private val insertData2 = TestEntity(4, "content4")
	private val update1 = TestEntity(1, "content3")
	private val update2 = TestEntity(2, "content4")
	
	@Test
	fun `test data for insert two entities together`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity2, insertData1, insertData2)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity1, entity2, insertData1, insertData2), listOf(InsertEntity(2, insertData1), InsertEntity(3, insertData2)))
		)
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for change two entities one by one`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, update2),
				listOf(update1, update2)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity1, update2), listOf(ChangeEntity(1, update2))),
				UpdateListAction(listOf(update1, update2), listOf(ChangeEntity(0, update1)))
		)
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for change two entities together`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(update1, update2)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2))),
				UpdateListAction(listOf(update1, update2), listOf(ChangeEntity(0, update1), ChangeEntity(1, update2)))
		)
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for change one entity`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity1, update2, entity3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2), InsertEntity(2, entity3))),
				UpdateListAction(listOf(entity1, update2, entity3), listOf(ChangeEntity(1, update2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for remove one entity`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity2)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity2), listOf(RemoveEntity(0, entity1)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for remove medium entity`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity1, entity3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2), InsertEntity(2, entity3))),
				UpdateListAction(listOf(entity1, entity3), listOf(RemoveEntity(1, entity2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for remove two entity together`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity3)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2), InsertEntity(2, entity3))),
				UpdateListAction(listOf(entity3), listOf(RemoveEntity(0, entity1), RemoveEntity(1, entity2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for remove all entities`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf()
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2), InsertEntity(2, entity3))),
				UpdateListAction(listOf(), listOf(RemoveEntity(0, entity1), RemoveEntity(1, entity2), RemoveEntity(2, entity3)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test data for insert two entities one by one`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity2, insertData1),
				listOf(entity1, entity2, insertData1, insertData2)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(0, entity1), InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity1, entity2, insertData1), listOf(InsertEntity(2, insertData1))),
				UpdateListAction(listOf(entity1, entity2, insertData1, insertData2), listOf(InsertEntity(3, insertData2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	private fun checkIsTransformCorrect(input: List<List<TestEntity>>, expected: List<ListAction<TestEntity>>) {
		val inputObservable = Observable.fromIterable(input)
		val testObservable = TestObserver<ListAction<TestEntity>>()
		
		inputObservable
				.compose(differ.transformToDiff())
				.subscribe(testObservable)
		
		testObservable.assertValues(*expected.toTypedArray())
	}
}