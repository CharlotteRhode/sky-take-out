package com.sky.server;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@ServerEndpoint("/ws/{sid}") // web socket来单提醒+催单
public class WebSocketServer {

    private static Map<String, Session> sessionMap = new HashMap<>();



    @OnOpen
    public void open(Session session, @PathParam("sid") String sid){
            log.info("链接建立, {}", sid);
            sessionMap.put(sid, session);//每建立一个连接，就存到这个map里
    }


    @OnMessage
    public void receiveMsg(Session session, String message, @PathParam("sid") String sid){
            log.info("收到消息, {}", message);
    }

    //服务器 -> 向客户端 群发消息：
    public void sendMsg(String message) throws IOException {
        Collection<Session> allSessions = sessionMap.values();
        for (Session each : allSessions) {
            each.getBasicRemote().sendText(message);
        }

    }

    @OnClose
    public void close(Session session, @PathParam("sid") String sid){
        log.info("链接关闭,{}", sid);
        sessionMap.remove(sid);
    }

    @OnError
    public void error(Session session, @PathParam("sid") String sid, Throwable throwable){
        throwable.printStackTrace();
        log.info("出现异常");
    }


}
