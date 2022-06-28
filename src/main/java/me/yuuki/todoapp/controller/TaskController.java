package me.yuuki.todoapp.controller;


import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskParser;
import me.yuuki.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskController {

    private String getUserId() {
        // TODO 获取用户ID，如果获取不到则抛出一个异常（用Shiro的话应该不会抛出异常）
        return "TEST_00000000001";
    }

    private TaskService taskService;
    
    private TaskParser taskParser;

    @Autowired
    public void setTaskParser(TaskParser taskParser) {
        this.taskParser = taskParser;
    }
    

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/get")
    Result<Task> get(@RequestParam(name = "task_id") long taskId) {
        return Result.ok(taskService.select(getUserId(), taskId));
    }

    @GetMapping("/get/all")
    Result<List<Task>> all() {
        return Result.ok(taskService.selectAllTask(getUserId()));
    }

    @GetMapping("/get/valid")
    Result<List<Task>> valid(@RequestParam(name = "date", required = false) Optional<Date> date) {
        return date.map(d -> taskService.selectValidTask(getUserId(), d))
                .map(Result::ok)
                .orElse(Result.ok(taskService.selectValidTask(getUserId())));
    }

    @GetMapping("/get/unfinished")
    Result<List<Task>> unfinished() {
        return Result.ok(taskService.selectUnfinishedTask(getUserId()));
    }

    @GetMapping("/get/outdated")
    Result<List<Task>> outdated() {
        return Result.ok(taskService.selectOutDatedTask(getUserId()));
    }

    @PostMapping("add")
    Result<Long> add(@RequestParam(name = "task") String taskStr) {
        Task task = ClientException.tryMe(
                () -> taskParser.parse(taskStr),
                e -> "Task 字符串解析失败！" + e.getLocalizedMessage());
        return Result.ok(taskService.addTask(getUserId(), task));
    }

    @PostMapping("done")
    Result<Void> done(@RequestParam Long taskId) {
        taskService.doneTask(getUserId(), taskId);
        return Result.ok(null);
    }

    @PostMapping("del")
    Result<Void> del(@RequestParam Long taskId) {
        taskService.deleteTask(getUserId(), taskId);
        return Result.ok(null);
    }
}
