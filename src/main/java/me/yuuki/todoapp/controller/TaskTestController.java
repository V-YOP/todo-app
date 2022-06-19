package me.yuuki.todoapp.controller;

import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用来随便做点测试
 */
@RestController
@RequestMapping("/test")
public class TaskTestController {
    private static final String USER_ID = "TEST_00000000001";

    private TaskService taskService;

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("list")
    Result<List<Task>> list(
            @RequestParam(name="token", defaultValue = "no men!") String token
    ) {
        if (!token.equals("YUUKI_YOP"))
            throw new ClientException("不给用！");
        return Result.ok(taskService.selectAllTask(USER_ID));
    }
    @GetMapping("list-str")
    String listStr(@RequestParam(name="token", defaultValue = "no men!") String token) {

        if (!token.equals("YUUKI_YOP"))
            throw new ClientException("不给用！");
        return taskService.selectAllTask(USER_ID)
                        .stream().map(task -> task.getId() + "  " + task.toString())
                        .collect(Collectors.joining("\n"));
    }

    @PostMapping("add")
    Result<Long> add(
            @RequestParam(name="token", defaultValue = "no men!") String token
            ,@RequestParam(name = "task") String taskStr) {
        if (!token.equals("YUUKI_YOP"))
            throw new ClientException("不给用！");


        Task task;
        try {
            task = Task.parse(taskStr);
        } catch (Exception e) {
            throw new ClientException("Task 解析失败！" + e.getLocalizedMessage());
        }
        return Result.ok(taskService.addTask(USER_ID, task));
    }

    @PostMapping("done")
    Result<Void> done(
            @RequestParam(name="token", defaultValue = "no men!") String token
            ,@RequestParam Long taskId) {

        if (!token.equals("YUUKI_YOP"))
            throw new ClientException("不给用！");



        taskService.doneTask(USER_ID, taskId);
        return Result.ok(null);
    }

    @PostMapping("del")
    Result<Void> del(
        @RequestParam(name="token", defaultValue = "no men!") String token,

         @RequestParam Long taskId) {

        if (!token.equals("YUUKI_YOP"))
            throw new ClientException("不给用！");



        taskService.deleteTask(USER_ID, taskId);
        return Result.ok(null);
    }
}
