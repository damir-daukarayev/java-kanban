package formatters;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {
    public static String getHeader() {
        return "id,type,name,status,description,start_time, duration,epic";
    }

    //таск превращает в CSV строку
    public static String toCSVString(Task task) {
        String startTimeStr = (task.getStartTime() != null) ? task.getStartTime().toString() : "null";
        long durationMinutes = (task.getDuration() != null) ? task.getDuration().toMinutes() : 0;

        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getTaskStatus() + "," + task.getDescription() + ","
                + startTimeStr + "," + durationMinutes + ",";
    }

    //эпик превращает в CSV строку
    public static String toCSVString(Epic epic) {
        String startTimeStr = (epic.getStartTime() != null) ? epic.getStartTime().toString() : "null";
        long durationMinutes = (epic.getDuration() != null) ? epic.getDuration().toMinutes() : 0;

        return epic.getId() + "," + epic.getType() + "," + epic.getName() + ","
                + epic.getTaskStatus() + "," + epic.getDescription() + ","
                + startTimeStr + "," + durationMinutes + ",";
    }

    //сабтаск превращает в CSV строку
    public static String toCSVString(Subtask subtask) {
        String startTimeStr = (subtask.getStartTime() != null) ? subtask.getStartTime().toString() : "null";
        long durationMinutes = (subtask.getDuration() != null) ? subtask.getDuration().toMinutes() : 0;

        return subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + ","
                + subtask.getTaskStatus() + "," + subtask.getDescription() + ","
                + startTimeStr + "," + durationMinutes + ","
                + subtask.getEpicId();
    }

    //CSV строку превращаем в таск
    public static Task fromString(String inputString) {
        String[] parts = inputString.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = parts[5].equals("null") ? null : LocalDateTime.parse(parts[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));

        if (type.equals("Subtask")) {
            int epicId = Integer.parseInt(parts[7]);
            Subtask subtask = new Subtask(name, description, taskStatus, epicId, duration, startTime);
            subtask.setId(id);
            return subtask;
        } else if (type.equals("Epic")) {
            Epic epic = new Epic(name, description);
            epic.setTaskStatus(taskStatus);
            epic.setId(id);
            epic.setDuration(duration);
            epic.setStartTime(startTime);
            return epic;
        }

        Task task = new Task(name, description, taskStatus);
        task.setId(id);
        return task;
    }
}
