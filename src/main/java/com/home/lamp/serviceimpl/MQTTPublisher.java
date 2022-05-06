package com.home.lamp.serviceimpl;


import com.home.lamp.bean.*;
import com.home.lamp.component.MqttConnect;
import com.home.lamp.config.MQTTConfig;
import com.home.lamp.dao.PublisherDao;
import com.home.lamp.tools.CurrentTime;
import com.home.lamp.tools.CurrentTimeMillis;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Transactional(rollbackFor = RuntimeException.class)
@Service
public class MQTTPublisher {

    @Autowired
    PublisherDao publisherDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTPublisher.class);

    /* 客户端对象 */
    public MqttClient client;

    /* 主题对象 */
    public MqttTopic topic;

    /* 消息内容对象 */
    public MqttMessage message;

    @Autowired
    private MqttConnect mqttConnect;

    @Autowired
    private MQTTConfig config;

    public MQTTPublisher() {
        LOGGER.info("消息发布者上线了");
    }

    /**
     * 客户端和服务端建立连接
     */
    public MqttClient connect() {
        //防止重复创建MQTTClient实例
        try {
            if (client == null) {
                //先让客户端和服务器建立连接，MemoryPersistence设置clientid的保存形式，默认为以内存保存
                String clientId = config.getClientid() + "-" + CurrentTime.getCurrentTime();
                client = new MqttClient(config.getHost(), clientId, new MemoryPersistence());
                //发布消息不需要回调连接
                //client.setCallback(new PushCallback());
            }

            MqttConnectOptions options = mqttConnect.getOptions();
            //判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!client.isConnected()) {
                client.connect(options);
                LOGGER.info("---------------------连接成功");
            } else {//这里的逻辑是如果连接成功就重新连接
                client.disconnect();
                client.connect(mqttConnect.getOptions(options));
                LOGGER.info("---------------------连接成功");
            }
        } catch (MqttException e) {
            LOGGER.info(e.toString());
            e.printStackTrace();
        }
        return client;
    }

    /**
     * 发布消息
     * @param topic
     * @param message
     */
    public void publish(MqttTopic topic, MqttMessage message) {

        MqttDeliveryToken token;
        boolean flag = false;
        try {
            //把消息发送给对应的主题
            token = topic.publish(message);
            token.waitForCompletion();
            //检查发送是否成功
            flag = token.isComplete();

            StringBuilder stringBuilder = new StringBuilder(200);
            stringBuilder.append("给主题为'").append(topic.getName());
            stringBuilder.append("'发布消息：");
            if (flag) {
                stringBuilder.append("成功！消息内容是：").append(new String(message.getPayload()));
            } else {
                stringBuilder.append("失败！");
            }
            LOGGER.info(stringBuilder.toString());
        } catch (MqttException e) {
            LOGGER.info(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * MQTT发送指令
     *
     * @param topic 主题
     * @param data  消息内容
     * @param qos   消息级别
     */
    public void sendMQTTMessage(String topic, String data, int qos) {

        try {
            MQTTPublisher publisher = new MQTTPublisher();

            publisher.client = connect();
            publisher.topic = publisher.client.getTopic(topic);
            publisher.message = new MqttMessage();
            //消息等级
            //level 0：消息最多传递一次，不再关心它有没有发送到对方，也不设置任何重发机制
            //level 1：包含了简单的重发机制，发送消息之后等待接收者的回复，如果没收到回复则重新发送消息。这种模式能保证消息至少能到达一次，但无法保证消息重复
            //level 2： 有了重发和重复消息发现机制，保证消息到达对方并且严格只到达一次
            publisher.message.setQos(qos);
            //如果重复消费，则把值改为true,然后发送一条空的消息，之前的消息就会覆盖，然后在改为false
            publisher.message.setRetained(false);
            publisher.message.setPayload(data.getBytes());
            publisher.publish(publisher.topic, publisher.message);
        } catch (Exception e) {
            LOGGER.info(e.toString());
            e.printStackTrace();
        }

    }

    public Result returnConnectStatus(String token, String topic) {
        ConnectResult result = new ConnectResult();
        ConnectData connectData = new ConnectData();
        try{
            Integer authId = publisherDao.getAuth(token);
            if(authId == null){
                result.getMeta().setMsg("token无效");
                result.getMeta().setStatus(400);
                return result;
            }
            String updateTime = publisherDao.getLastUpdate();
            if(updateTime == null || updateTime.equals("")){
                result.getMeta().setMsg("没有数据");
                result.getMeta().setStatus(200);
                return result;
            }
            long updateTimeMillis = CurrentTimeMillis.getCurrentTimeMillis(updateTime, "yyyy-MM-dd HH:mm:ss");
            if(System.currentTimeMillis()/1000 > updateTimeMillis/1000 + 10){  //超过10秒数据库无数据更新，说明未连接
                connectData.setConnected(0);
                connectData.setOpened(0);
            }else{
                connectData.setConnected(1);
                connectData.setOpened(1);
            }
            String curDate = CurrentTime.getCurrentTime("YYYY-MM-dd");
            Integer openTimes = publisherDao.getOpenTimes(curDate);
            connectData.setTimes(openTimes);
            this.sendMQTTMessage(topic, "4", 0);  // 保活topic（ctl）
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(connectData);
        result.getMeta().setMsg("查询连接状态成功");
        result.getMeta().setStatus(200);
        return result;
    }


    public Result control(String token, String topic, Integer command) {
        ConnectResult result = new ConnectResult();
        ConnectData connectData = new ConnectData();
        try{
            Integer authId = publisherDao.getAuth(token);
            if(authId == null){
                result.getMeta().setMsg("token无效");
                result.getMeta().setStatus(400);
                return result;
            }
            this.sendMQTTMessage(topic, command.toString(), 0);  // 发出指令
            String curDate = CurrentTime.getCurrentTime("YYYY-MM-dd");
            Integer openTimes = publisherDao.getOpenTimes(curDate);
            connectData.setConnected(1);
            connectData.setOpened(1);
            connectData.setTimes(openTimes);
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(connectData);
        result.getMeta().setMsg("发送指令成功");
        result.getMeta().setStatus(200);
        return result;
    }


    public Result returnDeviceStatus(String token) {
        DeviceStatusResult result = new DeviceStatusResult();
        DeviceStatusData deviceStatusData = new DeviceStatusData();
        ArrayList<ArrayList<Chart>> reportList = new ArrayList<>();
        try{
            Integer authId = publisherDao.getAuth(token);
            if(authId == null){
                result.getMeta().setMsg("token无效");
                result.getMeta().setStatus(400);
                return result;
            }
            List<LampStatus> lampStatusList = publisherDao.get5DeviceStatus();
            if(!lampStatusList.isEmpty()){
                for(LampStatus l: lampStatusList){
                    ArrayList<Chart> stateList2 = new ArrayList<>();
                    Chart chart1 = new Chart();
                    chart1.setxAxis(l.getUpdateTime());
                    chart1.setyAxis(l.getLedState());
                    stateList2.add(chart1);
                    Chart chart2 = new Chart();
                    chart2.setxAxis(l.getUpdateTime());
                    chart2.setyAxis(l.getLight());
                    stateList2.add(chart2);
                    Chart chart3 = new Chart();
                    chart3.setxAxis(l.getUpdateTime());
                    chart3.setyAxis(l.getTemperature());
                    stateList2.add(chart3);
                    Chart chart4 = new Chart();
                    chart4.setxAxis(l.getUpdateTime());
                    chart4.setyAxis(l.getHumidity());
                    stateList2.add(chart4);
                    reportList.add(stateList2);
                }
                deviceStatusData.setReportList(reportList);
                deviceStatusData.setTotal(reportList.size());
            }
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(deviceStatusData);
        result.getMeta().setMsg("查询设备状态成功");
        result.getMeta().setStatus(200);
        return result;
    }


    public Result returnPower(String token) {
        PowerResult result = new PowerResult();
        PowerData powerData = new PowerData();
        ArrayList<Chart> powerList = new ArrayList<>();
        try{
            Integer authId = publisherDao.getAuth(token);
            if(authId == null){
                result.getMeta().setMsg("token无效");
                result.getMeta().setStatus(400);
                return result;
            }
            String curMonth = CurrentTime.getCurrentTime("YYYY-MM");
            List<Power> powers = publisherDao.getCurMonthPower(curMonth);
            if(!powers.isEmpty()){
                for(Power p: powers){
                    Chart chart = new Chart();
                    chart.setxAxis(p.getCur_date());
                    float y = (float)(p.getDuration()/12.0);  // 10^(-5) kWh
                    chart.setyAxis(Math.round(y));
                    powerList.add(chart);
                }
                powerData.setPowerList(powerList);
                powerData.setTotal(powerList.size());
            }
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(powerData);
        result.getMeta().setMsg("查询用电情况成功");
        result.getMeta().setStatus(200);
        return result;
    }


//    public static void main(String[] args) throws MqttException {
//        //sendMQTTMessage("测试","我是发布端", 0);
//    }
}
