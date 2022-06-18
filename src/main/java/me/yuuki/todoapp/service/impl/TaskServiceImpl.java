package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.service.TaskService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Override
    public Task select(String userId, long taskId) {
        return null;
    }

    @Override
    public List<Task> selectAllTask(String userId) {
        return null;
    }

    @Override
    public List<Task> selectWorkingTask(String userId) {
        return null;
    }

    @Override
    public List<Task> selectValidTask(String userId) {
        return null;
    }

    @Override
    public List<Task> selectOutDatedTask(String userId) {
        return null;
    }

    @Override
    public List<Task> selectFutureTask(String userId) {
        return null;
    }

    @Override
    public List<Task> selectValidTask(String userId, Date date) {
        return null;
    }

    @Override
    public List<Task> selectDoneTask(String userId) {
        return null;
    }

    @Override
    public long addTask(Task task) {
        return 0;
    }

    @Override
    public void deleteTask(String userId, long taskId) {

    }

    @Override
    public void doneTask(String userId, long taskId) {

    }
}
