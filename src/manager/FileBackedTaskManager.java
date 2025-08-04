package manager;

import exceptions.ManagerSaveException;
import formatters.CSVFormatter;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean created = parent.mkdirs();
            if (!created) {
                System.err.println("Не удалось создать директорию: " + parent.getPath());
            }
        }

        // Создаём сам файл, если он не существует
        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                    System.err.println("Не удалось создать файл: " + file.getPath());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла: " + file.getPath(), e);
        }
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public boolean updateEpic(int epicId, String name, String description) {
        boolean updated = super.updateEpic(epicId, name, description);
        save();
        return updated;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean updated = super.updateEpic(epic);
        save();
        return updated;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean updated = super.updateSubtask(subtask);
        save();
        return updated;
    }

    @Override
    public boolean deleteSubtask(int index) {
        boolean deleted = super.deleteSubtask(index);
        save();
        return deleted;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public boolean updateTask(int id, Task updatedTask) {
        boolean updated = super.updateTask(id, updatedTask);
        save();
        return updated;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        String fileContent;
        int maxId = -1;

        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + file.getName(), exception);
        }

        String[] lines = fileContent.split(System.lineSeparator());

        // Filter out empty lines and the header
        List<String> taskLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (!lines[i].trim().isEmpty()) {
                taskLines.add(lines[i]);
            }
        }

        for (String line : taskLines) {
            Task task = CSVFormatter.fromString(line);
            int currentId = task.getId();

            if (currentId > maxId) {
                maxId = currentId;
            }

            switch (task.getType()) {
                case "Task":
                    taskManager.addLoadedTask(task); // Use addLoadedTask to also add to prioritizedTasks
                    break;
                case "Epic":
                    taskManager.addLoadedEpic((Epic) task);
                    break;
                // Subtasks handled in the second pass after epics are loaded
            }
        }

        for (String line : taskLines) {
            Task task = CSVFormatter.fromString(line); // Parse again
            if (task.getType().equals("Subtask")) {
                Subtask subtask = (Subtask) task;
                Epic parentEpic = taskManager.mapOfEpics.get(subtask.getEpicId());
                if (parentEpic != null) {
                    // Ensure the subtask is added to the InMemoryTaskManager via its methods
                    // to correctly update prioritizedTasks and epic calculations.
                    // Re-create the subtask using manager's method to ensure proper setup
                    // (though for loading, directly adding to map is fine if calculations are explicit).
                    // For simplicity and to reuse logic, let's call addLoadedSubtask
                    taskManager.addLoadedSubtask(subtask);
                    parentEpic.addSubtaskId(subtask.getId()); // Ensure epic has subtask ID
                } else {
                    System.err.println("Ошибка: Subtask " + task.getId() + " относится к несуществующему эпику " + subtask.getEpicId());
                }
            }
        }

        for (Epic epic : taskManager.mapOfEpics.values()) {
            taskManager.calculateEpicStatus(epic.getId());
            taskManager.calculateEpicTime(epic.getId());
        }

        taskManager.setId(maxId + 1); // Set the next available ID

        return taskManager;
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // 1. Заголовок
            bw.write(CSVFormatter.getHeader());
            bw.newLine();

            // 2. Тело
            // 2.1. Tasks
            for (Task task : mapOfTasks.values()) {
                bw.write(CSVFormatter.toCSVString(task));
                bw.newLine();
            }

            // 2.2. Epics
            for (Epic epic : mapOfEpics.values()) {
                bw.write(CSVFormatter.toCSVString(epic));
                bw.newLine();
            }

            // 2.3. Subtasks
            for (Subtask subtask : mapOfSubtasks.values()) {
                bw.write(CSVFormatter.toCSVString(subtask));
                bw.newLine();
            }

        } catch (IOException exception) {
            System.err.println("Ошибка при сохранении состояния: " + exception.getMessage());
            throw new ManagerSaveException("Не удалось сохранить", exception);
        }
    }
}
