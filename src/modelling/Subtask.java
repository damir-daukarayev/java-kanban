package modelling;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(String name,
                   String description,
                   TaskStatus taskStatus,
                   int epicId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    //  Для детального сохранения Сабтасков
    @Override
    public Task copy() {
        Subtask copy = new Subtask(this.name, this.description, this.taskStatus, this.epicId,
                this.duration, this.startTime);
        copy.setId(this.id);
        return copy;
    }

    @Override
    public boolean setId(int newId) {
        // переделал в boolean, чтобы можно было отслеживать возможность действия
        // при работе с тестами для TaskManager обнаружилось, что невозможно правильно
        // обработать случай, когда id у subtask и epic равны
        // для этого оверрайднул метод и добавил условие
        if (newId == this.epicId) {
            return false;
        }
        this.id = newId;
        return true;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String getType() {
        return "Subtask";
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId +
                '}';
    }
}
