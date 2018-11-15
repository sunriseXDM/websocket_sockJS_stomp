package com.hyy.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 连接时验证用户是否登录
 */
public class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        /*
        UserDO user = ShiroUtils.getUser();
        if (user == null) {
            logger.error("websocket权限拒绝:用户未登录");
            return false;
        }
        //attributes.put("user", user);
        */
        System.out.println("beforeHandshake");
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
        System.out.println("afterHandshake");
    }

}