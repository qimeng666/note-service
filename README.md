# 笔记微服务 (Note Service)

## JWT 验证流程说明

### 1. JWT 配置

项目使用 Spring Security OAuth2 Resource Server 进行 JWT 验证（与 user service 使用相同的 secret）：

```yaml
jwt:
  secret: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
```

### 2. 安全配置

在`SecurityConfig`中配置了以下安全规则：

- 关闭 CSRF 保护
- GET 请求的`/notes/**`和`/tags/**`允许匿名访问
- 其他所有请求需要 JWT 认证
- 使用 OAuth2 Resource Server 进行 JWT 验证

### 3. JWT 解码器配置

`JwtDecoderConfig`配置了 JWT 解码器：

- 使用 HS512 算法
- 从配置文件中读取密钥
- 支持 JWT 令牌的验证和解析

### 4. 控制器中的 JWT 使用

在`NoteController`中，受保护的接口通过`@AuthenticationPrincipal Jwt jwt`参数获取 JWT：

```java
@PostMapping("/notes")
public ResponseEntity<Note> create(@AuthenticationPrincipal Jwt jwt, @RequestBody NoteRequest req) {
    Long userId = jwt.getClaim("userId");
}
```

### 5. 跨服务 JWT 传递

通过`FeignAuthInterceptor`实现 JWT 令牌在微服务间的传递：

```java
@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String token = jwt.getTokenValue();
            template.header("Authorization", "Bearer " + token);
        }
    }
}
```

### 6. 验证流程

1. **客户端请求**: 客户端在请求头中携带 JWT 令牌

   ```
   Authorization: Bearer <jwt_token>
   ```

2. **Spring Security 验证**: Spring Security 自动验证 JWT 的有效性

   - 检查签名
   - 验证过期时间
   - 验证算法

3. **提取用户信息**: 从 JWT 中提取用户 ID 进行业务逻辑处理

4. **跨服务调用**: 通过 Feign 拦截器自动将 JWT 传递给用户服务

### 7. 调试接口

项目提供了调试接口来查看 JWT 信息：

- `GET /debug/jwt`: 显示 JWT 的所有声明信息
- `GET /debug/user/{userId}`: 测试用户服务调用

## Feign 熔断器/重试机制配置

### 1. 熔断器配置 (Resilience4j)

在`application.yml`中配置了熔断器参数：

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        registerHealthIndicator: true
        slidingWindowSize: 10 # 滑动窗口大小
        minimumNumberOfCalls: 5 # 最小调用次数
        permittedNumberOfCallsInHalfOpenState: 3 # 半开状态允许的调用数
        failureRateThreshold: 50 # 失败率阈值(50%)
        waitDurationInOpenState: 30s # 熔断器开启状态等待时间
```

### 2. 重试机制配置

```yaml
resilience4j:
  retry:
    instances:
      userService:
        maxAttempts: 5 # 最大重试次数
        waitDuration: 1s # 重试间隔时间
        retryExceptions:
          - feign.RetryableException
          - org.springframework.web.client.HttpServerErrorException
```

### 3. Feign 客户端配置

在`UserClient`中配置了熔断器：

```java
@FeignClient(
        name = "user-service",
        url = "${user.service.url}",
        fallbackFactory = UserClientFallbackFactory.class,  // 熔断器工厂
        configuration = FeignConfig.class
)
```

### 4. 熔断器降级处理

`UserClientFallbackFactory`实现了降级逻辑：

```java
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return userId -> {
            if (cause instanceof FeignException.NotFound) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId, cause);
            }
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "User service unavailable",
                    cause
            );
        };
    }
}
```

### 5. 超时配置

在`FeignConfig`中设置了请求超时：

```java
@Bean
public Request.Options requestOptions() {
    return new Request.Options(
            5000,  // 连接超时5秒
            10000, // 读取超时10秒
            true   // 允许重定向
    );
}
```

### 6. 工作机制

1. **正常状态**: 服务正常调用，统计成功/失败率
2. **熔断开启**: 当失败率超过 50%时，熔断器开启，直接返回降级响应
3. **半开状态**: 30 秒后进入半开状态，允许少量请求尝试恢复
4. **重试机制**: 对于可重试的异常，最多重试 5 次，间隔 1 秒
5. **降级处理**: 服务不可用时返回 503 状态码，用户不存在时返回 404 状态码
