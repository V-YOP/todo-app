package me.yuuki.todoapp.config;

import me.yuuki.todoapp.property.OSSProperty;
import me.yuuki.todoapp.util.oss.AliyunOSSUtilImpl;
import me.yuuki.todoapp.util.oss.MockOSSUtilImpl;
import me.yuuki.todoapp.util.oss.OSSUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSSConfiguration {
    private final OSSProperty ossProperty;

    public OSSConfiguration(OSSProperty ossProperty) {
        this.ossProperty = ossProperty;
    }

    @Bean("ossUtil")
    OSSUtil ossUtil() {
        // 这里直接显式配置即可（就像某种简单工厂方法）
        switch (ossProperty.getOssTarget()) {
            default: // Impossible
            case ALIYUN:
                return new AliyunOSSUtilImpl(ossProperty);
            case MOCK:
                return new MockOSSUtilImpl(ossProperty);
        }

    }


}
