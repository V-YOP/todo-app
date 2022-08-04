package me.yuuki.todoapp.controller;


import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskParser;
import me.yuuki.todoapp.service.TaskService;
import me.yuuki.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/task")
public class TaskController {

    private Integer getUserId() {
        return 1;
    }

    private UserService userService;

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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    
    Result<Task> get(
            @Min(value = 0, message = "taskId 必须大于 0！")
            @RequestParam long taskId) {
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

    /**
     * 添加 task，将数据包含在请求体中以避免麻烦的转义问题
     * @param taskStr task字符串，必须为单例的数组
     * @return 添加的task的id
     */
    @PostMapping("add")
    
    Result<Long> add(@Size(min = 1, max = 1, message = "请求体数组大小必须为1！")
                     @RequestBody List<@NotBlank(message = "输入字符串不能为空！") String> taskStr) {
        Task task = ClientException.tryMe(
                () -> taskParser.parse(taskStr.get(0)),
                e -> "Task 字符串解析失败！" + e.getLocalizedMessage());
        return Result.ok(taskService.addTask(getUserId(), task));
    }

    @PostMapping("toggle")
    
    Result<Void> done(
            @Min(value = 0, message = "taskId 必须大于 0！")
            @RequestParam Long taskId) {
        taskService.toggleTask(getUserId(), taskId);
        return Result.ok(null);
    }

    @PostMapping("del")
    
    Result<Void> del(
            @Min(value = 0, message = "taskId 必须大于 0！")
            @RequestParam Long taskId) {
        taskService.deleteTask(getUserId(), taskId);
        return Result.ok(null);
    }
}
