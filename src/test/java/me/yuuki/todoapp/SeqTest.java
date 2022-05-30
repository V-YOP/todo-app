package me.yuuki.todoapp;

import me.yuuki.todoapp.util.SequenceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest
public class SeqTest {

    @Autowired
    private SequenceUtil sequenceUtil;

    @Test
    void generate() {
        assert LongStream.range(0, 100000).parallel().mapToObj(i -> sequenceUtil.getSeq("abc"))
                .collect(Collectors.toList()).stream().distinct().count() == 100000;
    }

}
