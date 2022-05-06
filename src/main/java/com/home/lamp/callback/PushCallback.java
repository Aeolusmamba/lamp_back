package com.home.lamp.callback;

import com.home.lamp.bean.LampStatus;
import com.home.lamp.component.SpringUtil;
import com.home.lamp.dao.SubscriberDao;
import com.home.lamp.serviceimpl.MQTTSubscriber;
import com.home.lamp.tools.CurrentTime;
import com.home.lamp.tools.CurrentTimeMillis;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;


public class PushCallback implements MqttCallback {


    private static final Logger LOGGER = LoggerFactory.getLogger(PushCallback.class);

    private MQTTSubscriber mqttSubscriber;

    public PushCallback(MQTTSubscriber subscriber) {
        this.mqttSubscriber = subscriber;
    }


    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        LOGGER.info("---------------------连接断开，可以做重连");
        mqttSubscriber.connectCallback();  // 断连则调用连接方法

        while (true){
            try {
                //如果没有发生异常说明连接成功，如果发生异常，则死循环
                Thread.sleep(1000);
                break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息，消息到达后处理方法
     * 这里用不上
     * @param token
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    /**
     * 接收订阅的主题的消息,并处理
     * @param topic
     * @param message
     */
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        String result = new String(message.getPayload(), StandardCharsets.UTF_8);
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + message.getQos());
        System.out.println("接收消息内容 : " + result);
        try{
            //这里可以针对收到的消息做处理，比如持久化...
            JSONObject jsonObject = JSONObject.parseObject(result);

//            Map<String,Object> MapJson = jsonObject.getInnerMap();

            //实例化入库方法 这里就用到SpringUtil类 来手动的注入
            MQTTSubscriber mqttSubscriber = SpringUtil.getBean(MQTTSubscriber.class);
            //调用入库方法
            mqttSubscriber.LampStatusStore(jsonObject);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}