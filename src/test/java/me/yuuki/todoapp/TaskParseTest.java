package me.yuuki.todoapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.yuuki.todoapp.config.StringMultiValueMapDeserializer;
import me.yuuki.todoapp.config.StringMultiValueMapSerializer;
import me.yuuki.todoapp.model.Task;
import me.yuuki.todoapp.model.TaskParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(properties = "spring.profiles.active=test")
public class TaskParseTest {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private TaskParser taskParser;

    @Autowired
    public void setTaskParser(TaskParser taskParser) {
        this.taskParser = taskParser;
        taskParser.setCustomizers(Collections.emptyList());
    }

    @Test
    public void simple() {
        Task task = taskParser.parse(
                "X (A) @下班后 +学习 Spring MVC"
                , -1);
        System.out.println(task.debugFormat());
        assert task.getDone();
        assert task.getPriority() == Task.Priority.A;
        assert !task.getEndDate().isPresent();
        assert task.getStartDate().isPresent();
        assert task.getContextTags().size() == 1;
        assert task.getContextTags().get(0).equalsIgnoreCase("下班后");
        assert task.getProjectTags().size() == 1;
        assert task.getProjectTags().get(0).equalsIgnoreCase("学习");
        assert task.getKvTags().isEmpty();
    }

    @Test
    public void fullFeature() {
        Task parse = taskParser.parse(
                "X (B) 2233-12-31 2021-06-28 @晚上 +学习 处理 Excel 的最佳实践 afterDone:delete"
                , -1);
        System.out.println(parse.debugFormat());
    }

    @Test
    public void jsonSeDe() throws JsonProcessingException {
        Task task = taskParser.parse(
                "X (B) 2043-12-31 @晚上 +学习 处理 Excel 的最佳实践 afterDone:delete"
                , -1);

        String s = objectMapper.writeValueAsString(task);
        System.out.println(s);

        Task task1 = objectMapper.readValue(s, Task.class);
        System.out.println(task1.debugFormat());
        assert task.debugFormat().equalsIgnoreCase(task1.debugFormat());
    }

    @Test
    public void MultiValueMapSerDeTest() throws JsonProcessingException {
        MultiValueMapWrap wrap = new MultiValueMapWrap();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("hello", Arrays.asList("hello", "world"));
        map.put("world", Collections.emptyList());
        map.put("rua", null);
        wrap.data = map;
        System.out.println(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrap));

        System.out.println(objectMapper.readValue(objectMapper.writeValueAsString(wrap), MultiValueMapWrap.class)
                .data);
    }

    @Test
    void dateTest() {
        System.out.println(taskParser.parse("x wochao").debugFormat());
        System.out.println(taskParser.parse("2099-02-12 wochao").debugFormat());
        System.out.println(taskParser.parse("2099-02-12 2011-02-11").debugFormat());
    }

    private static class MultiValueMapWrap {
        @JsonDeserialize(using = StringMultiValueMapDeserializer.class)
        @JsonSerialize(using = StringMultiValueMapSerializer.class)
        public MultiValueMap<String, String> data;
    }
}
