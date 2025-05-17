import manager.TaskManager;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // 1. Создание двух задач
        Task task1 = manager.createTask(new Task("Task 1", "Description of Task 1"));
        Task task2 = manager.createTask(new Task("Task 2", "Description of Task 2"));

        // 2. Создание эпика с двумя подзадачами
        Epic epic1 = manager.createEpic(new Epic("Epic 1", "Description of Epic 1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1.1", "Description 1.1", TaskStatus.NEW, epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask 1.2", "Description 1.2", TaskStatus.NEW, epic1.getId()));

        // Добавление ID подзадач в эпик
        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());

        // 3. Эпик с одной подзадачей
        Epic epic2 = manager.createEpic(new Epic("Epic 2", "Description of Epic 2"));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask 2.1", "Description 2.1", TaskStatus.NEW, epic2.getId()));
        epic2.addSubtaskId(subtask3.getId());

        // 4. Распечатка всех задач, эпиков и подзадач
        System.out.println("=== Tasks ===");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n=== Epics ===");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n=== Subtasks ===");
        for (Subtask sub : manager.getAllEpicSubtasks(epic1.getId())) {
            System.out.println(sub);
        }
        for (Subtask sub : manager.getAllEpicSubtasks(epic2.getId())) {
            System.out.println(sub);
        }

        // 6. Проверка статусов
        System.out.println("\n=== Updated Tasks ===");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n=== Updated Epics ===");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n=== Updated Subtasks ===");
        for (Subtask sub : manager.getAllEpicSubtasks(epic1.getId())) {
            System.out.println(sub);
        }
        for (Subtask sub : manager.getAllEpicSubtasks(epic2.getId())) {
            System.out.println(sub);
        }

        // 7. Удаление одной задачи и одного эпика
        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic1.getId());

        System.out.println("\n=== After Deletion ===");
        System.out.println("Tasks:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nEpics:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nSubtasks of remaining epics:");
        for (Subtask sub : manager.getAllEpicSubtasks(epic2.getId())) {
            System.out.println(sub);
        }
    }
}
