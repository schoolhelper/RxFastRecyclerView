package tech.schoolhelper.rxfastrecyclerview

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Ignore
import org.junit.Test

class ListDifferWithFullOverrideTest {
	
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
	private val update3 = TestEntity(31, "update31")
	
	@Test
	fun `test insert two entities together`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity2, insertData1, insertData2)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity1, entity2, insertData1, insertData2), listOf(InsertRange(2, 2, listOf(insertData1, insertData2))))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test insert one entities together`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity2, insertData1)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity1, entity2, insertData1), listOf(InsertEntity(2, insertData1)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test change two entities one by one`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, update2),
				listOf(update1, update2)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity1, update2), listOf(ChangeEntity(1, update2))),
				UpdateListAction(listOf(update1, update2), listOf(ChangeEntity(0, update1)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test change two entities together`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(update1, update2)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(update1, update2), listOf(ChangeRange(0, 2, listOf(update1, update2))))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test change one entity`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity1, update2, entity3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertRange(0, 3, listOf(entity1, entity2, entity3)))),
				UpdateListAction(listOf(entity1, update2, entity3), listOf(ChangeEntity(1, update2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test remove one entity`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity2)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity2), listOf(RemoveEntity(0, entity1)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test remove medium entity`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity1, entity3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertRange(0, 3, listOf(entity1, entity2, entity3)))),
				UpdateListAction(listOf(entity1, entity3), listOf(RemoveEntity(1, entity2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test remove two entity together`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity3)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertRange(0, 3, listOf(entity1, entity2, entity3)))),
				UpdateListAction(listOf(entity3), listOf(RemoveRange(0, 2, listOf(entity1, entity2))))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test remove all entities`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf()
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertRange(0, 3, listOf(entity1, entity2, entity3)))),
				UpdateListAction(listOf(), listOf(RemoveRange(0, 3, listOf(entity1, entity2, entity3))))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test insert two entities one by one`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity2, insertData1),
				listOf(entity1, entity2, insertData1, insertData2)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity1, entity2, insertData1), listOf(InsertEntity(2, insertData1))),
				UpdateListAction(listOf(entity1, entity2, insertData1, insertData2), listOf(InsertEntity(3, insertData2)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test move`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity2, entity1)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 2, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity2, entity1), listOf(MoveEntity(1, 0)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Ignore
	@Test
	fun `test insert and remove`() {
		val input = listOf(
				listOf(entity1, entity2),
				listOf(entity1, entity3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertRange(0, 1, listOf(entity1, entity2)))),
				UpdateListAction(listOf(entity1, entity3), listOf(RemoveEntity(1, entity2), InsertEntity(1, entity3)))
		)
		
		checkIsTransformCorrect(input, expected)
	}

	@Test
	fun `test remove and update`() {
		val input = listOf(
				listOf(entity1, entity2, entity3),
				listOf(entity1, update3)
		)
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertRange(0, 2, listOf(entity1, entity2, entity3)))),
				UpdateListAction(listOf(entity1, update3), listOf(RemoveEntity(1, entity2), ChangeEntity(1, update3)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test init list one by one`() {
		val input = listOf(
				listOf(entity1),
				listOf(entity1, entity2),
				listOf(entity1, entity2, entity3)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1), listOf(InsertEntity(0, entity1))),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity1, entity2, entity3), listOf(InsertEntity(2, entity3)))
		)
		
		checkIsTransformCorrect(input, expected)
	}
	
	@Test
	fun `test add entity one by one with changes`() {
		val input = listOf(
				listOf(entity1),
				listOf(entity1, entity2),
				listOf(entity1, update2, entity3)
		)
		
		val expected = listOf<ListAction<TestEntity>>(
				InitListAction(emptyList()),
				UpdateListAction(listOf(entity1), listOf(InsertEntity(0, entity1))),
				UpdateListAction(listOf(entity1, entity2), listOf(InsertEntity(1, entity2))),
				UpdateListAction(listOf(entity1, update2, entity3), listOf(InsertEntity(2, entity3), ChangeEntity(1, update2)))
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
