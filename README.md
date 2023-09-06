## Masachi Backend Core

Inspired by Mr. Fu

### TODO List

- [x] MySQL
- [x] PGSQL
- [x] Kafka
- [x] Redis
- [x] GitHub Pager
- [x] Jwt Token
- [x] Redis Distributed Lock
- [x] Eureka
- [x] MyBatis with [custom generator](https://github.com/masachi/mybatis-generator)
- [x] Swagger v3
- [ ] ~Nacos~ 辣鸡玩意儿，不支持```behind proxy```
- [ ] Pinpoint
- [x] Skywalking
- [ ] Apollo Config
- [ ] ELK
- [x] GraphQL ~```有点蠢，也可能是我不会用吧```~
- [x] MongoDB
- [x] Spring Cloud Gateway ~```用于多个微服务前的网关层 用于token验证此类 一般用不上```~ ```Spring Cloud Gateway只能是异步模型 也就是 reactive 模型才能使用```
- [x] Elasticsearch Sample
- [ ] Hystrix
- [x] Rate Limit
- [x] MinIO w/o testing
- [x] Sentinel ```仅仅加入了pom中，一般从sentinel admin 中配置相关规则生效，除非大型系统，不然不建议使用```
- [x] WebSocket
- [ ] WebFlux ```Servlet迁移到WebFlux难度很大```
- [x] Caffeine
- [ ] R2DBC ```R2DBC 需要 WebFlux 支持，同时Pager 暂时没看到有支持，需要手动Query```
- [x] HTTP/2 w/o testing ```目前没测试出支持，会报错 socket hang up```
- [x] Parameter Validation via ```spring-boot-starter-validation```
- [x] Scheduler w/o testing
- [x] 幂等参数生成
- [ ] SpEL
- [x] Mail Send w/o testing
- [x] MySql / PGSQL Data Modify Interceptor
- [x] Database Exception Interceptor

### Feature
- Request & Response Encryption
- Bundle with appassembler
- MySQL Preheat
- NanoID in encrypt
