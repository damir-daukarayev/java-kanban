package manager;

import modelling.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    protected Map<Integer, Node> nodes = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodes.remove(id);
        if (node == null) return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            first = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            last = prev;
        }
    }

    public void linkLast(Task task) {
        if (task == null) return;

        Node node = new Node(task.copy(), last, null);

        if (first == null) {
            first = node;
        }

        if (last != null) {
            last.next = node;
        }

        last = node;

        nodes.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node curr = first;

        while (curr != null) {
            historyList.add(curr.value);
            curr = curr.next;
        }

        return historyList;
    }

    public static class Node {
        private Task value;
        private Node prev;
        private Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public Task getValue() {
            return value;
        }

        public void setValue(Task value) {
            this.value = value;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
