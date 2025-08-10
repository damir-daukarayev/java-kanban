package test;

import manager.*;
import modelling.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("Task", "Desc");
        Task task2 = new Task("Other", "Other Desc");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    public void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic("Epic A", "Description");
        manager.createEpic(epic); // ID присваивается внутри

        // manually set subtask's id to its epic's id
        Subtask invalidSubtask = new Subtask("Sub A", "Sub Desc", TaskStatus.NEW, epic.getId());
        manager.createSubtask(invalidSubtask);
        assertFalse(invalidSubtask.setId(epic.getId()),"Epic не должен содержать сам себя как подзадачу");
    }

    // дополнение к предыдущему тесту
    @Test
    public void shouldNotAllowEpicToHaveIdAsItsEpic() {
        Epic epic = new Epic("Epic A", "Description");
        manager.createEpic(epic); // ID присваивается внутри

        // manually set subtask's id to its epic's id
        Subtask invalidSubtask = new Subtask("Sub A", "Sub Desc", TaskStatus.NEW, epic.getId());
        manager.createSubtask(invalidSubtask);
        invalidSubtask.setId(epic.getId());

        assertNotEquals(epic.getId(), invalidSubtask.getId());
    }

    @Test
    public void shouldNotAllowSubtaskToHaveItselfAsEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // Create an epic and a subtask and have their IDs set to the same ID num
        Epic epic = new Epic("Epic A", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Invalid Subtask", "Invalid", TaskStatus.NEW, epic.getId());
        subtask.setId(42); // например, вручную задали ID
        subtask.setEpicId(42); // и epicId равен самому себе

        Subtask result = manager.createSubtask(subtask);

        // check if the subtask was created or not
        assertNull(result, "Subtask не должен быть своим же эпиком");
    }

    @Test
    void utilityClassReturnsInitializedManagers() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void managerAddsAndFindsDifferentTaskTypesById() {
        Task task = manager.createTask(new Task("Task", "Desc"));
        Epic epic = manager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = manager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        assertEquals(task, manager.getTask(task.getId()));
        assertEquals(epic, manager.getEpic(epic.getId()));
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    public void shouldNotModifyTaskOnAdd() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task original = new Task("Test Name", "Test Description");
        original.setTaskStatus(TaskStatus.IN_PROGRESS); // Задаем нестандартный статус

        // copy for comparison
        Task snapshot = new Task(original.getName(), original.getDescription());
        snapshot.setTaskStatus(original.getTaskStatus());

        Task created = manager.createTask(original);

        // comparing the tasks
        assertEquals(snapshot.getName(), created.getName(), "Имя задачи должно остаться неизменным");
        assertEquals(snapshot.getDescription(), created.getDescription(), "Описание должно остаться неизменным");
        assertEquals(snapshot.getTaskStatus(), created.getTaskStatus(), "Статус должен остаться неизменным");

        assertTrue(created.getId() >= 0, "ID должен быть сгенерирован");

        // making sure that the task didn't change, except for ID
        assertEquals(snapshot, new Task(created.getName(), created.getDescription(), created.getTaskStatus()),
                "Задача не должна изменяться по содержимому");

        // Проверка, что полученная задача из менеджера идентична
        Task stored = manager.getTask(created.getId());
        assertNotNull(stored, "Задача должна быть найдена по ID");
        assertEquals(created, stored, "Сохраненная задача должна совпадать с возвращенной после создания");
    }

    @Test
    public void shouldStoreTaskSnapshotInHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Initial Name", "Initial Description");
        task.setTaskStatus(TaskStatus.NEW);

        Task created = manager.createTask(task);

        Task accessed = manager.getTask(created.getId());

        accessed.setName("Modified Name");
        accessed.setDescription("Modified Description");
        accessed.setTaskStatus(TaskStatus.DONE);

        List<Task> history = manager.getHistoryManager();

        assertEquals(1, history.size(), "История должна содержать одну задачу");

        Task historySnapshot = history.get(0);

        assertEquals("Initial Name", historySnapshot.getName(), "Имя в истории должно быть как у оригинала при доступе");
        assertEquals("Initial Description", historySnapshot.getDescription(), "Описание должно совпадать со старым");
        assertEquals(created.getId(), history.get(0).getId());
    }


    @Test
    void taskIsUnchangedAfterAddingToManager() {
        Task original = new Task("Immutable", "Check");
        Task added = manager.createTask(original);
        assertEquals(original.getName(), added.getName());
        assertEquals(original.getDescription(), added.getDescription());
        assertEquals(original.getTaskStatus(), added.getTaskStatus());
    }

    @Test
    void shouldReturnEpicStatusNewForAllSubtasksWithStatusNew() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1", TaskStatus.NEW, epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask2", "Description2", TaskStatus.NEW, epic.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask3", "Description3", TaskStatus.NEW, epic.getId()));
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void shouldReturnEpicStatusDoneForAllSubtasksWithStatusDone() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1", TaskStatus.DONE, epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask2", "Description2", TaskStatus.DONE, epic.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask3", "Description3", TaskStatus.DONE, epic.getId()));
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void shouldReturnEpicStatusInProgressForAllSubtasksWithStatusMixed() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1", TaskStatus.NEW, epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask2", "Description2", TaskStatus.DONE, epic.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask3", "Description3", TaskStatus.NEW, epic.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void shouldReturnEpicStatusInProgressForAllSubtasksWithStatusInProgress() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1",
                TaskStatus.IN_PROGRESS, epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask2", "Description2",
                TaskStatus.IN_PROGRESS, epic.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask3", "Description3",
                TaskStatus.IN_PROGRESS, epic.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void shouldReturn11_50AsEndTime() {
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1",
                TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 31, 11, 30)));
        assertEquals(epic1.getStartTime().plusMinutes(epic1.getDuration().toMinutes()), epic1.getEndTime());
    }

    @Test
    void shouldReturnTotalDurationOf40Minutes() {
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1",
                TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 31, 11, 30)));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask2", "Description2",
                TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 31, 12, 0)));
        assertEquals(Duration.ofMinutes(40), epic1.getDuration());
    }

    @Test
    void shouldCatchOverlappingTasks() {
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Description1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask1", "Description1",
                TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2025, Month.JULY, 31, 11, 30)));

        assertEquals(null, manager.createSubtask(new Subtask("Subtask2", "Description2",
                TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 31, 11, 50))));
    }
}
