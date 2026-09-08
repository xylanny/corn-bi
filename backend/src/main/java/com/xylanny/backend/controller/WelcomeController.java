package com.xylanny.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping
    String welcomeGet(){
        return "welcome!";
    }

    @PostMapping
    String welcomePost(String name){
        return "welcome, " + name + "!";
    }
}

/**
 *
 * Tomcat处理HTTP请求的流程：
 * 1. 启动与监听端口
 * Spring Boot应用启动时，内嵌的Tomcat会随之初始化，并根据配置文件（如 application.yml 中的 server.port）
 * 在指定端口（默认 8080）上创建一个 Socket 服务端，开始监听来自前端的 HTTP 请求。
 *
 * 2. 接收请求并封装
 * 当Tomcat接收到一个HTTP请求时，它会解析底层的Socket数据流（包括请求行、请求头和请求体），
 * 然后自动将这些原始数据封装成一个标准的 HttpServletRequest对象，方便Java代码使用。
 *
 * 3. 执行业务逻辑
 * 封装好的HttpServletRequest对象会被交给Spring MVC的核心调度器（DispatcherServlet），
 * 再由它路由到你编写的具体处理器方法，执行后返回一个Java对象。
 *
 * 4. 将Java对象转为JSON并封装响应
 * 当处理器方法返回Java对象后，Spring会利用 HttpMessageConverter将这个对象转换为JSON字符串。
 * Tomcat会将该JSON字符串作为响应体，连同合适的响应头和状态码，组装成一个标准的HttpServletResponse对象。
 * 最终，Tomcat 会通过底层的 ServletOutputStream 将这个完整的 HTTP 响应写回到原先的 Socket 连接中，发送给前端。
 *
 * 5. 清理与线程归还
 * 响应发送完毕后，Tomcat 会清空本次请求相关的临时数据，并将处理该请求的工作线程归还给内部的线程池，以便高效地处理下一个请求。
 */
