package me.yuuki.todoapp.controller;

import me.yuuki.todoapp.dto.Result;
import me.yuuki.todoapp.exception.ClientException;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/")
public class HelloController {

    private final String[] msgs = {
            "Hello, World!",
            "不管要花多少年 不管分开多么远，我们总有一天会得到幸福",
            "实践是检验真理的唯一标准",
            "扫帚不到，灰尘照例不会自己跑掉",
            "感觉到了的东西，我们不能立刻理解它；只有理解了的东西，才能更深刻地感觉它",
            "不积跬步，无以至千里；不积小流，无以成江海"
    };

    @GetMapping(value = "/alive")
    public Result<Integer> alive() {
        return Result.ok(42,  msgs[new Random().nextInt(msgs.length)]);
    }
}
