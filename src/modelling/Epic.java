package modelling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.startTime = null;
        this.endTime = null;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime; // Return the calculated endTime
    }

    // Для детального сохранения эпика
    @Override
    public Task copy() {
        Epic copy = new Epic(this.name, this.description);
        copy.setId(this.id);
        copy.setTaskStatus(this.taskStatus);
        copy.setDuration(this.duration);
        copy.setStartTime(this.startTime);
        copy.setEndTime(this.endTime);
        for (int subId : this.getSubtaskIds()) {
            copy.addSubtaskId(subId);
        }
        return copy;
    }

    public void removeAllSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public String getType() {
        return "Epic";
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
