package me.yuuki.todoapp.service.impl;

import me.yuuki.todoapp.entity.TaskEntity;
import me.yuuki.todoapp.entity.TaskEntityExample;
import me.yuuki.todoapp.exception.ClientException;
import me.yuuki.todoapp.mapper.TaskEntityMapper;
import me.yuuki.todoapp.mapper.TaskTagMapper;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskComparator;
import me.yuuki.todoapp.model.TaskParser;
import me.yuuki.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskEntityMapper taskEntityMapper;

    private TaskTagMapper taskTagMapper;

    private TaskParser taskParser;

    @Autowired
    public void setTaskParser(TaskParser taskParser) {
        this.taskParser = taskParser;
    }

    private TaskEntity toTaskEntity(Integer userId, Task task) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.notNull(task, "Task不能为空！");
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(task.getId());
        taskEntity.setDone(task.getDone());
        try {
            taskEntity.setStartDate(task.getStartDate().orElse(dateFormat.parse("1970-01-01")));
            taskEntity.setEndDate(task.getEndDate().orElse(dateFormat.parse("2099-12-31")));
        } catch (ParseException e) {
            throw new RuntimeException("impossible");
        }

        taskEntity.setTaskDescription(String.join("\u0000", task.getDescriptionTokens()));
        if (task.getPriority() != Task.Priority.NONE) {
            taskEntity.setPriority(task.getPriority().toString());
        } else {
            taskEntity.setPriority("x");
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
        if (!taskEntity.getPriority().equalsIgnoreCase("x")) {
            result.add("(" + taskEntity.getPriority() + ")");
        }

        if (!dateFormat.format(taskEntity.getEndDate()).equals("2099-12-31")) {
            result.add(dateFormat.format(taskEntity.getEndDate()));
        }
        if (!dateFormat.format(taskEntity.getStartDate()).equals("1970-01-01")) {
            result.add(dateFormat.format(taskEntity.getStartDate()));
        }
        result.add(StringUtils.replace(taskEntity.getTaskDescription(), "\u0000", " "));
        return taskParser.parse(result.toString(), taskEntity.getId());
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
    public Task select(Integer userId, long taskId) {
        TaskEntity taskEntity = taskEntityMapper.selectByPrimaryKey(taskId);
        if (taskEntity == null) {
            throw new ClientException("该Task不存在！");
        }
        if (!taskEntity.getUserId().equals(userId)) {
            throw new ClientException("这好像不是你的 Task :(");
        }
        return toTask(taskEntity);
    }

    @Override
    public List<Task> selectAllTask(Integer userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectUnfinishedTask(Integer userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId)
                .andDoneEqualTo(false);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectValidTask(Integer userId) {
        return selectValidTask(userId,
                Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public List<Task> selectOutDatedTask(Integer userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andEndDateLessThan(new Date());
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectFutureTask(Integer userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andStartDateGreaterThan(new Date());
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectValidTask(Integer userId, Date date) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId)
                .andDoneEqualTo(false)
                .andStartDateLessThanOrEqualTo(date)
                .andEndDateGreaterThanOrEqualTo(date);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> selectDoneTask(Integer userId) {
        TaskEntityExample example = new TaskEntityExample();
        example.or().andUserIdEqualTo(userId)
                .andDoneEqualTo(true);
        return taskEntityMapper.selectByExample(example).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }

    @Override
    public long addTask(Integer userId, Task task) {
        TaskEntity taskEntity = toTaskEntity(userId, task);
        taskEntityMapper.insertSelective(taskEntity);
        return taskEntity.getId();
    }

    @Override
    @Transactional
    public void deleteTask(Integer userId, long taskId) {
        if (select(userId, taskId) == null) {
            throw new ClientException("没有这个 Task");
        }
        taskEntityMapper.deleteByPrimaryKey(taskId);
    }

    @Override
    @Transactional
    public void toggleTask(Integer userId, long taskId) {
        Task task = select(userId, taskId);

        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setDone(!task.getDone());
        taskEntityMapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public List<Task> selectValidTaskPeriod(Integer userId, Date startDate, Date endDate) {
        return taskEntityMapper.selectByPeriod(userId, startDate, endDate).stream()
                .map(this::toTask)
                .sorted(TaskComparator.get())
                .collect(Collectors.toList());
    }
}
