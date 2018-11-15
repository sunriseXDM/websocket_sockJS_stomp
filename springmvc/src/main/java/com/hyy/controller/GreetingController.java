package com.hyy.controller;

import com.hyy.model.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class GreetingController {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    public SimpMessagingTemplate template;

    @Autowired
    public GreetingController(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * 表示服务端可以接收客户端通过主题“/app/hello”发送过来的消息，客户端需要在主题"/topic/hello"上监听并接收服务端发回的消息
     */
    @MessageMapping("/hello") //"/hello"为WebSocketConfig类中registerStompEndpoints()方法配置的
    @SendTo("/topic/hello")
    public Greeting greeting(String message, @Header("atytopic") String topic, @Headers Map<String, Object> headers) {
        System.out.println("connected successfully....");
        System.out.println(topic);
        System.out.println(headers);
        System.out.println(message);
        return new Greeting(message);
    }

    /*
     * 相似于@RequestMapping
     */
    @MessageMapping("/init")
    @SendTo("/topic/hello")
    public String init(@RequestParam String msg) {
        System.out.println("msg:"+msg);
        //向/topic/hello监听端口广播发送消息，可用于服务器主动向客户端发送数据
        simpMessageSendingOperations.convertAndSend("/topic/hello",new Greeting(msg));
        return "init";
    }

    /**
     * 功能描述:
     * stomp中无法代替@MessageMapping("/init")
     * ！！！！！！stomp中该方法无效，可用ajax实现！！！！！！
     * @Author: maxudong
     * @Date:   2018/11/14/014
     */
    @RequestMapping(path = "/init2", method = RequestMethod.GET)
    @SendTo("/topic/hello")
    public Greeting init2(@RequestParam String msg) {
        System.out.println("msg2:"+msg);
        //向/topic/hello监听端口广播发送消息，可用于服务器主动向客户端发送数据
        simpMessageSendingOperations.convertAndSend("/topic/hello",new Greeting(msg));
        return new Greeting("init2");
    }

    /**
     * 这里用的是@SendToUser，这就是发送给单一客户端的标志。本例中，
     * 客户端接收一对一消息的主题应该是“/user/” + 用户Id + “/message” ,这里的用户id可以是一个普通的字符串，只要每个用户端都使用自己的id并且服务端知道每个用户的id就行。
     * ！！！！！待测！！！！！！
     * @return
     */
    @MessageMapping("/message")
    @SendToUser("/user/message")
    public Greeting handleSubscribe(Greeting message) {
        System.out.println("this is the @SendToUser");
        simpMessageSendingOperations.convertAndSendToUser("sunrise","/user/message",message);
        return new Greeting("I am a msg from handleSubscribe()");
    }

}
