package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.entity.TaskEntity;
import me.yuuki.todoapp.entity.TaskEntityExample;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.mapper.TaskEntityMapper;
import me.yuuki.todoapp.mapper.TaskTagMapper;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskComparator;
import me.yuuki.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskEntityMapper taskEntityMapper;

    private TaskTagMapper taskTagMapper;

    private TaskEntity toTaskEntity(String userId, Task task) {
        Assert.notNull(task, "Task不能为空！");
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(task.getId());
        taskEntity.setDone(task.getDone());
        task.getStartDate().ifPresent(taskEntity::setStartDate);
        task.getEndDate().ifPresent(taskEntity::setEndDate);
        taskEntity.setTaskDescription(String.join("\u0000", task.getDescriptionTokens()));
        if (task.getPriority() != Task.Priority.NONE) {
            taskEntity.setPriority(task.getPriority().toString());
        }
        taskEntity.setUserId(userId);
        return taskEntity;
    }

    private Task toTask(TaskEntity taskEntity) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.notNull(taskEntity, "taskEntity不能为空！");
        StringJoiner result = new StringJoiner(" ");
        if (taskEntity.getDone()) {
            result.add("x");
        }
        if (taskEntity.getPriority() != null) {
            result.add("(" + taskEntity.getPriority() + ")");
        }
        if (taskEntity.getEndDate() != null) {
            result.add(dateFormat.format(taskEntity.getEndDate()));
        }
        if (taskEntity.getStartDate() != null) {
            result.add(dateFormat.format(taskEntity.getStartDate()));
        }
        result.add(StringUtils.replace(taskEntity.getTaskDescription(), "\u0000", " "));
        return Task.parse(result.toString(), taskEntity.getId());
    }

    @Autowired
    public void setTaskEntityMapper(TaskEntityMapper taskEntityMapper) {
        this.taskEntityMapper = taskEntityMapper;
    }

    @Autowired
    public void setTaskTagMapper(TaskTagMapper taskTagMapper) {
        this.taskTagMapper = taskTagMapper;
    }

    @Override
    public Task select(String userId, long taskId) {
        TaskEntity taskEntity = taskEntityMapper.selectByPrimaryKey(taskId);
        if (taskEntity == null) {
            throw new ClientException("该Task不存在！");
        }
        if (!taskEntity.getUserId().equals(userId)) {
            throw new ClientException("这好像不是你的 Task");
        }
        return toTask(taskEntity);
    }

    @Override
    public List<Task> selectAllTask(String userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectUnfinishedTask(String userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andDoneEqualTo(false);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectValidTask(String userId) {
        return selectValidTask(userId,
                Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public List<Task> selectOutDatedTask(String userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andEndDateLessThan(new Date());
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectFutureTask(String userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andStartDateGreaterThan(new Date());
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectValidTask(String userId, Date date) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andStartDateLessThanOrEqualTo(date)
                .andEndDateGreaterThanOrEqualTo(date);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectDoneTask(String userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andDoneEqualTo(true);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public long addTask(String userId, Task task) {
        TaskEntity taskEntity = toTaskEntity(userId, task);
        taskEntityMapper.insert(taskEntity);
        return taskEntity.getId();
    }

    @Override
    @Transactional
    public void deleteTask(String userId, long taskId) {
        if (select(userId, taskId) == null) {
            throw new ClientException("没有这个 Task");
        }
        taskEntityMapper.deleteByPrimaryKey(taskId);
    }

    @Override
    @Transactional
    public void doneTask(String userId, long taskId) {
        Task task = select(userId, taskId);
        if (task.getDone()) {
            throw new ClientException("这个task已经完成了");
        }
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setDone(true);
        taskEntityMapper.updateByPrimaryKeySelective(entity);
    }
}
