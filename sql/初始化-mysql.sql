create table user_t
(
    user_id  VARCHAR(32) not null comment '用户ID，登录用',
    username VARCHAR(32) null comment '用户展示的ID',
    passwd   VARCHAR(64) null comment '用户密码，需加密',
    constraint user_t_pk
        primary key (user_id)
);

create table task_t
(
    id               int auto_increment,
    done             tinyint(1)    not null,
    priority         char          not null,
    start_date       date default '1970-01-01' not null,
    end_date         date default '2099-12-31' not null,
    task_description varchar(1024) not null comment '除是否完成，优先级，时间之外的东西，其中KVTag需要另外存储',
    user_id          varchar(32)   not null,
    constraint task_t_pk
        primary key (id)
);

create table task_tag_t
(
    id      INT(10) auto_increment,
    type    CHAR(16)     not null comment 'tag的类型',
    value   varchar(256) not null comment '整个 tag 的值',
    task_id INT(10)      not null comment 'tag对应的task',
    constraint task_tag_t_pk
        primary key (id)
);

