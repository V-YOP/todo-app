package me.yuuki.todoapp.dao;

import me.yuuki.todoapp.model.Task;

import java.util.List;

public interface TaskDao {

    List<Task> selectAllTask(String userId);

    List<Task> selectWorkingTask(String userId);

    List<Task> selectDoneTask(String userId);

    boolean addTask(String userId, Task task);

    boolean deleteTask(String userId, long taskId);

    void doneTask(String userId, long taskId);
}
