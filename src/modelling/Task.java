package modelling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus taskStatus;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    public Task(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        // переделал в boolean, чтобы можно было отслеживать возможность действия
        // при работе с тестами для TaskManager обнаружилось, что невозможно правильно
        // обработать случай, когда id у subtask и epic равны
        this.id = id;
        return true;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getType() {
        return "Task";
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    // Для правильного сохранения тасков в истории
    // таким образом удастся детально сохранять таск
    public Task copy() {
        Task copy = new Task(this.name, this.description, this.taskStatus, this.duration, this.startTime);
        copy.setId(this.id);
        return copy;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;

        return this.startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") + // Convert to minutes for display
                ", startTime=" + (startTime != null ? startTime : "null") +
                ", endTime=" + (getEndTime() != null ? getEndTime() : "null") +
                '}';
    }
}
