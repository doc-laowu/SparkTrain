package Util

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

/**
  * @Title: TimeUtil
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2618:38
  */
object TimeUtil {

  def formatStr2TimeStamp(str: String): Timestamp ={

    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return new Timestamp(sdf.parse(str).getTime)

  }

  def TimeStamp2formatStr(time: Timestamp): String ={

    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    if(null == time)
      return null
    return sdf.format(new Date(time.getTime))

  }

}
