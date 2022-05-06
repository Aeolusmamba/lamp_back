package com.home.lamp.dao;


import com.home.lamp.bean.LampStatus;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubscriberDao {
    /**
     * 根据token值查询auth id
     */
    @Select("select id from auth where token=#{token}")
    Integer getAuth(@Param("token") String token);

    /**
     * 获取上一次的led 状态
     * @return
     */
    @Select("select * from lampstatus order by updateTime desc limit 1")
    LampStatus getLastLedState();

    /**
     * 更新持续时间
     * @param curDate
     * @param duration
     */
    @Update("update power set duration = duration + #{duration} where cur_date = #{curDate}")
    void addDuration(String curDate, long duration);


    /**
     * 更新设备状态
     * @param ledState
     * @param temperature
     * @param humidity
     * @param light
     * @param updateTime
     */
    @Insert("insert into lampstatus values(#{ledState}, #{light}, #{humidity}, #{temperature}, #{updateTime})")
    void updateLampStatus(@Param("ledState") Integer ledState, @Param("light") Integer light, @Param("humidity") Integer temperature, @Param("temperature") Integer humidity, @Param("updateTime") String updateTime);

    /**
     * 更新开灯次数和时间
     * @param curDate
     */
    @Update("update power set openTimes = openTimes + 1 where cur_date = #{curDate}")
    void addOpenTimes(String curDate);

}
