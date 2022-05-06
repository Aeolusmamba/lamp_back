package com.home.lamp.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

/*
 * 产生随机数就应该用一个对象来生成，这样可以避免随机数的重复。
 * 所以设计成单例
 * */
public class TokenProcessor {


    private TokenProcessor() {
    }

    private final static TokenProcessor TOKEN_PROCESSOR = new TokenProcessor();

    public static TokenProcessor getInstance() {
        return TOKEN_PROCESSOR;
    }


    public String makeToken() {


        //这个随机生成出来的Token的长度是不确定的
        String token = String.valueOf(System.currentTimeMillis() + new Random().nextInt(99999999));

        try {
            //我们想要随机数的长度一致，就要获取到数据指纹
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] md5 = messageDigest.digest(token.getBytes());

            //如果我们直接 return  new String(md5)出去，得到的随机数会乱码。
            //因为随机数是任意的01010101010，在转换成字符串的时候，会查gb2312的码表，gb2312码表不一定支持该二进制数据，得到的就是乱码

            //于是乎经过base64编码成了明文的数据
            Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

}