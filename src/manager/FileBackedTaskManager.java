package manager;

import exceptions.ManagerSaveException;
import formatters.CSVFormatter;
import modelling.Epic;
import modelling.Subtask;
import modelling.Task;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

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

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                continue;
            }

            Task task = CSVFormatter.fromString(line);
            int currentId = task.getId();

            if (currentId > maxId) {
                maxId = currentId;
            }

            switch (task.getType()) {
                case "Task":
                    taskManager.mapOfTasks.put(currentId, task);
                    break;
                case "Epic":
                    taskManager.mapOfEpics.put(currentId, (Epic) task);
                    break;
                case "Subtask":
                    Subtask subtask = (Subtask) task;
                    taskManager.mapOfSubtasks.put(currentId, subtask);
                    Epic parentEpic = taskManager.mapOfEpics.get(subtask.getEpicId());
                    if (parentEpic != null) {
                        parentEpic.addSubtaskId(currentId);
                    } else {
                        System.err.println("Ошибка: Subtask " + currentId + " относится к несуществующему эпику " + subtask.getEpicId());
                    }
                    break;
            }
        }

        taskManager.id = maxId + 1;

        return taskManager;
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

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // 1. Заголовок
            bw.write(CSVFormatter.getHeader());
            bw.newLine();

            // 2. Тело
            // 2.1. Tasks
            for (Task task : getAllTasks()) {
                bw.write(CSVFormatter.toCSVString(task));
                bw.newLine();
            }

            // 2.2. Epics
            if (!mapOfEpics.isEmpty()) {
                for (Epic epic : getAllEpics()) {
                    bw.write(CSVFormatter.toCSVString(epic));
                    bw.newLine();
                }
            }

            // 2.3. Subtasks
            if (!mapOfSubtasks.isEmpty()) {
                for (Subtask subtask : getAllSubtasks()) {
                    bw.write(CSVFormatter.toCSVString(subtask));
                    bw.newLine();
                }
            }

        } catch (IOException exception) {
            System.err.println("Ошибка при сохранении состояния: " + exception.getMessage());
            throw new ManagerSaveException("Не удалось сохранить", exception);
        }
    }
}
