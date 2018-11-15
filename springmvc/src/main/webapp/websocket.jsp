<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    response.setHeader("Access-Control-Allow-Origin", "*");
%>

<html lang="en">
<head>
    <title>Hello WebSocket</title>
    <meta http-equiv="Access-Control-Allow-Origin" content="*" />
    <script src="http://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.js"></script>
    <script src="http://cdn.bootcss.com/stomp.js/2.3.3/stomp.js"></script>
    <script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            connect();
        });

        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var userid = document.getElementById('name').value;

            // 建立连接对象（还未发起连接）
            var socket = new SockJS("http://localhost:8080/hello");
            // 获取 STOMP 子协议的客户端对象
            stompClient = Stomp.over(socket);
            // 向服务器发起websocket连接并发送CONNECT帧
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);

                //订阅一个消息,监听后台返回广播消息
                stompClient.subscribe('/topic/hello', onmessage);
                //一对一通讯未实现，暂不考虑
                // stompClient.subscribe('/topic/sunrise/message',onmessage);

                //向服务器发送信息,与@RequestMapping相似的传值方法
                stompClient.send("/app/init",{},"stomp is coming");

                //不使用stomp，ajax请求也可行通
                $.ajax({
                    url: '/init2',
                    type: "get",
                    cache: false,
                    //async:false,
                    'data':{'msg':"ajax is coming"},
                    success: function (data) {
                        alert("ajax返回数据："+data.content);
                    }
                })


            });
        }

        //如果想让客户端订阅多个目的地，你可以在接收所有信息的时候调用相同的回调函数：
        onmessage = function(response) {
            alert(response);
            showGreeting(JSON.parse(response.body).content);
        }

        function sendName() {
            var name = document.getElementById('name').value;
            //前端向后台发送数据
            // stompClient.send("/app/hello", {atytopic:"greetings"}, JSON.stringify({ 'name': name }));
            stompClient.send("/app/hello", {atytopic:"greetings"}, name);
        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }


        function showGreeting(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
    </script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div id="conversationDiv">
        <label>What is your name?</label><input type="text" id="name" />
        <button id="sendName" onclick="sendName();">Send</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>
