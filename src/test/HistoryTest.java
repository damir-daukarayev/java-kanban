package test;

import manager.InMemoryTaskManager;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static modelling.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldReturnEmptyHistoryInitially() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после инициализации");
    }

    @Test
    void shouldAddTaskToHistoryWhenAccessed() {
        Task task = taskManager.createTask(new Task("Test Task", "Description"));
        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 элемент");
        assertEquals(task, history.get(0), "Добавленная задача должна быть в истории");
    }

    @Test
    void shouldAddEpicAndSubtaskToHistory() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Sub", "SubDesc", TaskStatus.NEW, epic.getId()));

        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(epic, history.get(0));
        assertEquals(subtask, history.get(1));
    }

    @Test
    void shouldStoreLast10ViewedTasks() {
        for (int i = 0; i < 12; i++) {
            Task task = taskManager.createTask(new Task("Task " + i, "Desc"));
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать только 10 последних задач");

        // Check that only the last 10 accessed tasks are stored
        for (int i = 2; i < 12; i++) {
            assertEquals("Task " + i, history.get(i - 2).getName());
        }
    }

    @Test
    void shouldStoreAllTypes() {
        Task task = taskManager.createTask(new Task("Task ", "Desctiption"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "epic desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "sub desc", NEW, epic.getId()));

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void shouldAllowDuplicateViewsInHistory() {
        Task task = taskManager.createTask(new Task("Repeating Task", "Description"));
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task, history.get(1));
    }

//    @Test
//    void modifyingReturnedListShouldNotAffectInternalHistory() {
//        Task task = new Task("Test", "Protect internal");
//        historyManager.add(task);
//
//        List<Task> externalHistory = historyManager.getHistory();
//        externalHistory.clear();
//
//        List<Task> actualHistory = historyManager.getHistory();
//        assertEquals(1, actualHistory.size());
//    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}

