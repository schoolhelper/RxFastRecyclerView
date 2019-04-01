package tech.schoolhelper.rxfastrecyclerview

import io.kotlintest.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.observers.TestObserver
import org.junit.Test

class FastAdapterControllerTest {
	
	@Test
	fun `test init content`() {
		val mockList = listOf<TestEntity>(
				mockk(),
				mockk()
		)
		
		val command = InitListAction(mockList)
		
		val notifyDataSetChanged: () -> Unit = mockk(relaxed = true)
		val notifyItemRemoved: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemInserted: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemMoved: (Int, Int) -> Unit = mockk(relaxed = true)
		
		val controller = FastAdapterController<TestEntity>(notifyDataSetChanged, notifyItemRemoved, notifyItemInserted, notifyItemMoved)
		controller.updateContent(command)
		
		verify {
			notifyDataSetChanged.invoke()
		}
		
		verify(exactly = 0) {
			notifyItemRemoved.invoke(any())
			notifyItemInserted.invoke(any())
			notifyItemMoved.invoke(any(), any())
		}
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test insert content`() {
		val mockList = listOf<TestEntity>(
				mockk(),
				mockk()
		)
		
		val command = UpdateListAction(mockList, listOf(InsertEntity(0, mockList[0]), InsertEntity(1, mockList[1])))
		
		val notifyDataSetChanged: () -> Unit = mockk(relaxed = true)
		val notifyItemRemoved: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemInserted: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemMoved: (Int, Int) -> Unit = mockk(relaxed = true)
		
		val controller = FastAdapterController<TestEntity>(notifyDataSetChanged, notifyItemRemoved, notifyItemInserted, notifyItemMoved)
		controller.updateContent(command)
		
		verify {
			notifyItemInserted.invoke(0)
			notifyItemInserted.invoke(1)
		}
		
		verify(exactly = 0) {
			notifyDataSetChanged.invoke()
			notifyItemRemoved.invoke(any())
			notifyItemMoved.invoke(any(), any())
		}
		
		controller.items shouldBe mockList
	}
	
	@Test
	fun `test update content`() {
		val mockList = listOf<TestEntity>(
				mockk(),
				mockk()
		)
		
		val command = UpdateListAction(mockList, listOf(ChangeEntity(0, mockList[0]), ChangeEntity(1, mockList[1])))
		
		val notifyDataSetChanged: () -> Unit = mockk(relaxed = true)
		val notifyItemRemoved: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemInserted: (Int) -> Unit = mockk(relaxed = true)
		val notifyItemMoved: (Int, Int) -> Unit = mockk(relaxed = true)
		
		val controller = FastAdapterController<TestEntity>(notifyDataSetChanged, notifyItemRemoved, notifyItemInserted, notifyItemMoved)
		
		val testObserver = TestObserver<ChangeEntity<TestEntity>>()
		
		controller.changeEntitiesPublisher
				.subscribe(testObserver)
		
		controller.updateContent(command)
		
		testObserver.assertValues(*mockList.mapIndexed { index, testEntity -> ChangeEntity(index, testEntity) }.toTypedArray())
		
		verify(exactly = 0) {
			notifyItemInserted.invoke(any())
			notifyDataSetChanged.invoke()
			notifyItemRemoved.invoke(any())
			notifyItemMoved.invoke(any(), any())
		}
		
		controller.items shouldBe mockList
	}
}