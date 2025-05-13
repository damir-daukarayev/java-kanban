package manager;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskId = 0;
    private int epicId = 0;
    private int subTaskId = 0;
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
        taskId = 0;
    }

    //Получение по идентификатору.
    public Task getTask(int id) {
        if (mapOfTasks.containsKey(id)) {
            return mapOfTasks.get(id);
        }
        return null;
    }

    public Task createTask(Task task) {
        int generatedId = generateTaskId();
        mapOfTasks.put(generatedId, new Task(task.getName(), task.getDescription(), generatedId, TaskStatus.NEW));
        System.out.println("Task[" + generatedId + "] created");
        return task;
    }

    public Task createTask(String name, String description) {
        int generatedId = generateTaskId();
        Task task = new Task(name, description, generatedId, TaskStatus.NEW);
        mapOfTasks.put(generatedId, task);
        System.out.println("Task[" + generatedId + "] created");
        return task;
    }

    public Task createTask(String name, String description, TaskStatus taskStatus) {
        int generatedId = generateTaskId();
        Task task = new Task(name, description, generatedId, taskStatus);
        mapOfTasks.put(generatedId, task);
        System.out.println("Task[" + generatedId + "] created");
        return task;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(int i, Task updatedTask) {
        mapOfTasks.put(i, updatedTask);
        return updatedTask;
    }

    public Task updateTaskName(int id, String name) {
        mapOfTasks.get(id).setName(name);
        return mapOfTasks.get(id);
    }

    public Task updateTaskDescription(int id, String description) {
        mapOfTasks.get(id).setDescription(description);
        return mapOfTasks.get(id);
    }

    public Task updateTaskStatus(int id, TaskStatus taskStatus) {
        mapOfTasks.get(id).setTaskStatus(taskStatus);
        return mapOfTasks.get(id);
    }

    //Удаление по идентификатору.
    public void deleteTask(int id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
        } else {
            System.out.println("Such a task with such id does not exist.");
        }
    }

    public int generateTaskId() {
        return this.taskId++;
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
        for (Epic epic : mapOfEpics.values()) {
            for (int index : epic.getSubtaskIds()) {
                deleteSubtask(epic.getId(), index);
            }
        }
        mapOfEpics.clear();
        epicId = 0;
    }

    //Получение по идентификатору.
    public Epic getEpic(int id) {
        if (mapOfEpics.containsKey(id)) {
            return mapOfEpics.get(id);
        }
        return null;
    }

    //Создание
    public Epic createEpic(Epic epic) {
        int generatedId = generateEpicId();
        mapOfEpics.put(generatedId, new Epic(epic.getName(), epic.getDescription(), generatedId, TaskStatus.NEW));
        System.out.println("Epic[" + generatedId + "] created");
        return epic;
    }

    public Epic createEpic(String name, String description) {
        int generatedId = generateEpicId();
        mapOfEpics.put(generatedId, new Epic(name, description, generatedId, TaskStatus.NEW));
        System.out.println("Epic[" + generatedId + "] created");
        return mapOfEpics.get(generatedId);
    }

    public Epic createEpic(String name, String description, TaskStatus taskStatus) {
        int generatedId = generateEpicId();
        mapOfEpics.put(generatedId, new Epic(name, description, generatedId, taskStatus));
        System.out.println("Epic[" + generatedId + "] created");
        return mapOfEpics.get(generatedId);
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Epic updateEpic(int epicId, Epic epic) {
        if (mapOfEpics.containsKey(epicId)) {
            //first delete the subtasks of the epic and the epic itself
            deleteEpic(epicId);
            mapOfEpics.put(epicId, epic);
            return mapOfEpics.get(epicId);
        } else {
            System.out.println("There is no such Epic with such id.");
        }
        return null;
    }

    //Удаление по идентификатору.
    public void deleteEpic(int id) {
        if (mapOfEpics.containsKey(id)) {
            for (int index : mapOfEpics.get(id).getSubtaskIds()) {
                deleteSubtask(id, index);
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

    public int generateEpicId() {
        return epicId++;
    }

    public void calculateEpicStatus(int epicId) {
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

        if (count_done == sizeOfList) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.DONE);
        } else if (count_in_progress > 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    //Subtask methods

    //Удаление всех задач
    public void clearAllSubtasks() {
        mapOfSubtasks.clear();
    }

    // Получение по идентификатору.
    public Subtask getSubtask(int subTaskId) {
        if (mapOfSubtasks.containsKey(subTaskId)) {
            return mapOfSubtasks.get(subTaskId);
        }
        return null;
    }

    //Создание
    public Subtask createSubtask(Subtask subtask, int parentEpicId) {
        int generatedId = generateSubtaskId();
        mapOfSubtasks.put(generatedId, new Subtask(subtask.getName(), subtask.getDescription(), generatedId, TaskStatus.NEW, parentEpicId));
        System.out.println("Subtask[" + generatedId + "] created");
        return subtask;
    }

    public Subtask createSubtask(String name, String description, int parentEpicId) {
        int generatedId = generateSubtaskId();
        mapOfSubtasks.put(generatedId, new Subtask(name, description, generatedId, TaskStatus.NEW, parentEpicId));
        System.out.println("Subtask[" + generatedId + "] created");
        return mapOfSubtasks.get(generatedId);
    }

    public Subtask createSubtask(String name, String description, TaskStatus taskStatus, int parentEpicId) {
        int generatedId = generateSubtaskId();
        mapOfSubtasks.put(generatedId, new Subtask(name, description, generatedId, taskStatus, parentEpicId));
        System.out.println("Subtask[" + generatedId + "] created");
        return mapOfSubtasks.get(generatedId);
    }

    //Обновление
    public Subtask updateSubtask(int subTaskId, Subtask subtask) {
        if (mapOfSubtasks.containsKey(subTaskId)) {
            mapOfSubtasks.put(subTaskId, subtask);
            return mapOfSubtasks.get(subTaskId);
        } else {
            System.out.println("There is no such Subtask with such id.");
        }
        return null;
    }

    //Удаление по идентефикатору
    public void deleteSubtask(int epicId, int index) {
        mapOfSubtasks.remove(index);
        mapOfEpics.get(epicId).removeSubtaskId(index);
    }

    public void updateSubtaskStatus(int subTaskId, TaskStatus taskStatus) {
        if (mapOfSubtasks.containsKey(subTaskId)) {
            mapOfSubtasks.get(subTaskId).setTaskStatus(taskStatus);
            calculateEpicStatus(mapOfSubtasks.get(subTaskId).getEpicId());
        } else {
            System.out.println("Status can't be updated, since there is no such a subtask with such id.");
        }
    }

    public int generateSubtaskId() {
        return subTaskId++;
    }
}