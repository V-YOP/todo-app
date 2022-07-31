package me.yuuki.todoapp.service;

import me.yuuki.todoapp.model.Task;

import java.util.Date;
import java.util.List;

/**
 * <p />Task 的 Service，分页等操作当前不考虑。
 *
 * <p />这里有一些很有趣的问题：userId怎么处理？这里所有方法都需要userId作为参数。
 *
 * <p />第一个想法是从ThreadLocal等地方去拿到userId，这在真实的业务代码上很常见，一些
 * 框架的作用机理也是如此，但这种操作有一种问题，就是进行单元测试的时候很蛋疼，且
 * 理解起来也会变得麻烦（我觉得一切需要的变量都应当在控制器里拿并通过参数传递给
 * 服务层，这样最好理解）
 *
 * <p />第二个想法是为方法的输入去自定义一个类型，把共用的参数都包括进去，一个
 * 典型是OSS的API……但我觉得这个方式有点过于java了。
 *
 * <p />第三个想法是学习柯里化的方式，让这个Service去提供一个方法接受用户ID（以及
 * 其它所有方法都包括的参数），返回一个所谓Handler的对象，而所有业务都给到handler
 * 中，这其实就像F#那种参数化模块。
 *
 * <p />但这里不想太多，直接全部作为参数。
 */
public interface TaskService {

    /**
     * 根据id查询task
     * @param userId 用户名，用于检查该Task是否属于该用户
     * @param taskId task的id
     * @return id对应task
     * @throws me.yuuki.todoapp.exception.ClientException 如果该task不存在，或task不属于该用户则抛出
     */
    Task select(Integer userId, long taskId);

    /**
     * 查询特定用户的所有Task
     * @param userId 用户ID
     * @return 用户的所有Task
     */
    List<Task> selectAllTask(Integer userId);

    /**
     * 返回所有未完成，即done为false的Task
     * @param userId 用户ID
     * @return 用户的所有Task
     */
    List<Task> selectUnfinishedTask(Integer userId);

    /**
     * 返回所有未完成且当前日期在起始日期和终止日期之间的 Task，其中若终止时间为NULL，则视为无穷远
     * @param userId 用户ID
     * @return 用户的所有合法 Task
     */
    List<Task> selectValidTask(Integer userId);

    /**
     * 返回所有未完成且当前日期大于终止日期之间的 Task
     * @param userId 用户ID
     * @return 用户的所有过期 Task
     */
    List<Task> selectOutDatedTask(Integer userId);

    /**
     * 返回所有未完成且当前日期小于起始日期之间的 Task
     * @param userId 用户ID
     * @return 用户的所有合法 Task
     */
    List<Task> selectFutureTask(Integer userId);

    /**
     * 返回特定日期下的合法的 Task
     * @param userId 用户ID
     * @param date 待查询的日期
     * @return 该用户该日期的所有合法 Task
     */
    List<Task> selectValidTask(Integer userId, Date date);

    /**
     * 返回所有完成的 Task
     * @param userId 用户ID
     * @return 该用户所有完成的 Task
     */
    List<Task> selectDoneTask(Integer userId);

    /**
     * 添加 Task，Task的userId字段将被用于设置Task对应用户
     * @return 生成的Task的ID
     */
    long addTask(Integer userId, Task task);

    /**
     * 删除Task
     * @param userId Task的用户ID
     * @param taskId Task的id
     * @throws me.yuuki.todoapp.exception.ClientException 如果Task不存在或Task不属于该用户
     */
    void deleteTask(Integer userId, long taskId);

    /**
     * 完成或取消完成Task
     * @param userId Task的用户ID
     * @param taskId Task的id
     * @throws me.yuuki.todoapp.exception.ClientException 如果Task不存在或Task不属于该用户
     */
    void toggleTask(Integer userId, long taskId);

    /**
     * 获取在一段日期中任意一天 valid 的Task
     * @param userId 用户ID
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 这段日期中valid的Task
     */
    List<Task> selectValidTaskPeriod(Integer userId, Date startDate, Date endDate);
}
