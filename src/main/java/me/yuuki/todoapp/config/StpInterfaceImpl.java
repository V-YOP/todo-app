package me.yuuki.todoapp.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.singletonList("42");
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return Collections.singletonList("user");
    }
}
