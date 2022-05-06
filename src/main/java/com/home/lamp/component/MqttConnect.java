package com.home.lamp.component;

import com.home.lamp.config.MQTTConfig;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MQTT服务端连接类
 */
@Component
public class MqttConnect {

    @Autowired
    private MQTTConfig config;

    public MqttConnect(MQTTConfig config) {
        this.config = config;
    }

    //创建MqttConnectOptions连接对象
    public MqttConnectOptions getOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(config.isCleansession());
        options.setUserName(config.getUsername());
        options.setPassword(config.getPassword().toCharArray());
        options.setConnectionTimeout(config.getConnectionTimeout());
        //设置心跳
        options.setKeepAliveInterval(config.getKeepalive());
        return options;
    }

    public MqttConnectOptions getOptions(MqttConnectOptions options) {

        options.setCleanSession(options.isCleanSession());
        options.setUserName(options.getUserName());
        options.setPassword(options.getPassword());
        options.setConnectionTimeout(options.getConnectionTimeout());
        options.setKeepAliveInterval(options.getKeepAliveInterval());
        return options;
    }
}
