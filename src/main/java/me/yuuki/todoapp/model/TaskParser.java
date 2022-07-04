package me.yuuki.todoapp.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class TaskParser {
    private static final Logger logger = LoggerFactory.getLogger(TaskParser.class);

    private List<TaskCustomizer> customizers = Collections.emptyList();

    @Autowired(required = false)
    public void setCustomizers(List<TaskCustomizer> customizers) {
        customizers.stream().map(ClassUtils::getUserClass)
                .forEach(clazz -> logger.info("注册 TaskCustomizer：{}", clazz.getName()));
        this.customizers = customizers;
    }

    /**
     * 解析 todo_.txt 语法的字符串到 Task，id 会设置为 -1
     *
     * @param str 原字符串
     * @return 对应 Task 对象
     * @throws IllegalArgumentException 如果解析失败则抛出该异常
     */
    public Task parse(String str) {
        return parse(str, -1);
    }


    /**
     * 解析 todo_.txt 语法的字符串到 Task，这是创建 Task 的唯一入口
     *
     * @param str 原字符串
     * @return 对应 Task 对象
     * @throws IllegalArgumentException 如果解析失败则抛出该异常
     */
    public Task parse(String str, long id) {
        Task task = parse_(str, id);
        if (CollectionUtils.isEmpty(customizers)) {
            return task;
        }
        return customizers.stream()
                .filter(customizer -> CollectionUtils
                    .containsAny(task.getKvTags().keySet(), Arrays.asList(customizer.matchKVTag())))
                .reduce(task, (acc, x) -> x.customize(acc), null);
    }

    /**
     * 解析 todo_.txt 语法的字符串到 Task，这是创建 Task 的唯一入口。
     * <p />
     * str的模式见<a href="https://v-yop.fun/2022/05-04todo-txt%E7%AE%80%E6%98%8E%E6%95%99%E7%A8%8B.html">此</a>，
     * 但考虑到需求，这里对其模式采取了一些更改：如果只出现一个日期，则认为是结束日期而非创建日期
     *
     * @param str 原字符串
     * @return 对应 Task 对象
     * @throws IllegalArgumentException 如果解析失败则抛出该异常
     */
    private Task parse_(String str, long id) {
        // 校验日期
        // TODO 应当考虑更加丰富的方式，比如 TODAY，TOMORROW，1_DAY 等以方便使用（不过有可能前端会屏蔽这个细节……）
        Predicate<String> validDate = dateStr -> {
            if (!dateStr.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                // TODO 应当能同时处理 1976/09/09，1976/9/9 的格式
                return false;
            }

            List<Integer> dates = Arrays.stream(dateStr.split("-"))
                    .map(Integer::parseInt).collect(Collectors.toList());
            int year = dates.get(0);
            int month = dates.get(1);
            int day = dates.get(2);

            int[] monthDays = {-1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                monthDays[2] = 29;
            }

            return month >= 1 && month <= 12 && day >= 1 && day <= monthDays[month];
        };

        // 获取 KV Tags
        Function<List<String>, LinkedMultiValueMap<String, String>> getKVTags = tokens -> {
            LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            for (String token : tokens) {
                String[] maybeKV = token.split("[:：]", 2);
                if (maybeKV.length != 2) continue;

                String key = maybeKV[0];
                String value = maybeKV[1];
                multiValueMap.add(key, value);
            }
            return multiValueMap;
        };

        str = str.trim();

        boolean done = false;
        Task.Priority priority = Task.Priority.NONE;
        Date completedDate = null;
        Date createdDate = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (!StringUtils.hasText(str)) {
            throw new IllegalStateException("输入字符串为空！");
        }

        List<String> tokens = Arrays.stream(str.split("\\s"))
                .filter(StringUtils::hasText).collect(Collectors.toList());

        if (tokens.isEmpty()) {
            throw new RuntimeException("Impossible!");
        }

        // 如果全程没有空白，即只有一个元素，则认为其是 description
        if (tokens.size() == 1) {
            return new Task(
                    id,
                    done,
                    priority,
                    Optional.empty(),
                    Optional.empty(),
                    tokens,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    new LinkedMultiValueMap<>()
            );
        }

        // 指示当前“解析”的位置
        int index = 0;

        // 如果包含是否完成
        if (tokens.get(index).equalsIgnoreCase("x")) {
            done = true;
            index++;
        }

        // 如果包含优先级，同时匹配中文括号和英文括号
        if (tokens.get(index).matches("\\([A-Za-z]\\)|（[A-Za-z]）")) {
            priority = Task.Priority.valueOf(tokens.get(index).substring(1, 2).toUpperCase());
            index++;
        }

        // 尝试获取日期
        try {
            if (validDate.test(tokens.get(index))) {
                Date tempDate = dateFormat.parse(tokens.get(index));
                index++;
                if (validDate.test(tokens.get(index))) {
                    completedDate = tempDate;
                    createdDate = dateFormat.parse(tokens.get(index));
                    index++;
                } else {
                    completedDate = tempDate;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("解析日期时发生错误！" + e.getLocalizedMessage());
        }

        // 对日期的合法性进行校验
        if (completedDate != null && createdDate != null) {
            if (completedDate.compareTo(createdDate) < 0)
                throw new IllegalArgumentException("Task 的终止时间必须大于等于起始时间！" +
                    String.format("当前输入终止时间：%s，起始时间：%s", dateFormat.format(completedDate), dateFormat.format(createdDate)));
            if (dateFormat.format(completedDate).equals("2099-12-31")) {
                throw new IllegalArgumentException("终止日期不能为 2099-12-31！");
            }
            if (dateFormat.format(createdDate).equals("1970-01-01")) {
                throw new IllegalArgumentException("起始日期不能为 1970-01-01！");
            }
        }

        List<String> descriptionTokens = tokens.subList(index, tokens.size());
        return new Task(
                id,
                done,
                priority,
                Optional.ofNullable(completedDate),
                Optional.ofNullable(createdDate),
                descriptionTokens,
                descriptionTokens.stream()
                        .filter(token -> token.length() != 1 && token.startsWith("+"))
                        .map(token -> token.substring(1)).collect(Collectors.toList()),
                descriptionTokens.stream()
                        .filter(token -> token.length() != 1 && token.startsWith("@"))
                        .map(token -> token.substring(1)).collect(Collectors.toList()),
                getKVTags.apply(descriptionTokens)
        );

    }
}
