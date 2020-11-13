package SparkSql

import DbSync.Write2Es
import org.apache.spark.sql.SparkSession

/**
  * @Title: Write2Es
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2020/4/816:20
  */
case class business_consumer(business_uid: Long, consumer_uid: String, bu: Long, total_tt: Long, country: String, province: String, city:String, first_join:String, laster_join: String, join_nums:Long, invite_nums:Long,
                  channel:String, channel_activity_id:String, age:Long, area:Long, data_integrity:Long, watch_period:Long, watch_times:Long, watch_tt:Long, share_channel:Long, share_effect:Long,
                  subscription:Long, common_device:Long, computer_common_browser:Long, mobile_common_browser:Long, user_level:Long, update_time:String)

object Write2EsTest {

  def GetIndexId(business_uid: String, consumer_uid: String): String ={
    val sb: StringBuilder = new StringBuilder()
    sb.append(business_uid)
      .append("_")
      .append(consumer_uid.replaceAll("-", ""))
      .toString()
  }

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    spark.udf.register("GetIndexId", (business_uid: String, consumer_uid: String)=>GetIndexId(business_uid, consumer_uid))

    val business_consumers = Seq(
      business_consumer(5100006, "26800005", 5, 123,"美国", "加利福利亚", "斯坦福大学", "2020-04-08 12:56:12", "2020-04-08 14:56:12", 9, 10, "1002", "123456789", 673, 123, 123, 123, 1213,
        123, 123, 123, 123, 123, 123, 12, 1, "2020-04-08 16:56:12"),
      business_consumer(5100006, "26900003", 5, 123, "美国", "加利福利亚", "斯坦福大学", "2020-04-08 12:56:12", "2020-04-08 14:56:12", 9, 10, "1002", "123456789", 673, 123, 123, 123, 1213,
        123, 123, 123, 123, 123, 123, 12, 1, "2020-04-08 16:56:12")
    )

    import spark.implicits._

    business_consumers.toDF().createOrReplaceTempView("consumer")

    val ret_DS= spark.sql(
      """
        |select
        |GetIndexId(c.business_uid, c.consumer_uid) as _id, c.*
        |from
        |consumer as c
      """.stripMargin)

    val script =
      """ctx._source.business_uid=params.business_uid;
        |ctx._source.consumer_uid=params.consumer_uid;
        |ctx._source.bu=params.bu;
        |ctx._source.total_tt=params.total_tt;
        |ctx._source.country=params.country;
        |ctx._source.province=params.province;
        |ctx._source.city=params.city;
        |ctx._source.first_join=params.first_join;
        |ctx._source.laster_join=params.laster_join;
        |ctx._source.join_nums=params.join_nums;
        |ctx._source.invite_nums=params.invite_nums;
        |ctx._source.channel=params.channel;
        |ctx._source.channel_activity_id=params.channel_activity_id;
        |if(ctx._source.activity_tags_array==null){ctx._source.activity_tags_array=new ArrayList();}
        |else{if(ctx._source.activity_tags_array.size()>0){ctx._source.activity_tags_array.clear();}}
        |if(params.age!=0){ctx._source.activity_tags_array.add(params.age);}
        |if(params.area!=0){ctx._source.activity_tags_array.add(params.area);}
        |if(params.data_integrity!=0){ctx._source.activity_tags_array.add(params.data_integrity);}
        |if(params.watch_period!=0){ctx._source.activity_tags_array.add(params.watch_period);}
        |if(params.watch_times!=0){ctx._source.activity_tags_array.add(params.watch_times);}
        |if(params.watch_tt!=0){ctx._source.activity_tags_array.add(params.watch_tt);}
        |if(params.share_channel!=0){ctx._source.activity_tags_array.add(params.share_channel);}
        |if(params.share_effect!=0){ctx._source.activity_tags_array.add(params.share_effect);}
        |if(params.subscription!=0){ctx._source.activity_tags_array.add(params.subscription);}
        |if(params.common_device!=0){ctx._source.activity_tags_array.add(params.common_device);}
        |if(params.computer_common_browser!=0){ctx._source.activity_tags_array.add(params.computer_common_browser);}
        |if(params.mobile_common_browser!=0){ctx._source.activity_tags_array.add(params.mobile_common_browser);}
        |ctx._source.user_level=params.user_level;
        |ctx._source.update_time=params.update_time;
      """.stripMargin.replaceAll("""\r|\n""", "")

    val params =
      """business_uid:business_uid,
        |consumer_uid:consumer_uid,
        |bu:bu,
        |total_tt:total_tt,
        |country:country,
        |province:province,
        |city:city,
        |first_join:first_join,
        |laster_join:laster_join,
        |join_nums:join_nums,
        |invite_nums:invite_nums,
        |channel:channel,
        |channel_activity_id:channel_activity_id,
        |age:age,
        |area:area,
        |data_integrity:data_integrity,
        |watch_period:watch_period,
        |watch_times:watch_times,
        |watch_tt:watch_tt,
        |share_channel:share_channel,
        |share_effect:share_effect,
        |subscription:subscription,
        |common_device:common_device,
        |computer_common_browser:computer_common_browser,
        |mobile_common_browser:mobile_common_browser,
        |user_level:user_level,
        |update_time:update_time
      """.stripMargin.replaceAll("""\r|\n""", "")

    println(s"script:${script}")

    println(s"params:${params}")

    Write2Es.Upsert2Es(ret_DS, script, params)


    spark.stop()

  }

}
