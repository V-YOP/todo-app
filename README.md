# todo app

预备自己实现一个 todo app，仅提供网页端去使用，考虑之后允许多用户使用。

当前服务器地址：`//service-k7zugq20-1259498433.gz.apigw.tencentcs.com/release/`

# 食用指南

总之需要在 classpath 里创建 `oss.properties`，配置这四个配置——

- oss.bucket-name
- oss.endpoint
- oss.access-key-id
- oss.access-key-secret

> 这玩意可不能传到 git 上。

# 目的

主要目的是学习 Spring Boot 的最佳实践，以及实践一些 OOD（如果有可能的话），为此，在该项目的实现中应达到——

1. 根据最佳实践去进行配置，校验，以及业务代码的编写（可惜当前没法有数据库）
2. 对所进行的配置进行详尽介绍

# TODO

- [ ] 实现控制器切面用来优雅处理异常
- [ ] 为 KVTable 和 OSSUtil 实现更多方法

以及最主要的：实现 todo，必须要实现 todo 的 CRUD；todo 需要能被格式化输出；