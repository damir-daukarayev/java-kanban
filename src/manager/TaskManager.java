package manager;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;
    protected HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    protected HashMap<Integer, Epic> mapOfEpics = new HashMap<>();
    protected HashMap<Integer, Subtask> mapOfSubtasks = new HashMap<>();

    //Task methods
    //Получение списка всех задач.
    public ArrayList<Task> getAllTasks() {
        if (mapOfTasks.isEmpty()) {
            System.out.println("No tasks currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }
        return new ArrayList<>(mapOfTasks.values());
    }

    //Удаление всех задач.
    public void clearAllTasks() {
        mapOfTasks.clear();
        System.out.println("All of the tasks were deleted");
        System.out.println("=".repeat(50));
    }

    //Получение по идентификатору.
    public Task getTask(int id) {
        return mapOfTasks.get(id);
    }

    public Task createTask(Task task) {
        //by default all the new tasks have a NEW status
        task.setId(generateId());
        mapOfTasks.put(task.getId(), task);
        System.out.println("Task[" + task.getId() + "] created");
        return task;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public boolean updateTask(int id, Task updatedTask) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.put(id, updatedTask);
        } else {
            System.out.println("There is no such task with such id.");
        }
        return false;
    }

    //Удаление по идентификатору.
    public void deleteTask(int id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
        } else {
            System.out.println("Such a task with such id does not exist.");
        }
    }

    //Epic methods

    //Получение списка всех задач.
    public ArrayList<Epic> getAllEpics() {
        if (mapOfEpics.isEmpty()) {
            System.out.println("No epics currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }
        return new ArrayList<>(mapOfEpics.values());
    }

    //Удаление всех задач.
    //deleting an epic also triggers deleting its subtasks
    public void clearAllEpics() {
        mapOfSubtasks.clear();
        mapOfEpics.clear();
    }

    //Получение по идентификатору.
    public Epic getEpic(int id) {
        return mapOfEpics.get(id);
    }

    //Создание
    public Epic createEpic(Epic epic) {
        mapOfEpics.put(epic.getId(), epic);
        System.out.println("Epic[" + epic.getId() + "] created");
        return epic;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
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

    //Удаление по идентификатору.
    public void deleteEpic(int id) {
        if (mapOfEpics.containsKey(id)) {
            for (int index : mapOfEpics.get(id).getSubtaskIds()) {
                deleteSubtask(index);
            }
            mapOfEpics.remove(id);
        } else {
            System.out.println("Such epic with such id does not exist.");
        }
    }

    //Получение списка всех подзадач определённого эпика.
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
        ArrayList<Integer> retreivedSubtaskIds = mapOfEpics.get(epicId).getSubtaskIds();
        int sizeOfList = retreivedSubtaskIds.size();
        System.out.println("size of subtask list for epic = " + sizeOfList);
        int count_in_progress = 0;
        int count_done = 0;
        for (int index : retreivedSubtaskIds) {
            if (mapOfSubtasks.get(index).getTaskStatus() == TaskStatus.IN_PROGRESS) {
                count_in_progress++;
            } else if (mapOfSubtasks.get(index).getTaskStatus() == TaskStatus.DONE) {
                count_done++;
            }
        }

        if(sizeOfList != 0) {
            if (count_done == sizeOfList) {
                mapOfEpics.get(epicId).setTaskStatus(TaskStatus.DONE);
            } else if (count_in_progress > 0) {
                mapOfEpics.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    //Subtask methods

    //Удаление всех задач
    public void clearAllSubtasks() {
        for (Epic e : mapOfEpics.values()) {
            e.removeAllSubtasks();
            calculateEpicStatus(e.getId());
        }
        mapOfSubtasks.clear();
    }

    // Получение по идентификатору.
    public Subtask getSubtask(int subTaskId) {
        return mapOfSubtasks.get(subTaskId);
    }

    //Создание
    public Subtask createSubtask(Subtask subtask) {
        mapOfSubtasks.put(subtask.getId(), subtask);
        mapOfEpics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        calculateEpicStatus(subtask.getEpicId());
        System.out.println("Subtask[" + subtask.getId() + "] created");
        return subtask;
    }

    //Обновление
    public boolean updateSubtask(int subTaskId, Subtask subtask) {
        if (mapOfSubtasks.containsKey(subTaskId)) {
            if (mapOfSubtasks.get(subTaskId).getEpicId() == subtask.getEpicId()) {
                mapOfSubtasks.put(subTaskId, subtask);
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
    public boolean deleteSubtask(int index) {
        if (mapOfSubtasks.containsKey(index)) {
            int epicId = mapOfSubtasks.get(index).getEpicId();
            mapOfEpics.get(mapOfSubtasks.get(index).getEpicId()).removeSubtaskId(index);
            calculateEpicStatus(epicId);
            mapOfSubtasks.remove(index);
            return true;
        } else {
            System.out.println("There is no subtask with such id");
        }
        return false;
    }

    private static int generateId() {
        return id++;
    }
}