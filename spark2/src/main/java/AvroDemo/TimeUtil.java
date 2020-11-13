package AvroDemo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
  * @Title: TimeUtil
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2618:38
  */
class TimeUtil {

  public static Timestamp formatStr2TimeStamp(String str) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return new Timestamp(sdf.parse(str).getTime());

  }

  public static String TimeStamp2formatStr(Timestamp time) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if(null == time)
      return null;
    return sdf.format(new Date(time.getTime()));

  }

}
