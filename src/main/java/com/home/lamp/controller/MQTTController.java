package com.home.lamp.controller;


import com.home.lamp.bean.Result;
import com.home.lamp.callback.PushCallback;
import com.home.lamp.serviceimpl.MQTTPublisher;
import com.home.lamp.serviceimpl.MQTTSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MQTTController {
    @Autowired
    MQTTPublisher mqttPublisher;

    @Autowired
    MQTTSubscriber mqttSubscriber;


    private String topic = "ctl";

    private String topic2 = "lamp";

    @RequestMapping("/mqtt/testPublish")
    public String testPublish(String topic, String msg, int qos) {
        mqttPublisher.sendMQTTMessage(topic, msg, qos);
        return "发送了一条主题是‘"+topic+"’，内容是:"+msg+"，消息级别 "+qos;
    }

    /**
     * 定时请求：订阅主题
     * @return
     */
    @RequestMapping(value = "/mqtt/subscribe", method = {RequestMethod.GET})
    public String subscribe() {
        mqttSubscriber.init(topic2, 0);
        System.out.println("订阅'"+topic2+"'成功");
        return "订阅'"+topic2+"'成功";
    }

    /**
     * 退订主题
     * @return
     */
    @RequestMapping("/mqtt/unsubscribe")
    public String unsubscribe() {
        mqttSubscriber.cancelInit(topic2);
        return "取消订阅'"+topic2+"'成功";
    }


    /**
     * 前端每过3秒请求一次接口，返回状态的同时持续发布主题"ctl"
     * @param token
     * @return
     */
    @RequestMapping(value = "/api/lamp/connect", method = {RequestMethod.GET})
    public Result returnConnectStatus(@RequestHeader("Authorization") String token) {
        token = token.replaceAll(" ", "\\+");
        return mqttPublisher.returnConnectStatus(token, topic);
    }

    /**
     * 客户端发出开关灯控制指令
     * @param token
     * @param command
     * @return
     */
    @RequestMapping(value = "/api/lamp/control", method = {RequestMethod.GET})
    public Result control(@RequestHeader("Authorization") String token, @RequestParam("command") Integer command) {
        token = token.replaceAll(" ", "\\+");
        return mqttPublisher.control(token, topic, command);
    }

    /**
     * 返回设备状态情况
     * @param token
     * @return
     */
    @RequestMapping(value = "/api/report/device", method = {RequestMethod.GET})
    public Result returnDeviceStatus(@RequestHeader("Authorization") String token) {
        token = token.replaceAll(" ", "\\+");
        return mqttPublisher.returnDeviceStatus(token);
    }


    /**
     * 返回本月每天电量使用情况
     * @param token
     * @return
     */
    @RequestMapping(value = "/api/report/power", method = {RequestMethod.GET})
    public Result returnPower(@RequestHeader("Authorization") String token) {
        token = token.replaceAll(" ", "\\+");
        return mqttPublisher.returnPower(token);
    }

}
