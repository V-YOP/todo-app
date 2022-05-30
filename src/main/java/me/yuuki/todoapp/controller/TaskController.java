package me.yuuki.todoapp.controller;

import me.yuuki.todoapp.dao.TaskDao;
import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.util.SequenceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO 当前业务逻辑先写在Controller，看之后有无必要定义Service
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    private static final String userId = "yuuki";

    private SequenceUtil sequenceUtil;
    private TaskDao taskDao;

    @Autowired
    public void setSequenceUtil(SequenceUtil sequenceUtil) {
        this.sequenceUtil = sequenceUtil;
    }

    @Autowired
    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @PostMapping("add")
    Result<Long> addTask(@RequestParam("task") String taskStr) {
        long taskId = sequenceUtil.getSeq("task");
        taskDao.addTask(userId, Task.parse(taskStr, taskId));
        return Result.ok(taskId, "创建 Task 成功");
    }

    @GetMapping("list")
    Result<List<Task>> listAll() {
        return Result.ok(taskDao.selectAllTask(userId));
    }

    @GetMapping("list/working")
    Result<List<Task>> listWorkingTask() {
        return Result.ok(taskDao.selectWorkingTask(userId));
    }

    @GetMapping("list/done")
    Result<List<Task>> listDoneTask() {
        return Result.ok(taskDao.selectDoneTask(userId));
    }

    @PostMapping("delete")
    Result<Boolean> deleteTask(@RequestParam Long taskId) {
        return Result.ok(taskDao.deleteTask(userId, taskId));
    }

    @PostMapping("done")
    Result<Void> doneTask(@RequestParam Long taskId) {
        taskDao.doneTask(userId, taskId);
        return Result.ok(null);
    }

    @GetMapping("strs")
    String strs() {
        return taskDao.selectAllTask(userId).stream()
                .map(task -> task.getId() + " " + task).collect(Collectors.joining("\n"));
    }


}
