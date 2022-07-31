package me.yuuki.todoapp.service;

import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
@Rollback
class TaskServiceTest {

    @Autowired
    TaskService taskService;

    private TaskParser taskParser;

    @Autowired
    public void setTaskParser(TaskParser taskParser) {
        this.taskParser = taskParser;
    }

    private int randomInt() {
        return new Random().nextInt(1000) + 1000000;
    }

    @Test
    void select() {
        int userId = randomInt();
        long id = taskService.addTask(userId, taskParser.parse(
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
        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        assert taskService.selectAllTask(userId).size() == 3;
    }

    @Test
    void selectUnfinishedTask() {
        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));

        assert taskService.selectUnfinishedTask(userId).size() == 0;
        taskService.addTask(userId, taskParser.parse(
                "(A) 2022-09-20 2021-03-20 测试一下"
        ));
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2022-09-20 2021-03-20 测试一下"
        ));
        assert taskService.selectUnfinishedTask(userId).size() == 1;
    }

    @Test
    void selectValidTask() {
        int userId = randomInt();

        // valid
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 测试一下"
        ));
        assert taskService.selectValidTask(userId).size() == 1;
        // notValid
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2099-03-20 测试一下"
        ));

        assert taskService.selectValidTask(userId).size() == 1;

        // notValid
        taskService.addTask(userId, taskParser.parse(
                " (A) 2012-09-20 1999-01-20 测试一下"
        ));

        assert taskService.selectValidTask(userId).size() == 1;
        // valid
        taskService.addTask(userId, taskParser.parse(
                " (A) 2099-09-20 2022-01-20 测试一下"
        ));
        assert taskService.selectValidTask(userId).size() == 2;
    }

    @Test
    void selectOutDatedTask() {

        int userId = randomInt();
        // valid
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 测试一下"
        ));
        assert taskService.selectOutDatedTask(userId).size() == 0;
        // notValid
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2099-03-20 测试一下"
        ));

        assert taskService.selectOutDatedTask(userId).size() == 0;

        // notValid
        taskService.addTask(userId, taskParser.parse(
                " (A) 2012-09-20 1999-01-20 测试一下"
        ));

        assert taskService.selectOutDatedTask(userId).size() == 1;
    }

    @Test
    void selectFutureTask() {
        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 测试一下"
        ));

        assert taskService.selectFutureTask(userId).size() == 0;

        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2089-02-11 测试一下"
        ));

        taskService.addTask(userId, taskParser.parse(
                "(A) 1999-09-20 1988-02-11 测试一下"
        ));

        assert taskService.selectFutureTask(userId).size() == 1;
    }

    @Test
    void testSelectValidTask() throws ParseException {

        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        taskService.addTask(userId, taskParser.parse(
                "(A) 2070-09-20 一个现在的task"
        ));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assert taskService.selectValidTask(userId,
                dateFormat.parse("2090-12-31")).size() == 1;

    }

    @Test
    void selectDoneTask() {
        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert taskService.selectDoneTask(userId).size() == 0;
        taskService.addTask(userId, taskParser.parse(
                "X (A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert taskService.selectDoneTask(userId).size() == 1;
    }

    @Test
    void deleteTask() {

        int userId = randomInt();
        long id = taskService.addTask(userId, taskParser.parse(
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

        int userId = randomInt();
        long id = taskService.addTask(userId, taskParser.parse(
                "(A) 2099-09-20 2088-02-12 一个未来的task"
        ));
        assert !taskService.select(userId, id).getDone();
        taskService.toggleTask(userId, id);
        assert taskService.select(userId, id).getDone();
        taskService.toggleTask(userId, id);
        assert !taskService.select(userId, id).getDone();
    }


    @Test
    void selectValidTaskPeriod() throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int userId = randomInt();
        taskService.addTask(userId, taskParser.parse(
                "(A) 2099-01-01 1970-12-31 一个未来的task"
        ));
        assert taskService.selectValidTaskPeriod(userId,
                dateFormat.parse("1970-12-30"),
                dateFormat.parse("1970-12-30")).size() == 0;
        assert taskService.selectValidTaskPeriod(userId,
                dateFormat.parse("1970-12-30"),
                dateFormat.parse("1970-12-31")).size() == 1;
        assert taskService.selectValidTaskPeriod(userId,
                dateFormat.parse("1970-12-30"),
                dateFormat.parse("2199-12-31")).size() == 1;
    }
}