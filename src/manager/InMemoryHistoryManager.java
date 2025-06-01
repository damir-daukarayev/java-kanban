package manager;

import modelling.Task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT = 10;
    private final Deque<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (history.size() == LIMIT) {
            history.pollFirst();
        }
        history.addLast(task.copy());
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
