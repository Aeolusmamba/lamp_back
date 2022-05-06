package com.home.lamp.serviceimpl;

import com.alibaba.fastjson.JSONObject;
import com.home.lamp.bean.LampStatus;
import com.home.lamp.callback.PushCallback;
import com.home.lamp.component.MqttConnect;
import com.home.lamp.config.MQTTConfig;
import com.home.lamp.dao.SubscriberDao;
import com.home.lamp.tools.CurrentTime;
import com.home.lamp.tools.CurrentTimeMillis;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(rollbackFor = RuntimeException.class)
@Service
public class MQTTSubscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTSubscriber.class);

    @Autowired
    SubscriberDao subscriberDao;

    /* 客户端对象 */
    public MqttClient client;

    @Autowired
    private MQTTConfig mqttConfig;

    @Autowired
    private MqttConnect mqttConnect;

    /**
     * connectCallback：回调类对象（PushCallBack）调用的连接方法，实则功能和connect一样
     */
    public void connectCallback() {
        try {
            if (client==null) {
                System.out.println("新的clientId");
                String clientId = mqttConfig.getClientid() + "-" + CurrentTime.getCurrentTime();
                client = new MqttClient(mqttConfig.getHost(), clientId, new MemoryPersistence());
            }else{
                System.out.println("client not null");
            }
            MqttConnectOptions options = mqttConnect.getOptions();
            //判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!client.isConnected()) {
                System.out.println("111");
                client.connect(options);
            }else {//这里的逻辑是如果连接成功就重新连接
                System.out.println("222");
                client.disconnect();
                client.connect(mqttConnect.getOptions(options));
            }

            LOGGER.info("-----回调-----客户端连接成功");
        } catch (MqttException e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage(), e);
        }
    }

    /**
     * 方法实现说明：
     * 断线重连方法，如果是持久订阅，重连是不需要再次订阅
     * 如果是非持久订阅，重连是需要重新订阅主题 取决于options.setCleanSession(true);
     * true为非持久订阅
     */
    public void connect() {
        try {
            //防止重复创建MQTTClient实例
            if (client==null) {
                //clientId不能和其它的clientId一样，否则会出现频繁断开连接和重连的问题
                System.out.println("新的clientId");
                String clientId = mqttConfig.getClientid() + "-" + CurrentTime.getCurrentTime();
                System.out.println("clientId: " + clientId);
                client = new MqttClient(mqttConfig.getHost(), clientId, new MemoryPersistence());// MemoryPersistence设置clientid的保存形式，默认为以内存保存
                //如果是订阅者则添加回调类，发布者则不需要，PushCallback类在后面
                client.setCallback(new PushCallback(MQTTSubscriber.this));  // 注册回调方法
            }
            MqttConnectOptions options = mqttConnect.getOptions();
            //判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!client.isConnected()) {
                client.connect(options);
            }else {  //这里的逻辑是如果连接成功就重新连接
                client.disconnect();
                client.connect(mqttConnect.getOptions(options));
            }
            LOGGER.info("----------客户端连接成功");
        } catch (MqttException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    /**
     * 订阅端订阅消息
     * @param topic 要订阅的主题
     * @param qos 订阅消息的级别
     */
    public void init(String topic, int qos) {
        //建立连接
        connect();
        //以某个消息级别订阅某个主题
        subscribe(topic, qos);
    }

    /**
     * 订阅端取消订阅消息
     * @param topic 要取消订阅的主题
     */
    public void cancelInit(String topic) {
        //建立连接
        connect();
        //取消订阅某个主题
        unsubscribe(topic);
    }

    /**
     * 订阅某个主题
     *
     * @param topic .
     * @param qos .
     */
    public void subscribe(String topic, int qos) {

        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    /**
     * 取消订阅某个主题
     *
     * @param topic 要取消的主题
     */
    public void unsubscribe(String topic) {

        try {
            //MQTT 协议中订阅关系是持久化的，因此如果不需要订阅某些 Topic，需要调用 unsubscribe 方法取消订阅关系。
            client.unsubscribe(topic);
        } catch (MqttException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    public void LampStatusStore(JSONObject jsonObject){
        try{
            Integer ledState = jsonObject.getInteger("ledState");
            Float temperature = jsonObject.getFloat("temperature");
            Float humidity = jsonObject.getFloat("humidity");
            Integer light = jsonObject.getInteger("light");
            System.out.println(ledState);
            System.out.println(temperature);
            System.out.println(humidity);
            System.out.println(light);
            LampStatus lastLampStatus = subscriberDao.getLastLedState();
            if(lastLampStatus == null){
                lastLampStatus = new LampStatus();
                lastLampStatus.setLedState(0);
            }
            System.out.println("------------");
            System.out.println(lastLampStatus.getLedState());
            System.out.println(lastLampStatus.getTemperature());
            System.out.println(lastLampStatus.getHumidity());
            System.out.println(lastLampStatus.getLight());
            System.out.println(lastLampStatus.getUpdateTime());
            String updateTime = CurrentTime.getCurrentTime("yyyy-MM-dd HH:mm:ss");
            // 当前灯关闭，上一次最新状态是开启，则计算持续时间
            if(ledState != null && ledState.equals(0) && lastLampStatus.getLedState().equals(1)){
                System.out.println("111");
                String curDate = CurrentTime.getCurrentTime("YYYY-MM-dd");
                long lastUpdateTimeMillis = CurrentTimeMillis.getCurrentTimeMillis(lastLampStatus.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
                long duration = System.currentTimeMillis()/1000 - lastUpdateTimeMillis/1000;
                subscriberDao.addDuration(curDate, duration);
            }else if(ledState != null && ledState.equals(1) && lastLampStatus.getLedState().equals(1)){  //若当前状态和上一次的状态都是1，则延用更新时间
                updateTime = lastLampStatus.getUpdateTime();
            }else if(ledState != null && ledState.equals(1) && lastLampStatus.getLedState().equals(0)){  //当前灯开启，上一次灯关闭，增加开启次数
                String curDate = CurrentTime.getCurrentTime("YYYY-MM-dd");
                subscriberDao.addOpenTimes(curDate);
            }

            //更新设备状态
            subscriberDao.updateLampStatus(ledState, light, Math.round(humidity), Math.round(temperature), updateTime);

        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
