package tech.schoolhelper.rxfastrecyclerview

import io.kotlintest.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class FastAdapterControllerTest {
	
	private val notifyDataSetChanged: () -> Unit = mockk(relaxed = true)
	private val notifyItemRemoved: (Int) -> Unit = mockk(relaxed = true)
	private val notifyItemInserted: (Int) -> Unit = mockk(relaxed = true)
	private val notifyItemInsertedRange: (Int, Int) -> Unit = mockk(relaxed = true)
	private val notifyItemMoved: (Int, Int) -> Unit = mockk(relaxed = true)
	
	private lateinit var controller: FastAdapterController<TestEntity>
	
	@Before
	fun setUp() {
		controller = FastAdapterController(notifyDataSetChanged, notifyItemRemoved, notifyItemInserted, notifyItemMoved)
	}
	
	@Test
	fun `test init content`() {
		val mockList = makeListOfMocks(2)
		
		val command = InitListAction(mockList)
		controller.updateContent(command)
		
		verify {
			notifyDataSetChanged.invoke()
		}
		
		verifyNoCallNotify(excludeNotifyDataSetChanged = true)
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test insert one`() {
		val mockList = makeListOfMocks(2)
		
		val command = UpdateListAction(mockList, listOf(InsertEntity(0, mockList[0]), InsertEntity(1, mockList[1])))
		controller.updateContent(command)
		
		verify {
			notifyItemInserted.invoke(0)
			notifyItemInserted.invoke(1)
		}
		
		verifyNoCallNotify(excludeNotifyItemInsert = true)
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test insert range`() {
		val mockList = makeListOfMocks(3)
		
		val command = UpdateListAction(mockList, listOf(InsertRange(0, 2, mockList)))
		
		controller.updateContent(command)
		
		verify {
			notifyItemInsertedRange.invoke(0, 2)
		}
		
		verifyNoCallNotify(excludeNotifyItemInsertedRange = true)
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test update all content one by one`() {
		val mockList = makeListOfMocks(2)
		
		val command = UpdateListAction(mockList, listOf(ChangeEntity(0, mockList[0]), ChangeEntity(1, mockList[1])))
		
		val testObserver = TestObserver<ChangeEntity<TestEntity>>()
		
		controller.getChangeEntitiesPublisher()
				.subscribe(testObserver)
		
		controller.updateContent(command)
		
		testObserver.assertValues(*mockList.mapIndexed { index, testEntity -> ChangeEntity(index, testEntity) }.toTypedArray())
		
		verifyNoCallNotify()
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test update content into middle of list one by one`() {
		val mockList = makeListOfMocks(6)
		
		val command = UpdateListAction(mockList, listOf(ChangeEntity(2, mockList[2]), ChangeEntity(4, mockList[4])))
		
		val testObserver = TestObserver<ChangeEntity<TestEntity>>()
		
		controller.getChangeEntitiesPublisher()
				.subscribe(testObserver)
		
		controller.updateContent(command)
		
		testObserver.assertValues(*listOf(2, 4).map { index -> ChangeEntity(index, mockList[index]) }.toTypedArray())
		
		verifyNoCallNotify()
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test update range into middle of list`() {
		val mockList = makeListOfMocks(6)
		
		val command = UpdateListAction(mockList, listOf(ChangeRange(2, 3, listOf(mockList[2], mockList[3]))))
		
		val testObserver = TestObserver<ChangeEntity<TestEntity>>()
		
		controller.getChangeEntitiesPublisher()
				.subscribe(testObserver)
		
		controller.updateContent(command)
		
		testObserver.assertValues(*listOf(ChangeEntity(2, mockList[2]), ChangeEntity(3, mockList[3])).toTypedArray())
		
		verifyNoCallNotify()
		
		controller.items shouldBe mockList
	}
	
	private fun makeListOfMocks(size: Int): List<TestEntity> {
		return MutableList(size) { mockk<TestEntity>() }.toList()
	}
	
	private fun verifyNoCallNotify(
			excludeNotifyItemInsert: Boolean = false,
			excludeNotifyDataSetChanged: Boolean = false,
			excludeNotifyItemRemoved: Boolean = false,
			excludeNotifyItemMoved: Boolean = false,
			excludeNotifyItemInsertedRange: Boolean = false
	) {
		if (!excludeNotifyItemInsert) {
			verify(exactly = 0) {
				notifyItemInserted.invoke(any())
			}
		}
		if (!excludeNotifyDataSetChanged) {
			verify(exactly = 0) {
				notifyDataSetChanged.invoke()
			}
		}
		
		if (!excludeNotifyItemRemoved) {
			verify(exactly = 0) {
				notifyItemRemoved.invoke(any())
			}
		}
		
		if (!excludeNotifyItemMoved) {
			verify(exactly = 0) {
				notifyItemMoved.invoke(any(), any())
			}
		}
		
		if (!excludeNotifyItemInsertedRange) {
			verify(exactly = 0) {
				notifyItemInsertedRange.invoke(any(), any())
			}
		}
		
	}
}