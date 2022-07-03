package me.yuuki.todoapp.controller;


import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskParser;
import me.yuuki.todoapp.service.TaskService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskController {

    private String getUserId() {
        return Optional.ofNullable((String) SecurityUtils.getSubject().getPrincipal())
                .orElseThrow(() -> new RuntimeException("该接口需要添加鉴权！"));
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
    @RequiresUser
    Result<Task> get(@RequestParam(name = "task_id") long taskId) {
        return Result.ok(taskService.select(getUserId(), taskId));
    }

    @GetMapping("/get/all")
    @RequiresUser
    Result<List<Task>> all() {
        return Result.ok(taskService.selectAllTask(getUserId()));
    }

    @GetMapping("/get/valid")
    @RequiresUser
    Result<List<Task>> valid(@RequestParam(name = "date", required = false) Optional<Date> date) {
        return date.map(d -> taskService.selectValidTask(getUserId(), d))
                .map(Result::ok)
                .orElse(Result.ok(taskService.selectValidTask(getUserId())));
    }

    @GetMapping("/get/unfinished")
    @RequiresUser
    Result<List<Task>> unfinished() {
        return Result.ok(taskService.selectUnfinishedTask(getUserId()));
    }

    @GetMapping("/get/outdated")
    @RequiresUser
    Result<List<Task>> outdated() {
        return Result.ok(taskService.selectOutDatedTask(getUserId()));
    }

    @PostMapping("add")
    @RequiresUser
    Result<Long> add(@RequestParam(name = "task") String taskStr) {
        Task task = ClientException.tryMe(
                () -> taskParser.parse(taskStr),
                e -> "Task 字符串解析失败！" + e.getLocalizedMessage());
        return Result.ok(taskService.addTask(getUserId(), task));
    }

    @PostMapping("done")
    @RequiresUser
    Result<Void> done(@RequestParam Long taskId) {
        taskService.doneTask(getUserId(), taskId);
        return Result.ok(null);
    }

    @PostMapping("del")
    @RequiresUser
    Result<Void> del(@RequestParam Long taskId) {
        taskService.deleteTask(getUserId(), taskId);
        return Result.ok(null);
    }
}
