package me.yuuki.todoapp.dao;

import me.yuuki.todoapp.entity.TaskEntity;
import me.yuuki.todoapp.model.Task;

import java.util.Date;
import java.util.List;

/**
 * Task 的 DAO，分页等操作当前不考虑。
 */
public interface TaskDao {

    TaskEntity select(long taskId);

    List<TaskEntity> selectAllTask(String userId);

    /**
     * 返回所有未完成的 Task
     */
    List<TaskEntity> selectWorkingTask(String userId);

    /**
     * 返回所有未完成且当前日期在起始日期和终止日期之间的 Task
     */
    List<TaskEntity> selectValidTask(String userId);

    /**
     * 返回所有未完成且当前日期不在起始日期和终止日期之间的 Task
     */
    List<TaskEntity> selectOutDatedTask(String userId);

    /**
     * 返回特定日期的 valid 的 Task
     */
    List<TaskEntity> selectValidTask(String userId, Date date);

    List<TaskEntity> selectDoneTask(String userId);

    long addTask(TaskEntity task);

    boolean deleteTask(long taskId);

    void doneTask(long taskId);
}
