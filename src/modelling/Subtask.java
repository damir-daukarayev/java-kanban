package modelling;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    //  Для детального сохранения Сабтасков
    @Override
    public Task copy() {
        Subtask copy = new Subtask(this.name, this.description, this.taskStatus, this.epicId);
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
}
