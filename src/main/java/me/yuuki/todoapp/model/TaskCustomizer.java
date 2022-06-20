package me.yuuki.todoapp.model;

/**
 * 根据 KV Tag 对 Task 进行修改或造成副作用
 */
public interface TaskCustomizer {
    String[] matchKVTag();
    Task customize(Task task);
}

