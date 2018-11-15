package com.hyy.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * 复写了 registerStompEndpoints() 方法：添加一个服务端点，来接收客户端的连接。将 "/endpointChat" 路径注册为 STOMP 端点。
     * 这个路径与发送和接收消息的目的路径有所不同， 这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点，
     * 即用户发送请求 ：url="/127.0.0.1:8080/endpointChat" 与 STOMP server 进行连接，之后再转发到订阅url；
     */
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //添加一个/hello端点，客户端就可以通过这个端点来进行连接；withSockJS作用是添加SockJS支持
        stompEndpointRegistry.addEndpoint("/hello")
                ////添加连接登录验证
                .addInterceptors(new SessionAuthHandshakeInterceptor())
//                允许指定的域名或IP(含端口号)建立长连接
                .setAllowedOrigins("*")
                //添加SockJS支持
                .withSockJS();
    }

    /**
     * 复写了 configureMessageBroker() 方法：
     * 配置了一个 简单的消息代理，通俗一点讲就是设置消息连接请求的各种规范信息。
     * 发送应用程序的消息将会带有 “/app” 前缀。
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //表示在topic和user这两个域上可以向客户端发消息
        registry.enableSimpleBroker("/topic", "/user");
        //客户端向服务端发送时的主题上面需要加"/app"作为前缀
        registry.setApplicationDestinationPrefixes("/app");
        //表示给指定用户发送（一对一）的主题前缀是“/user/”
        registry.setUserDestinationPrefix("/user/");
    }
}
