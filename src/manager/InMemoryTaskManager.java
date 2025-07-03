package manager;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    protected Map<Integer, Task> mapOfTasks = new HashMap<>();
    protected Map<Integer, Epic> mapOfEpics = new HashMap<>();
    protected Map<Integer, Subtask> mapOfSubtasks = new HashMap<>();

    private final HistoryManager history = Managers.getDefaultHistory();

    //Task methods
    //Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() {
        if (mapOfTasks.isEmpty()) {
            System.out.println("No tasks currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }

        for (Map.Entry<Integer, Task> e : mapOfTasks.entrySet()) {
            history.add(e.getValue());
        }

        return new ArrayList<>(mapOfTasks.values());
    }

    //Удаление всех задач.
    @Override
    public void clearAllTasks() {
        // Удалить все из истории при массовом удалении задач
        for (Map.Entry<Integer, Task> e : mapOfTasks.entrySet()) {
            history.remove(e.getKey());
        }
        mapOfTasks.clear();
        System.out.println("All of the tasks were deleted");
        System.out.println("=".repeat(50));
    }

    //Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        Task task = mapOfTasks.get(id);
        if (task != null) {
            history.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        mapOfTasks.put(task.getId(), task);
        System.out.println("Task[" + task.getId() + "] created");
        return task;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateTask(int id, Task updatedTask) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.put(id, updatedTask);
        } else {
            System.out.println("There is no such task with such id.");
        }
        return false;
    }

    //Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
            history.remove(id);
        } else {
            System.out.println("Such a task with such id does not exist.");
        }
    }

    //Epic methods

    //Получение списка всех задач.
    @Override
    public ArrayList<Epic> getAllEpics() {
        if (mapOfEpics.isEmpty()) {
            System.out.println("No pics currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }

        for (Map.Entry<Integer, Epic> e : mapOfEpics.entrySet()) {
            history.add(e.getValue());
        }

        return new ArrayList<>(mapOfEpics.values());
    }

    //Удаление всех задач.
    //deleting an epic also triggers deleting its subtasks
    @Override
    public void clearAllEpics() {
        // Удалить все из истории при массовом удалении задач
        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
            history.remove(e.getKey());
        }
        for (Map.Entry<Integer, Epic> e : mapOfEpics.entrySet()) {
            history.remove(e.getKey());
        }

        mapOfSubtasks.clear();
        mapOfEpics.clear();
    }

    //Получение по идентификатору.
    @Override
    public Epic getEpic(int id) {
        Epic epic = mapOfEpics.get(id);
        if (epic != null) {
            history.add(epic);
        }
        return epic;
    }

    //Создание
    @Override
    public Epic createEpic(Epic epic) {
        //в соответсвии с переделанным setId
        epic.setId(generateId());
        mapOfEpics.put(epic.getId(), epic);
        System.out.println("Epic[" + epic.getId() + "] created");
        return epic;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateEpic(int epicId, String name, String description) {
        if (mapOfEpics.containsKey(epicId)) {
            //first delete the subtasks of the epic and the epic itself
            mapOfEpics.get(epicId).setName(name);
            mapOfEpics.get(epicId).setDescription(description);
            return true;
        } else {
            System.out.println("There is no such Epic with such id.");
        }
        return false;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateEpic(Epic epic) {
        if (mapOfEpics.containsKey(epic.getId())) {
            //first delete the subtasks of the epic and the epic itself
            mapOfEpics.get(epic.getId()).setName(epic.getName());
            mapOfEpics.get(epic.getId()).setDescription(epic.getName());
            return true;
        } else {
            System.out.println("There is no such Epic with such id.");
        }
        return false;
    }

    //Удаление по идентификатору.
    @Override
    public void deleteEpic(int id) {
        if (mapOfEpics.containsKey(id)) {
            for (int index : mapOfEpics.get(id).getSubtaskIds()) {
                deleteSubtask(index);
            }
            mapOfEpics.remove(id);
            history.remove(id);
        } else {
            System.out.println("Such epic with such id does not exist.");
        }
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        if (mapOfEpics.containsKey(epicId)) {
            for (int index : mapOfEpics.get(epicId).getSubtaskIds()) {
                allSubtasks.add(getSubtask(index));
            }
        }
        return allSubtasks;
    }

    private void calculateEpicStatus(int epicId) {
        ArrayList<Integer> subtaskIds = mapOfEpics.get(epicId).getSubtaskIds();
        int sizeOfList = subtaskIds.size();
        System.out.println("size of subtask list for epic = " + sizeOfList);

        if (sizeOfList == 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
            return;
        }

        int countInProgress = 0;
        int countDone = 0;
        int validSubtaskCount = 0;

        for (int index : subtaskIds) {
            Subtask subtask = mapOfSubtasks.get(index);
            if (subtask == null) {
                continue; // Skip deleted or invalid subtasks
            }
            validSubtaskCount++;

            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                countInProgress++;
            } else if (subtask.getTaskStatus() == TaskStatus.DONE) {
                countDone++;
            }
        }

        if (validSubtaskCount == 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
        } else if (countDone == validSubtaskCount) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.DONE);
        } else if (countInProgress > 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        } else {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
        }
    }


    //Subtask methods

    //Удаление всех задач
    @Override
    public void clearAllSubtasks() {
        for (Epic e : mapOfEpics.values()) {
            e.removeAllSubtasks();
            calculateEpicStatus(e.getId());
        }
        // Удалить все из истории при массовом удалении задач
        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
            history.remove(e.getKey());
        }
        mapOfSubtasks.clear();
    }

    // Получение по идентификатору.
    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask subtask = mapOfSubtasks.get(subTaskId);
        if (subtask != null) {
            history.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        if (mapOfSubtasks.isEmpty()) {
            System.out.println("No sub-tasks currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }

        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
            history.add(e.getValue());
        }

        return new ArrayList<>(mapOfSubtasks.values());
    }

    //Создание
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (mapOfEpics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            mapOfSubtasks.put(subtask.getId(), subtask);
            mapOfEpics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            System.out.println("Subtask[" + subtask.getId() + "] created");
            calculateEpicStatus(subtask.getEpicId());
            return subtask;
        } else {
            System.out.println("There is no such epic for a subtask to be associated to.");
        }
        return null;
    }

    //Обновление
    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (mapOfSubtasks.containsKey(subtask.getId())) {
            if (mapOfSubtasks.get(subtask.getId()).getEpicId() == subtask.getEpicId()) {
                mapOfSubtasks.put(subtask.getId(), subtask);
                calculateEpicStatus(subtask.getEpicId());
                return true;
            } else {
                System.out.println("The modified subtask's epic id does not match the existing subtask's epic id");
            }
        } else {
            System.out.println("No such subtask with such subtask id exists.");
        }
        return false;
    }

    //Удаление по идентефикатору
    @Override
    public boolean deleteSubtask(int index) {
        if (mapOfSubtasks.containsKey(index)) {
            int epicId = mapOfSubtasks.get(index).getEpicId();
            mapOfEpics.get(mapOfSubtasks.get(index).getEpicId()).removeSubtaskId(index);
            mapOfSubtasks.remove(index);
            history.remove(id);
            calculateEpicStatus(epicId);
            return true;
        } else {
            System.out.println("There is no subtask with such id");
        }
        return false;
    }

    private int generateId() {
        return this.id++;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}