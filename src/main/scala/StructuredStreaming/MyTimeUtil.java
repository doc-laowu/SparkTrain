package StructuredStreaming;//package StructuredStreaming;

import org.apache.commons.lang.time.FastDateFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: MyTimeUtil
 * @ProjectName Real_Dws_Data_Statistics
 * @Description: 用户自定义事件工具
 * @Author gaosen
 * @Date 2019/3/2512:24
 */
public class MyTimeUtil implements Serializable {

    private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化时间字符串
     * @param strTime
     * @return
     * @throws ParseException
     */
    public static String formatTime(String strTime) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timeRet = fdf.format(sdf.parse(strTime));

        return timeRet;
    }

    /**
      * @Author: yisheng.wu
      * @Description 格式化时间字符串
      * @Date 11:00 2019/3/26
      * @Param [timestamp]
      * @return java.lang.String
      **/
    public static Timestamp formatTime2TimeStamp(Long timestamp) throws Exception {

        try {

            Timestamp ts = new Timestamp(timestamp);
            return ts;
        }catch (Exception e){
            throw new Exception("transfer the unix timestamp to the sql timestamp error!");
        }
    }

    /**
      * @Author: yisheng.wu
      * @Description 将字符串的时间转为时间戳
      * @Date 10:37 2019/3/27
      * @Param [timestamp]
      * @return java.sql.Timestamp
      **/
    public static Timestamp formatTime2TimeStamp(String timestamp) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatTime2TimeStamp(sdf.parse(timestamp).getTime());
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String CurrentTime() {

        return fdf.format(new Date());
    }

}
