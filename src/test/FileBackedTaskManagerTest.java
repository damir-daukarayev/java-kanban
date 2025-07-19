package test;

import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        // Delete the temporary file after each test
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private boolean areTasksEqual(Task t1, Task t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getId() != t2.getId()) return false;
        if (!t1.getName().equals(t2.getName())) return false;
        if (!t1.getDescription().equals(t2.getDescription())) return false;
        if (t1.getTaskStatus() != t2.getTaskStatus()) return false;
        if (!t1.getType().equals(t2.getType())) return false;

        if (t1 instanceof Subtask && t2 instanceof Subtask) {
            return ((Subtask) t1).getEpicId() == ((Subtask) t2).getEpicId();
        }
        return true;
    }

    @Test
    void shouldLoadEmptyFileCorrectly() {
        // Here, 'manager' (FileBackedTaskManager) is already initialized with an empty file
        // No need to create tasks for this test, as we are testing loading an empty file.
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список тасков должен быть пустой.");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустой.");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список сабтасков должен быть пустой.");
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task1 = manager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = manager.createTask(new Task("Task 2", "Description 2"));
        Task task3 = manager.createTask(new Task("Task 3", "Description 3"));

        Epic epic1 = manager.createEpic(new Epic("Epic 1", "Description of epic 1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1",
                "Subtask 1 of Epic 1", TaskStatus.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Epic 2", "Description of epic 2"));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask 2",
                "Subtask 2 of Epic 2", TaskStatus.NEW, epic2.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask 3",
                "Subtask 3 of Epic 2", TaskStatus.NEW, epic2.getId()));

        InMemoryTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(3, loadedManager.getAllTasks().size(), "Должно быть 3 таска");
        assertEquals(2, loadedManager.getAllEpics().size(), "Должно быть 2 эпика");
        assertEquals(3, loadedManager.getAllSubtasks().size(), "Должно быть 3 подзадачи");
    }
}
