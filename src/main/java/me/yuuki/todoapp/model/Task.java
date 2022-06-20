package me.yuuki.todoapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.yuuki.todoapp.config.StringMultiValueMapDeserializer;
import me.yuuki.todoapp.config.StringMultiValueMapSerializer;
import org.springframework.util.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO 考虑之后把Json相关的部分移出去，现在有点丑陋
 */
public final class Task {

    /**
     * Task 的唯一标识符
     */
    private final Long id;

    /**
     * Task 的描述部分（即是否完成，优先级，日期后的所有部分），根据空白去分割
     */
    private final List<String> descriptionTokens;
    /**
     * Task 是否完成
     */
    private final Boolean done;
    /**
     * Task 的优先级
     */
    private final Priority priority;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private final Optional<Date> startDate;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private final Optional<Date> endDate;
    private final List<String> projectTags;
    private final List<String> contextTags;

    @JsonDeserialize(using = StringMultiValueMapDeserializer.class)
    @JsonSerialize(using = StringMultiValueMapSerializer.class)
    private final MultiValueMap<String, String> kvTags;

    @JsonCreator
    Task(
            @JsonProperty("id") Long id,
            @JsonProperty("done") Boolean done,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("endDate") Optional<Date> endDate,
            @JsonProperty("startDate") Optional<Date> startDate,
            @JsonProperty("descriptionTokens") List<String> descriptionTokens, // descriptionTokens 实际上包含下面的值
            @JsonProperty("projectTags") List<String> projectTags,
            @JsonProperty("contextTags") List<String> contextTags,
            @JsonProperty("kvTags")
            @JsonDeserialize(using = StringMultiValueMapDeserializer.class)
            @JsonSerialize(using = StringMultiValueMapSerializer.class)
            MultiValueMap<String, String> kvTags) {
        this.id = id;
        Assert.noNullElements(Arrays.asList(id, descriptionTokens, done, priority, endDate, startDate, projectTags,
                        contextTags,
                        kvTags),
                "Task 对象不允许空值！");
        this.descriptionTokens = descriptionTokens;
        this.done = done;
        this.priority = priority;
        this.endDate = endDate;
        this.startDate = startDate;
        this.projectTags = Collections.unmodifiableList(projectTags);
        this.contextTags = Collections.unmodifiableList(contextTags);
        this.kvTags = kvTags;
    }

    public long getId() {
        return id;
    }

    public List<String> getDescriptionTokens() {
        return descriptionTokens;
    }

    public Boolean getDone() {
        return done;
    }

    public Priority getPriority() {
        return priority;
    }

    public List<String> getProjectTags() {
        return projectTags;
    }

    public List<String> getContextTags() {
        return contextTags;
    }

    public MultiValueMap<String, String> getKvTags() {
        return kvTags;
    }

    public Optional<Date> getStartDate() {
        return startDate;
    }

    public Optional<Date> getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return Stream.concat(
                        Stream.of(
                                done ? "X" : null,
                                priority != Priority.NONE ? String.format("(%s)", priority.toString()) : null,
                                getEndDate().map(dateFormat::format).orElse(null),
                                getStartDate().map(dateFormat::format).orElse(null)),
                        descriptionTokens.stream())
                .filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    public String debugFormat() {
        return new StringJoiner("\n")
                .add("Task Debug Output:")
                .add("    id                 :  " + getId())
                .add("    content            :  " + this)
                .add("    done               :  " + getDone())
                .add("    priority           :  " + getPriority())
                .add("    end_date           :  " + getEndDate().orElse(null))
                .add("    start_date         :  " + getStartDate().orElse(null))
                .add("    description_tokens :  " + getDescriptionTokens())
                .add("    project_tags       :  " + getProjectTags())
                .add("    context_tags       :  " + getContextTags())
                .add("    kv_tags            :  " + getKvTags())
                .toString();
    }

    // 枚举的比较中，越后定义的枚举越大
    public enum Priority {
        NONE, Z, Y, X, W, V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A
    }


    public static final class TaskBuilder {
        private long id;
        private List<String> descriptionTokens;
        private Boolean done;
        private Priority priority;
        private Optional<Date> startDate = Optional.of(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        private Optional<Date> endDate = Optional.empty();
        private List<String> projectTags;
        private List<String> contextTags;
        private MultiValueMap<String, String> kvTags;


        private TaskBuilder() {
        }

        public static TaskBuilder aTask() {
            return new TaskBuilder();
        }

        public static TaskBuilder from(Task task) {
            Assert.notNull(task, "Task 不能为空！");
            return new TaskBuilder()
                    .withId(task.getId())
                    .withDescriptionTokens(task.getDescriptionTokens())
                    .withDone(task.getDone())
                    .withPriority(task.getPriority())
                    .withProjectTags(task.getProjectTags())
                    .withContextTags(task.getContextTags())
                    .withStartDate(task.getStartDate())
                    .withEndDate(task.getEndDate())
                    .withKvTags(task.getKvTags());
        }

        public TaskBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public TaskBuilder withDescriptionTokens(List<String> descriptionTokens) {
            this.descriptionTokens = descriptionTokens;
            return this;
        }

        public TaskBuilder withDone(Boolean done) {
            this.done = done;
            return this;
        }

        public TaskBuilder withPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public TaskBuilder withStartDate(Optional<Date> startDate) {
            this.startDate = startDate;
            return this;
        }

        public TaskBuilder withEndDate(Optional<Date> endDate) {
            this.endDate = endDate;
            return this;
        }

        public TaskBuilder withProjectTags(List<String> projectTags) {
            this.projectTags = projectTags;
            return this;
        }

        public TaskBuilder withContextTags(List<String> contextTags) {
            this.contextTags = contextTags;
            return this;
        }

        public TaskBuilder withKvTags(Map<String, List<String>> kvTags) {
            this.kvTags = new MultiValueMapAdapter<>(kvTags);
            return this;
        }

        public Task build() {
            return new Task(id, done, priority, endDate, startDate, descriptionTokens, projectTags, contextTags, kvTags);
        }
    }
}
