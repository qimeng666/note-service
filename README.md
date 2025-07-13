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
