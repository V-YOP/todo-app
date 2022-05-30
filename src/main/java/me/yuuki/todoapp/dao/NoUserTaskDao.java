package me.yuuki.todoapp.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.yuuki.todoapp.dto.ClientException;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.util.oss.OSSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.yuuki.todoapp.util.KeyUtil.constructKey;

/**
 * 不关心多用户的 TaskDao
 */
@Repository
public class NoUserTaskDao implements TaskDao {

    private static final Logger logger = LoggerFactory.getLogger(NoUserTaskDao.class);

    private static final String GLOBAL_PREFIX = "data";
    private OSSUtil ossUtil;
    private ObjectMapper objectMapper;

    @Autowired
    public void setOssUtil(OSSUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String workingDir(String userId) {
        return constructKey(GLOBAL_PREFIX, userId, "working");
    }

    private String doneDir(String userId) {
        return constructKey(GLOBAL_PREFIX, userId, "done");
    }

    private String repeatDir(String userId) {
        return constructKey(GLOBAL_PREFIX, userId, "repeat");
    }

    private Task jsonToTask(String taskJson) {
        try {
            return objectMapper.readValue(taskJson, Task.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析 Task 的 json 失败！", e);
        }
    }

    private String taskToJson(Task task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Task> selectPrefixTaskIgnoreNull(String prefix) {
        return ossUtil.listAsStream(prefix)
                .flatMap(key -> ossUtil.get(key)
                        .map(this::jsonToTask)
                        .map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectAllTask(String userId) {
        return selectPrefixTaskIgnoreNull(constructKey(GLOBAL_PREFIX, userId));
    }

    @Override
    public List<Task> selectWorkingTask(String userId) {
        return selectPrefixTaskIgnoreNull(workingDir(userId));
    }

    @Override
    public List<Task> selectDoneTask(String userId) {
        return selectPrefixTaskIgnoreNull(doneDir(userId));
    }

    @Override
    public boolean addTask(String userId, Task task) {
        String key = constructKey(task.getDone() ? doneDir(userId) : workingDir(userId),
                Long.toString(task.getId()));
        ossUtil.put(key, taskToJson(task));
        logger.info("写入key：{}", key);
        return true;
    }

    @Override
    public boolean deleteTask(String userId, long taskId) {
        boolean success = ossUtil.delete(constructKey(workingDir(userId), String.valueOf(taskId))) ||
                ossUtil.delete(constructKey(doneDir(userId), String.valueOf(taskId)));
        if (!success) {
            throw new ClientException("该 TaskId 不存在！");
        }
        return true;
    }

    @Override
    public void doneTask(String userId, long taskId) {
        Task theTask = selectWorkingTask(userId).stream()
                .filter(task -> task.getId() == taskId).findFirst()
                .map(task -> Task.TaskBuilder.from(task).withDone(true).build())
                .orElseThrow(() -> new ClientException("该 TaskId 不存在！"));
        deleteTask(userId, taskId);
        ossUtil.put(constructKey(doneDir(userId), String.valueOf(taskId)), taskToJson(theTask));
    }
}
