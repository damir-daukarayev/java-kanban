package modelling;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
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

    // Для детального сохранения эпика
    @Override
    public Task copy() {
        Epic copy = new Epic(this.name, this.description);
        copy.setId(this.id);
        copy.setTaskStatus(this.taskStatus);
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
                '}';
    }
}
