package tech.schoolhelper.rxfastrecyclerview

import io.kotlintest.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.observers.TestObserver
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestFastAdapterController {

    private val notifyDataSetChanged: () -> Unit = mockk(relaxed = true)
    private val notifyItemRemoved: (Int) -> Unit = mockk(relaxed = true)
    private val notifyItemRemovedRange: (Int, Int) -> Unit = mockk(relaxed = true)
    private val notifyItemInserted: (Int) -> Unit = mockk(relaxed = true)
    private val notifyItemInsertedRange: (Int, Int) -> Unit = mockk(relaxed = true)
    private val notifyItemMoved: (Int, Int) -> Unit = mockk(relaxed = true)

    private lateinit var controller: FastAdapterController<TestEntity>

    @BeforeEach
    fun setUp() {
        controller = FastAdapterController(
            notifyDataSetChanged,
            notifyItemMoved,
            notifyItemRemoved,
            notifyItemRemovedRange,
            notifyItemInserted,
            notifyItemInsertedRange
        )
    }

    @Test
    fun initContent() {
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
        val mockList = makeListOfMocks(10)

        val command = UpdateListAction(mockList, listOf(InsertEntity(5, mockList[5])))
        controller.updateContent(command)

        verify {
            notifyItemInserted.invoke(5)
        }

        verifyNoCallNotify(excludeNotifyItemInsert = true)

        controller.items shouldBe mockList
    }

    @Test
    fun `test insert two`() {
        val mockList = makeListOfMocks(2)

        val command = UpdateListAction(
            mockList,
            listOf(InsertEntity(0, mockList[0]), InsertEntity(1, mockList[1]))
        )
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

        val command = UpdateListAction(
            mockList,
            listOf(ChangeEntity(0, mockList[0]), ChangeEntity(1, mockList[1]))
        )

        val testObserver = TestObserver<ChangeEntity<TestEntity>>()

        controller.getChangeEntitiesPublisher()
            .subscribe(testObserver)

        controller.updateContent(command)

        testObserver.assertValues(*mockList.mapIndexed { index, testEntity ->
            ChangeEntity(
                index,
                testEntity
            )
        }.toTypedArray())

        verifyNoCallNotify()

        controller.items shouldBe mockList
    }

    @Test
    fun `test update content into middle of list one by one`() {
        val mockList = makeListOfMocks(6)

        val command = UpdateListAction(
            mockList,
            listOf(ChangeEntity(2, mockList[2]), ChangeEntity(4, mockList[4]))
        )

        val testObserver = TestObserver<ChangeEntity<TestEntity>>()

        controller.getChangeEntitiesPublisher()
            .subscribe(testObserver)

        controller.updateContent(command)

        testObserver.assertValues(*listOf(2, 4).map { index ->
            ChangeEntity(
                index,
                mockList[index]
            )
        }.toTypedArray())

        verifyNoCallNotify()

        controller.items shouldBe mockList
    }

    @Test
    fun `test update range into middle of list`() {
        val mockList = makeListOfMocks(6)

        val command =
            UpdateListAction(mockList, listOf(ChangeRange(2, 2, listOf(mockList[2], mockList[3]))))

        val testObserver = TestObserver<ChangeEntity<TestEntity>>()

        controller.getChangeEntitiesPublisher()
            .subscribe(testObserver)

        controller.updateContent(command)

        testObserver.assertValues(
            *listOf(
                ChangeEntity(2, mockList[2]),
                ChangeEntity(3, mockList[3])
            ).toTypedArray()
        )

        verifyNoCallNotify()

        controller.items shouldBe mockList
    }

    @Test
    fun `test remove one entity`() {
        val mockList = makeListOfMocks(4)

        val command = UpdateListAction(mockList, listOf(RemoveEntity(2, mockList[2])))
        controller.updateContent(command)

        verify {
            notifyItemRemoved.invoke(2)
        }

        verifyNoCallNotify(excludeNotifyItemRemoved = true)
        controller.items shouldBe mockList
    }

    @Test
    fun `test remove two entity`() {
        val mockList = makeListOfMocks(10)

        val command = UpdateListAction(
            mockList,
            listOf(RemoveEntity(2, mockList[2]), RemoveEntity(5, mockList[5]))
        )
        controller.updateContent(command)

        verify {
            notifyItemRemoved.invoke(2)
            notifyItemRemoved.invoke(5)
        }

        verifyNoCallNotify(excludeNotifyItemRemoved = true)
        controller.items shouldBe mockList
    }

    @Test
    fun `test remove range`() {
        val mockList = makeListOfMocks(10)

        val command = UpdateListAction(
            mockList,
            listOf(RemoveRange(2, 2, listOf(mockList[2], mockList[3], mockList[4])))
        )
        controller.updateContent(command)

        verify {
            notifyItemRemovedRange(2, 2)
        }

        verifyNoCallNotify(excludeNotifyItemRemovedRange = true)
        controller.items shouldBe mockList
    }

    @Test
    fun `test move two entities`() {
        val mockList = makeListOfMocks(10)

        val command = UpdateListAction(mockList, listOf(MoveEntity(2, 4)))
        controller.updateContent(command)

        verify {
            notifyItemMoved(2, 4)
        }

        verifyNoCallNotify(excludeNotifyItemMoved = true)
        controller.items shouldBe mockList
    }

    private fun makeListOfMocks(size: Int): List<TestEntity> {
        return MutableList(size) { mockk<TestEntity>() }.toList()
    }

    private fun verifyNoCallNotify(
        excludeNotifyDataSetChanged: Boolean = false,
        excludeNotifyItemInsert: Boolean = false,
        excludeNotifyItemInsertedRange: Boolean = false,
        excludeNotifyItemRemoved: Boolean = false,
        excludeNotifyItemRemovedRange: Boolean = false,
        excludeNotifyItemMoved: Boolean = false
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
        if (!excludeNotifyItemRemovedRange) {
            verify(exactly = 0) {
                notifyItemRemovedRange.invoke(any(), any())
            }
        }

    }
}