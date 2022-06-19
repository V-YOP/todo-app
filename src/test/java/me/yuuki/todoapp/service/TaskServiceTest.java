package me.yuuki.todoapp.service;

import me.yuuki.todoapp.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
@Rollback
class TaskServiceTest {

    @Autowired
    TaskService taskService;

    private String randomStr(int length) {
        String tmp = "";
        for (int i = 0; i < 1 + length / 32; i++) {
            tmp += UUID.randomUUID().toString();
        }
        return tmp.substring(0, length);
    }

    @Test
    void select() {
        String userId = randomStr(32);
        long id = taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        Task task = taskService.select(userId, id);
        assert task != null;
        assert task.getDone().equals(true);
        assert task.getPriority().equals(Task.Priority.A);
        assert task.getDescriptionTokens().get(0).equals("测试一下");
    }

    @Test
    void selectAllTask() {
        String userId = randomStr(32);
        taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        assert taskService.selectAllTask(userId).size() == 3;
    }

    @Test
    void selectUnfinishedTask() {
        String userId = randomStr(32);
        taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));

        assert taskService.selectUnfinishedTask(userId).size() == 0;
        taskService.addTask(userId, Task.parse(
                "(A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, Task.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        assert taskService.selectUnfinishedTask(userId).size() == 1;
    }

    @Test
    void selectValidTask() {
        String userId = randomStr(32);

        // valid
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 测试一下"
        ));
        assert taskService.selectValidTask(userId).size() == 1;
        // notValid
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2099-03-20 测试一下"
        ));

        assert taskService.selectValidTask(userId).size() == 1;

        // notValid
        taskService.addTask(userId, Task.parse(
                " (A) 2012-09-20 1999-01-20 测试一下"
        ));

        assert taskService.selectValidTask(userId).size() == 1;
        // valid
        taskService.addTask(userId, Task.parse(
                " (A) 2099-09-20 2022-01-20 测试一下"
        ));
        assert taskService.selectValidTask(userId).size() == 2;
    }

    @Test
    void selectOutDatedTask() {

        String userId = randomStr(32);
        // valid
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 测试一下"
        ));
        assert taskService.selectOutDatedTask(userId).size() == 0;
        // notValid
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2099-03-20 测试一下"
        ));

        assert taskService.selectOutDatedTask(userId).size() == 0;

        // notValid
        taskService.addTask(userId, Task.parse(
                " (A) 2012-09-20 1999-01-20 测试一下"
        ));

        assert taskService.selectOutDatedTask(userId).size() == 1;
    }

    @Test
    void selectFutureTask() {
        String userId = randomStr(32);
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 测试一下"
        ));

        assert taskService.selectFutureTask(userId).size() == 0;

        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2089-02-11 测试一下"
        ));

        taskService.addTask(userId, Task.parse(
                "(A) 1999-09-20 1988-02-11 测试一下"
        ));

        assert taskService.selectFutureTask(userId).size() == 1;
    }

    @Test
    void testSelectValidTask() throws ParseException {


        String userId = randomStr(32);
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        taskService.addTask(userId, Task.parse(
                "(A) 2070-09-20 一个现在的task"
        ));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assert taskService.selectValidTask(userId,
                dateFormat.parse("2090-12-31")).size() == 1;

    }

    @Test
    void selectDoneTask() {
        String userId = randomStr(32);
        taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert taskService.selectDoneTask(userId).size() == 0;
        taskService.addTask(userId, Task.parse(
                "X (A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert taskService.selectDoneTask(userId).size() == 1;
    }

    @Test
    void deleteTask() {

        String userId = randomStr(32);
        long id = taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        taskService.select(userId, id);
        taskService.deleteTask(userId, id);

        try {
            taskService.select(userId, id);
        } catch (Exception e) {
            return;
        }
        throw new RuntimeException("Something wrong！");
    }

    @Test
    void doneTask() {

        String userId = randomStr(32);
        long id = taskService.addTask(userId, Task.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert !taskService.select(userId, id).getDone();
        taskService.doneTask(userId, id);
        assert taskService.select(userId, id).getDone();

        try {
            taskService.doneTask(userId, id);
        } catch (Exception e) {
            return;
        }
        throw new RuntimeException("Something wrong");
    }
}