package manager;

import modelling.Epic;
import modelling.Subtask;
import modelling.Task;
import modelling.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> mapOfTasks = new HashMap<>();
    protected Map<Integer, Epic> mapOfEpics = new HashMap<>();
    protected Map<Integer, Subtask> mapOfSubtasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(LocalDateTime::compareTo)));

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    // And possibly a method to directly add a task with its ID
    public void addLoadedTask(Task task) {
        mapOfTasks.put(task.getId(), task);
        updatePrioritizedTask(task);
    }

    public void addLoadedEpic(Epic epic) {
        mapOfEpics.put(epic.getId(), epic);
    }

    public void addLoadedSubtask(Subtask subtask) {
        mapOfSubtasks.put(subtask.getId(), subtask);
        updatePrioritizedTask(subtask);
    }

    //Task methods
    //Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() {
        if (mapOfTasks.isEmpty()) {
            System.out.println("No tasks currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }

//        for (Map.Entry<Integer, Task> e : mapOfTasks.entrySet()) {
//            historyManager.add(e.getValue());
//        }

        mapOfTasks.values().stream().forEach(historyManager::add);

        return new ArrayList<>(mapOfTasks.values());
    }

    //Удаление всех задач.
    @Override
    public void clearAllTasks() {
        // Удалить все из истории при массовом удалении задач
//        for (Map.Entry<Integer, Task> e : mapOfTasks.entrySet()) {
//            historyManager.remove(e.getKey());
//        }

        mapOfTasks.keySet().stream().forEach(historyManager::remove);

        mapOfTasks.clear();
        prioritizedTasks.clear();
        System.out.println("All of the tasks were deleted");
        System.out.println("=".repeat(50));
    }

    //Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        Task task = mapOfTasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (task.getStartTime() != null && isTaskOverlappingWithExisting(task)) {
            System.out.println("Таски пересекаются");
            return null;
        }

        task.setId(generateId());
        mapOfTasks.put(task.getId(), task);
        updatePrioritizedTask(task);
        System.out.println("Task[" + task.getId() + "] created");
        return task;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateTask(int id, Task updatedTask) {
        if (!mapOfTasks.containsKey(id)) {
            System.out.println("There is no such task with such id.");
            return false;
        }
        updatedTask.setId(id);
        if (updatedTask.getStartTime() != null && isTaskOverlappingWithExisting(updatedTask)) {
            System.out.println("Таска пересекается с другими.");
            return false;
        }

        mapOfTasks.put(id, updatedTask);
        updatePrioritizedTask(updatedTask);
        return true;
    }

    //Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        if (mapOfTasks.containsKey(id)) {
            updatePrioritizedTask(mapOfTasks.get(id));
            mapOfTasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Such a task with such id does not exist.");
        }
    }

    //Epic methods

    //Получение списка всех задач.
    @Override
    public ArrayList<Epic> getAllEpics() {
        if (mapOfEpics.isEmpty()) {
            System.out.println("No epics currently.");
            System.out.println("=".repeat(50));
            return new ArrayList<>();
        }

//        for (Map.Entry<Integer, Epic> e : mapOfEpics.entrySet()) {
//            historyManager.add(e.getValue());
//        }

        mapOfEpics.values().stream().forEach(historyManager::add);

        return new ArrayList<>(mapOfEpics.values());
    }

    //Удаление всех задач.
    //deleting an epic also triggers deleting its subtasks
    @Override
    public void clearAllEpics() {
        // Удалить все из истории при массовом удалении задач
//        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
//            historyManager.remove(e.getKey());
//        }

        mapOfSubtasks.keySet().stream().forEach(historyManager::remove);

//        for (Map.Entry<Integer, Epic> e : mapOfEpics.entrySet()) {
//            historyManager.remove(e.getKey());
//        }

        mapOfEpics.keySet().stream().forEach(historyManager::remove);

        mapOfSubtasks.clear();
        mapOfEpics.clear();
    }

    //Получение по идентификатору.
    @Override
    public Epic getEpic(int id) {
        Epic epic = mapOfEpics.get(id);
        if (epic != null) {
            historyManager.add(epic);
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
            historyManager.remove(id);
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

    protected void calculateEpicStatus(int epicId) {
        ArrayList<Integer> subtaskIds = mapOfEpics.get(epicId).getSubtaskIds();
        int sizeOfList = subtaskIds.size();
        System.out.println("size of subtask list for epic = " + sizeOfList);

        if (sizeOfList == 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
            return;
        }

        long validSubtaskCount = 0;
        long countNew = 0;
        long countInProgress = 0;
        long countDone = 0;

        validSubtaskCount = subtaskIds.stream()
                .map(mapOfSubtasks::get)
                .filter(Objects::nonNull)
                .count();

        countNew = subtaskIds.stream()
                .map(mapOfSubtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getTaskStatus() == TaskStatus.NEW)
                .count();

        countInProgress = subtaskIds.stream()
                .map(mapOfSubtasks::get)
                .filter(Objects::nonNull)
                .filter(subTask -> subTask.getTaskStatus() == TaskStatus.IN_PROGRESS).
                count();

        countDone = subtaskIds.stream()
                .map(mapOfSubtasks::get)
                .filter(Objects::nonNull)
                .filter(subTask -> subTask.getTaskStatus() == TaskStatus.DONE)
                .count();

        if (validSubtaskCount == 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
        } else if ((countNew > 0 && countNew != validSubtaskCount) || (countDone > 0 && countDone != validSubtaskCount)) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (countDone == validSubtaskCount) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.DONE);
        } else if (countInProgress > 0) {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        } else {
            mapOfEpics.get(epicId).setTaskStatus(TaskStatus.NEW);
        }
    }

    protected void calculateEpicTime(int epicId) {
        Epic epic = mapOfEpics.get(epicId);
        if (epic == null) {
            return;
        }

        ArrayList<Integer> subtaskIDs = epic.getSubtaskIds();

        if (subtaskIDs.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        Duration totalDuration = Duration.ZERO;

        for (int id : subtaskIDs) {
            Subtask subtask = mapOfSubtasks.get(id);
            if (subtask != null && subtask.getStartTime() != null && subtask.getDuration() != null) {
                LocalDateTime currStartTime = subtask.getStartTime();
                LocalDateTime currEndTime = subtask.getEndTime();

                if (earliestStartTime == null || currStartTime.isBefore(earliestStartTime)) {
                    earliestStartTime = currStartTime;
                }

                if (latestEndTime == null || currEndTime.isAfter(latestEndTime)) {
                    latestEndTime = currEndTime;
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);
        epic.setDuration(totalDuration);
    }

    //Subtask methods

    //Удаление всех задач
    @Override
    public void clearAllSubtasks() {
        for (Epic e : mapOfEpics.values()) {
            e.removeAllSubtasks();
            calculateEpicStatus(e.getId());
            calculateEpicTime(e.getId());
        }

        prioritizedTasks.removeIf(task -> task.getType().equals("Subtask"));
        // Удалить все из истории при массовом удалении задач
//        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
//            historyManager.remove(e.getKey());
//        }

        mapOfSubtasks.keySet().stream().forEach(historyManager::remove);


        mapOfSubtasks.clear();
    }

    // Получение по идентификатору.
    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask subtask = mapOfSubtasks.get(subTaskId);
        if (subtask != null) {
            historyManager.add(subtask);
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

//        for (Map.Entry<Integer, Subtask> e : mapOfSubtasks.entrySet()) {
//            historyManager.add(e.getValue());
//        }

        mapOfSubtasks.values().stream().forEach(historyManager::add);

        return new ArrayList<>(mapOfSubtasks.values());
    }

    //Создание
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (mapOfEpics.containsKey(subtask.getEpicId())) {
            if (subtask.getStartTime() != null && isTaskOverlappingWithExisting(subtask)) {
                System.out.println("Сабтаск пересекается с другими тасками.");
                return null;
            }
            subtask.setId(generateId());
            mapOfSubtasks.put(subtask.getId(), subtask);
            mapOfEpics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            updatePrioritizedTask(subtask);
            System.out.println("Subtask[" + subtask.getId() + "] created");
            calculateEpicStatus(subtask.getEpicId());
            calculateEpicTime(subtask.getEpicId());
            return subtask;
        } else {
            System.out.println("There is no such epic for a subtask to be associated to.");
        }
        return null;
    }

    //Обновление
    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (!mapOfSubtasks.containsKey(subtask.getId())) {
            System.out.println("No such subtask with such subtask id exists.");
            return false;
        }
        if (mapOfSubtasks.get(subtask.getId()).getEpicId() != subtask.getEpicId()) {
            System.out.println("ID не совпадают.");
            return false;
        }

        if (subtask.getStartTime() != null && isTaskOverlappingWithExisting(subtask)) {
            System.out.println("Обновленный сабтаск пересекается с другими тасками.");
            return false;
        }

        mapOfSubtasks.put(subtask.getId(), subtask);
        updatePrioritizedTask(subtask);
        calculateEpicStatus(subtask.getEpicId());
        calculateEpicTime(subtask.getEpicId());
        return true;
    }

    //Удаление по идентефикатору
    @Override
    public boolean deleteSubtask(int index) {
        if (mapOfSubtasks.containsKey(index)) {
            int epicId = mapOfSubtasks.get(index).getEpicId();
            removePrioritizedTask(mapOfSubtasks.get(index));
            mapOfEpics.get(mapOfSubtasks.get(index).getEpicId()).removeSubtaskId(index);
            mapOfSubtasks.remove(index);
            historyManager.remove(index);
            calculateEpicStatus(epicId);
            calculateEpicTime(epicId);
            return true;
        } else {
            System.out.println("There is no subtask with such id");
        }
        return false;
    }

    protected void setId(int id) {
        this.id = id;
    }

    private int generateId() {
        return this.id++;
    }

    protected void updatePrioritizedTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        } else {
            prioritizedTasks.remove(task);
        }
    }

    protected void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected boolean areTasksOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }

        // (start1 < end2) AND (end1 > start2)
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task1.getEndTime().isAfter(task2.getStartTime());
    }

    protected boolean isTaskOverlappingWithExisting(Task newTask) {
        int newTaskId = newTask.getId();

//        for (Task existingTask : prioritizedTasks) {
//            if (existingTask.getId() == newTaskId) {
//                continue;
//            }
//            if (areTasksOverlapping(newTask, existingTask)) {
//                return true;
//            }
//        }

        List<Task> tasksToCheck = prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getStartTime() != null && existingTask.getDuration() != null)
                .filter(existingTask -> existingTask.getId() != newTaskId)
                .toList();

        return tasksToCheck.stream()
                .anyMatch(existingTask -> areTasksOverlapping(existingTask, newTask));
    }

    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }
}