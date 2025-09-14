package com.example.noteapp.presentation.home

import app.cash.turbine.test
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.fakes.FakeNoteRepository
import com.example.noteapp.presentation.screens.home.HomeEvents
import com.example.noteapp.presentation.screens.home.HomeViewModel
import com.example.noteapp.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var repo: FakeNoteRepository
    private lateinit var vm: HomeViewModel

    private fun n(
        id: Long,
        title: String,
        desc: String? = null,
        pinned: Boolean = false,
        createdAt: Long,
        categoryBadge: String?,
        reminderAt: Long?
    ) = NoteEntity(id = id, title = title, description = desc, pinned = pinned, createdAt = createdAt, category = categoryBadge, reminderAt = reminderAt)

    @Before
    fun setup() {
        repo = FakeNoteRepository(
            initial = listOf(
                n(1, "Alpha", "first", pinned = false, createdAt = 100, categoryBadge = "work", reminderAt = 23674532),
                n(2, "Beta", "second", pinned = true,  createdAt = 200, categoryBadge = "study", reminderAt = 7455857),
                n(3, "Gamma", "third", pinned = false, createdAt = 300, categoryBadge = "workout", reminderAt = 4559087)
            )
        )
        vm = HomeViewModel(
            noteRepository = repo,
            io = mainRule.dispatcher
        )
    }

    @Test
    fun `initial notes are sorted (pinned first, then createdAt desc)`() = runTest {
        advanceUntilIdle()
        val list = vm.notes.first()

        assertEquals(list.map { it.id }, listOf(2L, 3L, 1L))
        assertTrue(list.first().pinned)
        assertTrue(list[1].createdAt >= list[2].createdAt)
    }

    @Test
    fun `onSearchChange filters by title or description case-insensitively`() = runTest {
        advanceUntilIdle()
        vm.onSearchChange("be")
        advanceUntilIdle()

        val list = vm.notes.first()
        assertEquals(1, list.size)
        assertEquals(2L, list.first().id)
    }

    @Test
    fun `onGridToggleClicked toggles between LIST and GRID`() = runTest {
        assertEquals(LayoutMode.LIST, vm.layoutMode.value)
        vm.onGridToggleClicked()
        assertEquals(LayoutMode.GRID, vm.layoutMode.value)
        vm.onGridToggleClicked()
        assertEquals(LayoutMode.LIST, vm.layoutMode.value)
    }

    @Test
    fun `toggleSelection adds and removes ids`() = runTest {
        assertTrue(vm.selected.value.isEmpty())
        vm.toggleSelection(2L)
        assertEquals(setOf(2L), vm.selected.value)
        vm.toggleSelection(2L)
        assertTrue(vm.selected.value.isEmpty())
    }

    @Test
    fun `onNoteDetailsClicked emits NavigateToNoteDetailsScreen`() = runTest {
        vm.events.test {
            vm.onNoteDetailsClicked(42L)
            val ev = awaitItem()
            assertTrue(ev is HomeEvents.NavigateToNoteDetailsScreen)
            assertEquals(42L, (ev as HomeEvents.NavigateToNoteDetailsScreen).noteId)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onAddNoteClicked emits NavigateToAddNoteScreen`() = runTest {
        vm.events.test {
            vm.onAddNoteClicked()
            val ev = awaitItem()
            assertEquals(HomeEvents.NavigateToAddNoteScreen, ev)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteSelected removes notes and clears selection`() = runTest {
        vm.toggleSelection(1L)
        advanceUntilIdle()

        vm.deleteSelected()
        advanceUntilIdle()

        assertEquals(listOf(2L, 3L), repo.snapshot().map { it.id }.sorted())
        assertTrue(vm.selected.value.isEmpty())
    }

    @Test
    fun `deleteSelected no-op when nothing selected`() = runTest {
        val before = repo.snapshot()
        vm.deleteSelected()
        advanceUntilIdle()
        assertEquals(before, repo.snapshot())
    }

    @Test
    fun `pinSelected pins target when exactly one selected and none previously pinned`() = runTest {
        repo.setAll(repo.snapshot().map { it.copy(pinned = false) })
        advanceUntilIdle()

        vm.toggleSelection(3L)
        vm.pinSelected()
        advanceUntilIdle()

        val now = repo.snapshot()
        val pinned = now.firstOrNull { it.pinned }
        assertNotNull(pinned)
        assertEquals(3L, pinned!!.id)
        assertTrue(vm.selected.value.isEmpty())
    }

    @Test
    fun `pinSelected emits error when selecting more than one`() = runTest {
        vm.toggleSelection(2L)
        vm.toggleSelection(3L)

        vm.events.test {
            vm.pinSelected()
            val ev = awaitItem()
            assertTrue(ev is HomeEvents.Error)
            assertTrue((ev as HomeEvents.Error).message.contains("Select exactly one"))
            cancelAndConsumeRemainingEvents()
        }
    }
}
