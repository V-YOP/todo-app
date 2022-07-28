package me.yuuki.todoapp.mapper;

import java.util.Date;
import java.util.List;
import me.yuuki.todoapp.entity.TaskEntity;
import me.yuuki.todoapp.entity.TaskEntityExample;
import org.apache.ibatis.annotations.Param;

public interface TaskEntityMapper {

    /**
     * 查询一段日期内合法的Task，这段日期中任意一天合法的Task都会被选择到
     * @param userId 用户ID
     * @param startDate 起始日期，inclusive
     * @param endDate  终止日期，inclusive
     */
    List<TaskEntity> selectByPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    long countByExample(TaskEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int deleteByExample(TaskEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int insert(TaskEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int insertSelective(TaskEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    List<TaskEntity> selectByExample(TaskEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    TaskEntity selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int updateByExampleSelective(@Param("record") TaskEntity record, @Param("example") TaskEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int updateByExample(@Param("record") TaskEntity record, @Param("example") TaskEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int updateByPrimaryKeySelective(TaskEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_t
     *

     */
    int updateByPrimaryKey(TaskEntity record);
}