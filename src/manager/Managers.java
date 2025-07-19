package manager;

import java.io.File;

public class Managers {
    //private, so no one has access to it.
    private Managers() {

    }

    public static InMemoryTaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/tasksInformation.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
