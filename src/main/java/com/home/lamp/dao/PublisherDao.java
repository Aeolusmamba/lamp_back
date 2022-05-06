package com.home.lamp.dao;

import com.home.lamp.bean.LampStatus;
import com.home.lamp.bean.Power;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PublisherDao {

    /**
     * 根据token值查询menuAuth是0还是1
     */
    @Select("select id from auth where token=#{token}")
    Integer getAuth(@Param("token") String token);


    /**
     * 获取最后一条状态的更新时间
     * @return
     */
    @Select("select updateTime from lampstatus order by updateTime desc limit 1")
    String getLastUpdate();

    /**
     * 得到今天开启灯泡的次数
     * @param curDate
     * @return
     */
    @Select("select openTimes from power where cur_date=#{cur_date}")
    Integer getOpenTimes(@Param("cur_date") String curDate);

    /**
     * 获取5条最近的设备状态
     * @return
     */
    @Select("select * from lampstatus order by updateTime desc limit 5")
    List<LampStatus> get5DeviceStatus();

    /**
     * 得到本月每天的用电情况
     * @param curMonth
     * @return
     */
    @Select("select * from power where cur_date like concat(#{curMonth},'%')")
    List<Power> getCurMonthPower(@Param("curMonth") String curMonth);


}
