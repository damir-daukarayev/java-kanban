package manager;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;

import java.util.List;

public interface TaskManager {
    //Task methods
    //Получение списка всех задач.
    List<Task> getAllTasks();

    //Удаление всех задач.
    void clearAllTasks();

    //Получение по идентификатору.
    Task getTask(int id);

    Task createTask(Task task);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    boolean updateTask(int id, Task updatedTask);

    //Удаление по идентификатору.
    void deleteTask(int id);

    //Получение списка всех задач.
    List<Epic> getAllEpics();

    //Удаление всех задач.
    //deleting an epic also triggers deleting its subtasks
    void clearAllEpics();

    //Получение по идентификатору.
    Epic getEpic(int id);

    //Создание
    Epic createEpic(Epic epic);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    boolean updateEpic(int epicId, String name, String description);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    boolean updateEpic(Epic epic);

    //Удаление по идентификатору.
    void deleteEpic(int id);

    //Получение списка всех подзадач определённого эпика.
    List<Subtask> getAllEpicSubtasks(int epicId);

    //Удаление всех задач
    void clearAllSubtasks();

    // Получение по идентификатору.
    Subtask getSubtask(int subTaskId);

    List<Subtask> getAllSubtasks();

    //Создание
    Subtask createSubtask(Subtask subtask);

    //Обновление
    boolean updateSubtask(Subtask oldSubtask, Subtask updatedSubtask);

    //Удаление по идентефикатору
    boolean deleteSubtask(int index);

    //Получить историю тасков, к которым мы получали доступ
    List<Task> getHistoryManager();

    List<Task> getPrioritizedTasks();
}
