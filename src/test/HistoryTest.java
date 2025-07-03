package test;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static modelling.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryTest {

    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
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
    void shouldStoreLast12ViewedTasks() {
        for (int i = 0; i < 12; i++) {
            Task task = taskManager.createTask(new Task("Task " + i, "Desc"));
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(12, history.size(), "История должна содержать только 10 последних задач");

        // Check that only the last 10 accessed tasks are stored
        for (int i = 0; i < 12; i++) {
            assertEquals("Task " + i, history.get(i).getName());
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

    //доп тест
    @Test
    void modifyingReturnedListShouldNotAffectInternalHistory() {
        Task task = new Task("Test", "Protect internal");
        historyManager.add(task);

        List<Task> externalHistory = historyManager.getHistory();
        externalHistory.clear();

        List<Task> actualHistory = historyManager.getHistory();
        assertEquals(1, actualHistory.size());
    }

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

    @Test
    public void shouldReturnLastElementAsTask3AfterReAccess() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1", NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2", NEW));
        Task task3 = taskManager.createTask(new Task("Task 3", "Description 3", NEW));
        Task task4 = taskManager.createTask(new Task("Task 4", "Description 4", NEW));

        for (int i = 0; i < 4; i++) {
            taskManager.getTask(i);
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size());

        // Теперь получаем повторный доступ к задаче 3 (индекс 2)
        // последний элемент в листе должен быть "Задача 3"
        taskManager.getTask(2);
        history = taskManager.getHistory();
        assertEquals(task3, history.getLast());
    }

    @Test
    public void shouldReturnFirstAccessedTaskAfterReAccess() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1", NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2", NEW));
        Task task3 = taskManager.createTask(new Task("Task 3", "Description 3", NEW));
        Task task4 = taskManager.createTask(new Task("Task 4", "Description 4", NEW));

        for (int i = 0; i < 4; i++) {
            taskManager.getTask(i);
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(task1, history.getFirst());
        assertEquals(task4, history.getLast());

        taskManager.getTask(0);
        history = taskManager.getHistory();
        // Первая задача в списке теперь задача № 2, а не № 1
        assertEquals(task2, history.getFirst());
        // Последняя задача в списке теперь точно не задача № 4
        assertNotEquals(task4, history.getLast());
        // Последняя задача в списке теперь задача № 1, а не № 4
        assertEquals(task1, history.getLast());
    }

    @Test
    public void shoudlReturnSize0AfterRemovalViaInMemoryTaskManager() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 0", NEW));
        taskManager.getTask(0);
        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size());

        taskManager.deleteTask(0);
        history = taskManager.getHistory();

        assertEquals(0, history.size());
    }

    @Test
    public void shouldReturnSize0AfterMassDeletionOfTasks() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1", NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2", NEW));
        Task task3 = taskManager.createTask(new Task("Task 3", "Description 3", NEW));
        Task task4 = taskManager.createTask(new Task("Task 4", "Description 4", NEW));

        for (int i = 0; i < 4; i++) {
            taskManager.getTask(i);
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size());

        taskManager.clearAllTasks();
        history = taskManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void shouldReturnSize0AfterMassDeletionOfEpicsOnly() {
        Epic task1 = taskManager.createEpic(new Epic("Epic 1", "Description 1"));
        Epic task2 = taskManager.createEpic(new Epic("Epic 2", "Description 2"));
        Epic task3 = taskManager.createEpic(new Epic("Epic 3", "Description 3"));
        Epic task4 = taskManager.createEpic(new Epic("Epic 4", "Description 4"));

        for (int i = 0; i < 4; i++) {
            taskManager.getEpic(i);
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size());

        taskManager.clearAllEpics();
        history = taskManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void shouldReturnSize4AfterMassDeletionOfSubtasks() {
        Epic task1 = taskManager.createEpic(new Epic("Epic 1", "Description 1"));
        Epic task2 = taskManager.createEpic(new Epic("Epic 2", "Description 2"));
        Epic task3 = taskManager.createEpic(new Epic("Epic 3", "Description 3"));
        Epic task4 = taskManager.createEpic(new Epic("Epic 4", "Description 4"));

        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Subtask1", "Description of subtask1", NEW, task1.getId())
        );
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Subtask2", "Description of subtask2", NEW, task1.getId())
        );
        Subtask subtask3 = taskManager.createSubtask(
                new Subtask("Subtask3", "Description of subtask3", NEW, task2.getId())
        );
        Subtask subtask4 = taskManager.createSubtask(
                new Subtask("Subtask4", "Description of subtask4", NEW, task3.getId())
        );
        Subtask subtask5 = taskManager.createSubtask(
                new Subtask("Subtask5", "Description of subtask5", NEW, task3.getId())
        );
        Subtask subtask6 = taskManager.createSubtask(
                new Subtask("Subtask6", "Description of subtask6", NEW, task3.getId())
        );
        Subtask subtask7 = taskManager.createSubtask(
                new Subtask("Subtask7", "Description of subtask7", NEW, task4.getId())
        );

        for (int i = 0; i < 4; i++) {
            taskManager.getEpic(i);
        }

        taskManager.getAllSubtasks();

        List<Task> history = taskManager.getHistory();
        assertEquals(11, history.size());

        taskManager.clearAllSubtasks();
        history = taskManager.getHistory();
        assertEquals(4, history.size());
    }

    @Test
    public void shouldReturnSize0AfterMassDeletionOfEpics() {
        Epic task1 = taskManager.createEpic(new Epic("Epic 1", "Description 1"));
        Epic task2 = taskManager.createEpic(new Epic("Epic 2", "Description 2"));
        Epic task3 = taskManager.createEpic(new Epic("Epic 3", "Description 3"));
        Epic task4 = taskManager.createEpic(new Epic("Epic 4", "Description 4"));

        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Subtask1", "Description of subtask1", NEW, task1.getId())
        );
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Subtask2", "Description of subtask2", NEW, task1.getId())
        );
        Subtask subtask3 = taskManager.createSubtask(
                new Subtask("Subtask3", "Description of subtask3", NEW, task2.getId())
        );
        Subtask subtask4 = taskManager.createSubtask(
                new Subtask("Subtask4", "Description of subtask4", NEW, task3.getId())
        );
        Subtask subtask5 = taskManager.createSubtask(
                new Subtask("Subtask5", "Description of subtask5", NEW, task3.getId())
        );
        Subtask subtask6 = taskManager.createSubtask(
                new Subtask("Subtask6", "Description of subtask6", NEW, task3.getId())
        );
        Subtask subtask7 = taskManager.createSubtask(
                new Subtask("Subtask7", "Description of subtask7", NEW, task4.getId())
        );

        for (int i = 0; i < 4; i++) {
            taskManager.getEpic(i);
        }

        taskManager.getAllSubtasks();

        List<Task> history = taskManager.getHistory();
        assertEquals(11, history.size());

        taskManager.clearAllEpics();
        history = taskManager.getHistory();
        assertEquals(0, history.size());
    }
}

