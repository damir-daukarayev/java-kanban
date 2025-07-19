package formatters;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

public class CSVFormatter {
    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    //таск превращает в CSV строку
    public static String toCSVString(Task task) {
        return  task.getId() + "," + task.getType() + "," + task.getName() + "," +
                "" + task.getTaskStatus() + "," + task.getDescription() + ",";
    }

    //эпик превращает в CSV строку
    public static String toCSVString(Epic epic) {
        return  epic.getId() + "," + epic.getType() + "," + epic.getName() + "," +
                "" + epic.getTaskStatus() + "," + epic.getDescription() + ",";
    }

    //сабтаск превращает в CSV строку
    public static String toCSVString(Subtask subtask) {
        return  subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," +
                "" + subtask.getTaskStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    //CSV строку превращаем в таск
    public static Task fromString(String inputString) {
        String[] parts = inputString.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        if (type.equals("Subtask")) {
            // Correctly retrieves epicId
            int epicId = Integer.parseInt(parts[5]);
            Subtask subtask = new Subtask(name, description, taskStatus, epicId);
            subtask.setId(id); // Ensure the ID is set from the file
            return subtask;
        } else if (type.equals("Epic")) {
            Epic epic = new Epic(name, description);
            epic.setTaskStatus(taskStatus);
            epic.setId(id); // Ensure the ID is set from the file
            return epic;
        }

        Task task = new Task(name, description, taskStatus);
        task.setId(id); // Ensure the ID is set from the file
        return task;
    }
}
