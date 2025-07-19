import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager1 = Managers.getDefault();
        Task task1 = manager1.createTask(new Task("Task 1", "Description 1"));
        Task task2 = manager1.createTask(new Task("Task 2", "Description 2"));
        Task task3 = manager1.createTask(new Task("Task 3", "Description 3"));

        Epic epic1 = manager1.createEpic(new Epic("Epic 1", "Description of epic 1"));
        Subtask subtask1 = manager1.createSubtask(new Subtask("Subtask 1",
                "Subtask 1 of Epic 1", TaskStatus.NEW, epic1.getId()));

        Epic epic2 = manager1.createEpic(new Epic("Epic 2", "Description of epic 2"));
        Subtask subtask2 = manager1.createSubtask(new Subtask("Subtask 2",
                "Subtask 2 of Epic 2", TaskStatus.NEW, epic2.getId()));
        Subtask subtask3 = manager1.createSubtask(new Subtask("Subtask 3",
                "Subtask 3 of Epic 2", TaskStatus.NEW, epic2.getId()));

        InMemoryTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("resources/tasksInformation.csv"));

        printAllTasks(manager2);

    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("\n\n\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория:");
        for (Task task : manager.getHistoryManager()) {
            System.out.println(task);
        }
    }
}
