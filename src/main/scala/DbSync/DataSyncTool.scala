package DbSync

import java.sql.Timestamp

import Util.TimeUtil
import com.github.binarywang.java.emoji.EmojiConverter
import org.apache.spark.sql.{Dataset, Row, SparkSession, functions}

/**
  * @Title: consumer_business_info_sync
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2710:36
  */
object DataSyncTool {

  def emojiConverter(nickname: String): String ={
    if(nickname == null)
      return null;
    val emojiConverter = EmojiConverter.getInstance();
    val content = emojiConverter.toAlias(nickname);//将聊天内容进行转义
    return content
  }

  def GetIndexId(business_uid: Long, consumer_uid: String): String ={

    val sb: StringBuilder = new StringBuilder()

    sb.append(business_uid)
      .append("_")
      .append(consumer_uid.replaceAll("-", ""))
      .toString()
  }

  def TimeStamp2Date(timestamp: Timestamp): String ={

    TimeUtil.TimeStamp2formatStr(timestamp)

  }


  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    spark.udf.register("emojiConverter", (nickname: String)=>emojiConverter(nickname))

    spark.udf.register("getIndexId", (business_uid: Long, consumer_uid: String)=>GetIndexId(business_uid, consumer_uid))

    spark.udf.register("TimeStamp2Date", (timestamp: Timestamp)=>TimeStamp2Date(timestamp))

//
//    val query_1 = """SELECT
//                    |consumer_uid,
//                    |account_id,
//                    |nickname,
//                    |username,
//                    |avatar,
//                    |phone,
//                    |email,
//                    |industry,
//                    |position,
//                    |source,
//                    |channel,
//                    |sex,
//                    |real_name,
//                    |education_level,
//                    |country,
//                    |province,
//                    |city,
//                    |area,
//                    |if(birthday = '0000-00-00 00:00:00', null, birthday) as birthday,
//                    |last_visited_ip,
//                    |is_activated,
//                    |deleted as id_deleted
//                    |FROM consumer""".stripMargin
//
//    val ds_0: Dataset[Row] = ReadFromDb.ReadData(spark, query_1).dropDuplicates("consumer_uid")
//
//    ds_0.createOrReplaceTempView("consumer")
//
//    val query_5 = """SELECT
//                    |consumer_uid,
//                    |app_id,
//                    |type,
//                    |type_value
//                    |FROM consumer_bind""".stripMargin
//
//    val ds_1: Dataset[Row] = ReadFromDb.ReadData(spark, query_5)
//
//    ds_1.createOrReplaceTempView("consumer_bind")
//
//
//    spark.sql("""SELECT
//                |consumer_uid,
//                |type_value as wx_open_id
//                |FROM
//                |consumer_bind WHERE type = 'WECHAT'""".stripMargin).dropDuplicates("consumer_uid")
//      .createOrReplaceTempView("wx_open_id")
//
//    spark.sql("""SELECT
//                |consumer_uid,
//                |type_value as wx_qr_open_id
//                |FROM
//                |consumer_bind WHERE type = 'WECHAT_PC'""".stripMargin).dropDuplicates("consumer_uid")
//      .createOrReplaceTempView("wx_qr_open_id")
//
//
//    spark.sql("""SELECT
//                |consumer_uid,
//                |type_value as wx_union_id
//                |FROM
//                |consumer_bind WHERE type = 'WECHAT_UNION'""".stripMargin).dropDuplicates("consumer_uid")
//      .createOrReplaceTempView("wx_union_id")
//
//
//    val query =
//      """SELECT
//        |C.consumer_uid,
//        |C.account_id,
//        |emojiConverter(C.nickname) as nickname,
//        |C.username,
//        |C.avatar,
//        |C.phone,
//        |C.email,
//        |C.industry,
//        |C.position,
//        |C.source,
//        |C.channel,
//        |C.sex,
//        |C.real_name,
//        |C.education_level,
//        |C.country,
//        |C.province,
//        |C.city,
//        |C.area,
//        |C.birthday,
//        |C.last_visited_ip,
//        |C.is_activated,
//        |wx_union_id.wx_union_id,
//        |wx_open_id.wx_open_id,
//        |wx_qr_open_id.wx_qr_open_id,
//        |C.id_deleted
//        |FROM
//        |consumer AS C
//        |LEFT JOIN wx_open_id ON C.consumer_uid = wx_open_id.consumer_uid
//        |LEFT JOIN wx_qr_open_id ON C.consumer_uid = wx_qr_open_id.consumer_uid
//        |LEFT JOIN wx_union_id ON C.consumer_uid = wx_union_id.consumer_uid
//      """.stripMargin
//
//    val ret_DS: Dataset[Row] = spark.sql(query).dropDuplicates("consumer_uid")
//
//    Write2Db.Save2Rds(ret_DS, "consumer")


//    val query1 =
//      """SELECT
//        |visitor_id,
//        |consumer_uid
//        |FROM
//        |consumer_visitor
//      """.stripMargin
//
//    val ds_0: Dataset[Row] = ReadFromDb.ReadData(spark, query1)
//
//    Write2Db.Save2Rds(ds_0, "consumer_visitor_bind")

//        val query_1 = """SELECT
//                        |visitor_id,
//                        |nickname,
//                        |avatar,
//                        |deleted
//                        |FROM visitor""".stripMargin
//
//      ReadFromDb.ReadData(spark, query_1).createOrReplaceTempView("visitor")
//
//        val query_5 = """SELECT
//                        |visitor_id,
//                        |wx_union_id,
//                        |wx_open_id,
//                        |wx_qr_open_id
//                        |FROM visitor_bind""".stripMargin
//
//        ReadFromDb.ReadData(spark, query_5).createOrReplaceTempView("visitor_bind")
//
//        val query =
//          """SELECT
//            |C.visitor_id,
//            |emojiConverter(C.nickname) AS nickname,
//            |C.avatar,
//            |C.deleted AS id_deleted,
//            |B.wx_union_id,
//            |B.wx_open_id,
//            |B.wx_qr_open_id
//            |FROM
//            |visitor AS C
//            |LEFT JOIN visitor_bind AS B ON C.visitor_id = B.visitor_id
//          """.stripMargin
//
//        val ret_DS: Dataset[Row] = spark.sql(query)
//
//        Write2Db.Save2Rds(ret_DS, "visitor")

//        val query1 =
//          """SELECT
//            |industry_id,
//            |parent_id,
//            |name,
//            |level,
//            |deleted as is_deleted
//            |FROM
//            |industry
//          """.stripMargin
//
//        val ds_0: Dataset[Row] = ReadFromDb.ReadData(spark, query1)
//
//        Write2Db.Save2Rds(ds_0, "industry")

//    val query1 =
//      """SELECT
//        |business_uid,
//        |avatar,
//        |nickname,
//        |apply_name,
//        |position,
//        |company,
//        |website,
//        |industry_id,
//        |license_code,
//        |license_pic,
//        |admin_verify,
//        |verify,
//        |bu,
//        |deleted as is_deleted
//        |FROM
//        |business_user
//      """.stripMargin
//
//    ReadFromDb.ReadData(spark, query1).createOrReplaceTempView("business_user")
//
//    val query2 = """select * from business_reg_form"""
//    ReadFromDb.ReadData(spark, query2).createOrReplaceTempView("business_reg_form")
//
//    val query3 = """SELECT * FROM business_user_mapping_sso"""
//    ReadFromDb.ReadData(spark, query3).createOrReplaceTempView("business_user_mapping_sso")
//
//    val query4 =
//      """select
//        |BU.business_uid,
//        |BUMS.account_id,
//        |BU.avatar,
//        |BU.nickname,
//        |BU.apply_name,
//        |BRF.phone,
//        |BU.position,
//        |BU.company,
//        |BU.website,
//        |BU.industry_id,
//        |BU.license_code,
//        |BU.license_pic,
//        |BU.admin_verify,
//        |BU.verify,
//        |BU.bu,
//        |BU.is_deleted
//        |from
//        |business_user as BU left join
//        |business_reg_form as BRF ON BU.business_uid = BRF.business_uid left join
//        |business_user_mapping_sso as BUMS ON BU.business_uid = BUMS.business_uid
//      """.stripMargin
//
//    val ret_DS: Dataset[Row] = spark.sql(query4)
//
//    Write2Db.Save2Rds(ret_DS, "business_user")

//      val query1 =
//        """SELECT
//          |group_id,
//          |business_uid,
//          |bu,
//          |title,
//          |describe,
//          |rules,
//          |type,
//          |system_type,
//          |user_count,
//          |deleted as is_deleted
//          |FROM
//          |group
//        """.stripMargin
//
//    ReadFromDb.ReadData(spark, """select * from test_vhall_business.group""").createOrReplaceTempView("group")
//
//    val ret_DS: Dataset[Row] = spark.sql(query1)
//
//    Write2Db.Save2Rds(ret_DS, "user_support_service.group")


    ReadFromDb.ReadData(spark, """select * from test_vhall_business.consumer_tag""").createOrReplaceTempView("consumer_tag")

    ReadFromDb.ReadData(spark, """select * from test_vhall_business.consumer_tag_category_mapping""").createOrReplaceTempView("consumer_tag_category_mapping")

    val query1 =
      """SELECT
        |ct.tag_id,
        |nvl(ctcm.category_id, 0) as category_id,
        |ct.business_uid,
        |ct.bu,
        |ct.tag_name,
        |ct.describe,
        |ct.tag_type,
        |ct.type,
        |ct.sort,
        |ct.deleted as is_deleted
        |FROM
        |consumer_tag as ct left join
        |consumer_tag_category_mapping as ctcm on ct.tag_id = ctcm.tag_id
      """.stripMargin

    val ret_DS: Dataset[Row] = spark.sql(query1)

    Write2Db.Save2Rds(ret_DS, "user_support_service.tag")



//    ReadFromDb.ReadData(spark, """select * from test_vhall_business.consumer_tag_category""").createOrReplaceTempView("consumer_tag_category")
//
//    val query1 =
//      """SELECT
//        |id as category_id,
//        |business_uid,
//        |bu,
//        |category_name,
//        |type,
//        |deleted as is_deleted
//        |FROM
//        |consumer_tag_category
//      """.stripMargin
//
//    val ret_DS: Dataset[Row] = spark.sql(query1)
//
//    Write2Db.Save2Rds(ret_DS, "user_support_service.tag_category")


//    ReadFromDb.ReadData(spark, """select * from activity_info""").createOrReplaceTempView("activity_info")
//
//    ReadFromDb.ReadData(spark, """select * from activity_starttoend""").createOrReplaceTempView("activity_starttoend")
//
//    ReadFromDb.ReadData(spark, """select * from activity_status_explain_t""").createOrReplaceTempView("activity_status_explain_t")
//
//
//    val query1 =
//          """SELECT
//            |T.business_uid,
//            |T.bu,
//            |T.activity_id,
//            |ai.live_room as live_room_id,
//            |ai.hd_room as hd_room_id,
//            |ai.channel_room as channel_room_id,
//            |T.start_time,
//            |T.end_time,
//            |T.aid_tt as live_tt,
//            |yuyue.status as is_yuyue,
//            |baoming.status as is_baoming
//            |FROM
//            |activity_starttoend as T inner join
//            |activity_info as ai on T.business_uid = ai.business_uid and T.activity_id = ai.activity_id inner join
//            |(
//            |select
//            |business_uid,
//            |activity_id,
//            |bu,
//            |status
//            |from
//            |activity_status_explain_t
//            |where data_type = 1
//            |) as yuyue on T.business_uid = yuyue.business_uid and T.activity_id = yuyue.activity_id inner join
//            |(
//            |select
//            |business_uid,
//            |activity_id,
//            |bu,
//            |status
//            |from
//            |activity_status_explain_t
//            |where data_type = 2
//            |) as baoming on T.business_uid = baoming.business_uid and T.activity_id = baoming.activity_id
//          """.stripMargin
//
//        val ret_DS: Dataset[Row] = spark.sql(query1)
//
//        Write2Db.Save2Rds(ret_DS, "activity_support_service.activity_live")

//    ReadFromDb.ReadData(spark, """select * from activity_record_info""").createOrReplaceTempView("activity_record_info")
//    ReadFromDb.ReadData(spark, """select * from activity_starttoend""").createOrReplaceTempView("activity_starttoend")
//
//    val ret_DS = spark.sql(
//      """
//        |select
//        |A.business_uid,
//        |A.activity_id,
//        |T.bu,
//        |A.record_id as vod_id,
//        |A.duration as vod_tt,
//        |A.type as vod_type,
//        |A.status
//        |from
//        |activity_record_info AS A inner join
//        |(
//        |select
//        |distinct
//        |business_uid,
//        |activity_id,
//        |bu
//        |from
//        |activity_starttoend
//        |) as T ON A.business_uid = T.business_uid and A.activity_id = T.activity_id
//      """.stripMargin)
//
//    Write2Db.Save2Rds(ret_DS, "activity_support_service.activity_vod")


//    val ret_DS =  ReadFromDb.ReadData(spark,
//      """select
//        |business_uid,
//        |activity_id,
//        |inviter_type,
//        |invitation_card_id,
//        |sweep_time,
//        |inviter_visitor_id,
//        |inviter_consumer_id,
//        |invited_person_visitor_id,
//        |invited_person_consumer_id
//        |from invitation_card""".stripMargin)
//
//    Write2Db.Save2Rds(ret_DS, "activity_support_service.activity_invi_card")


//    val ret_DS =  ReadFromDb.ReadData(spark,
//      """select
//        |business_uid,
//        |consumer_uid,
//        |activity_id,
//        |visitor_id,
//        |behavior,
//        |event,
//        |occur_time,
//        |pf,
//        |service_names,
//        |bu,
//        |ua,
//        |refer,
//        |question_id,
//        |answer_id,
//        |market_tools_id,
//        |market_tools_status,
//        |standby_1,
//        |standby_2,
//        |standby_3
//        |from user_behavior_log""".stripMargin)
//
//
//    Write2Db.Save2Rds(ret_DS, "activity_support_service.activity_user_behavior_log")

//    val ret_DS =  ReadFromDb.ReadData(spark,
//      """select
//        |business_uid,
//        |bu,
//        |is_compute
//        |from business_uid_is_compute""".stripMargin)
//
//
//    Write2Db.Save2Rds(ret_DS, "activity_support_service.b_is_compute")


//    ReadFromDb.ReadData(spark,
//      """select
//        |business_uid,
//        |business_consumer_uid,
//        |group_id,
//        |bu
//        |from group_consumer_mapping""".stripMargin).createOrReplaceTempView("group_consumer_mapping")
//
//
//    ReadFromDb.ReadData(spark,
//      """select
//        |business_consumer_uid,
//        |consumer_uid,
//        |bu
//        |from consumer""".stripMargin).createOrReplaceTempView("consumer")
//
//    val ret_DS = spark.sql(
//      """
//        |select
//        |gcm.business_uid,
//        |gcm.group_id,
//        |gcm.bu,
//        |c.consumer_uid
//        |from
//        |group_consumer_mapping as gcm inner join
//        |consumer as c on gcm.business_consumer_uid = c.business_consumer_uid
//      """.stripMargin)
//
//    Write2Db.Save2Rds(ret_DS, "activity_support_service.consumer_group_rel")



//    ReadFromDb.ReadData(spark,
//      """select
//        |consumer_uid,
//        |business_uid,
//        |visitor_id,
//        |phone,
//        |real_name,
//        |nickname,
//        |email,
//        |industry,
//        |position,
//        |sex,
//        |education_level,
//        |if(birthday = '0000-00-00 00:00:00', null, birthday) as birthday,
//        |remark,
//        |is_activated,
//        |source,
//        |import_date,
//        |country,
//        |province,
//        |city,
//        |first_visited_at as first_join,
//        |last_visited_at as laster_join,
//        |join_count as join_nums,
//        |invite_friends_count as invite_nums,
//        |watch_live_time,
//        |watch_replay_time,
//        |user_level,
//        |wx_union_id,
//        |wx_qr_open_id,
//        |wx_open_id,
//        |bu,
//        |channel,
//        |channel_activity_id,
//        |score,
//        |deleted
//        |from consumer""".stripMargin).createOrReplaceTempView("consumer")
//
//
//
//    val ret_DS = spark.sql(
//      """select
//        |getIndexId(business_uid, case when consumer_uid = 0 then visitor_id else consumer_uid end) as _id,
//        |case when consumer_uid = 0 then visitor_id else consumer_uid end as consumer_uid,
//        |cast(business_uid as bigint) as business_uid,
//        |phone,
//        |real_name,
//        |nickname,
//        |email,
//        |industry,
//        |position,
//        |sex,
//        |education_level,
//        |birthday,
//        |remark,
//        |is_activated,
//        |source,
//        |import_date,
//        |country,
//        |province,
//        |city,
//        |first_join,
//        |laster_join,
//        |cast(join_nums as bigint) as join_nums,
//        |cast(invite_nums as bigint) as invite_nums,
//        |cast(watch_live_time + watch_replay_time as bigint) as total_tt,
//        |cast(user_level as bigint) as user_level,
//        |wx_union_id,
//        |wx_qr_open_id,
//        |wx_open_id,
//        |bu,
//        |channel,
//        |channel_activity_id,
//        |deleted
//        |from
//        |consumer
//      """.stripMargin)
//
//    val script =
//      """ctx._source.business_uid=params.business_uid;
//        |ctx._source.consumer_uid=params.consumer_uid;
//        |ctx._source.phone=params.phone;
//        |ctx._source.real_name=params.real_name;
//        |ctx._source.nickname=params.nickname;
//        |ctx._source.email=params.email;
//        |ctx._source.industry=params.industry;
//        |ctx._source.position=params.position;
//        |ctx._source.sex=params.sex;
//        |ctx._source.education_level=params.education_level;
//        |ctx._source.birthday=params.birthday;
//        |ctx._source.remark=params.remark;
//        |ctx._source.is_activated=params.is_activated;
//        |ctx._source.source=params.source;
//        |ctx._source.import_date=params.import_date;
//        |ctx._source.country=params.country;
//        |ctx._source.province=params.province;
//        |ctx._source.city=params.city;
//        |ctx._source.first_join=params.first_join;
//        |ctx._source.laster_join=params.laster_join;
//        |ctx._source.join_nums=params.join_nums;
//        |ctx._source.invite_nums=params.invite_nums;
//        |ctx._source.total_tt=params.total_tt;
//        |ctx._source.user_level=params.user_level;
//        |ctx._source.wx_union_id=params.wx_union_id;
//        |ctx._source.wx_qr_open_id=params.wx_qr_open_id;
//        |ctx._source.wx_open_id=params.wx_open_id;
//        |ctx._source.bu=params.bu;
//        |ctx._source.channel=params.channel;
//        |ctx._source.channel_activity_id=params.channel_activity_id;
//        |ctx._source.deleted=params.deleted;
//      """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")
//
//    val params =
//      """
//        |business_uid:business_uid,
//        |consumer_uid:consumer_uid,
//        |phone:phone,
//        |real_name:real_name,
//        |nickname:nickname,
//        |email:email,
//        |industry:industry,
//        |position:position,
//        |sex:sex,
//        |education_level:education_level,
//        |birthday:birthday,
//        |remark:remark,
//        |is_activated:is_activated,
//        |source:source,
//        |import_date:import_date,
//        |country:country,
//        |province:province,
//        |city:city,
//        |first_join:first_join,
//        |laster_join:laster_join,
//        |join_nums:join_nums,
//        |invite_nums:invite_nums,
//        |total_tt:total_tt,
//        |user_level:user_level,
//        |wx_union_id:wx_union_id,
//        |wx_qr_open_id:wx_qr_open_id,
//        |wx_open_id:wx_open_id,
//        |bu:bu,
//        |channel:channel,
//        |channel_activity_id:channel_activity_id,
//        |deleted:deleted
//      """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")
//
//    Write2Es.Upsert2Es(ret_DS, script, params)



//    ReadFromDb.ReadData(spark,
//      """select
//        |business_uid,
//        |activity_id,
//        |bu,
//        |consumer_uid,
//        |visitor_id,
//        |score,
//        |os,
//        |device,
//        |browser,
//        |sex,
//        |country,
//        |province,
//        |city,
//        |watch_live_time as live_tt,
//        |watch_replay_time as vod_tt,
//        |last_leave_at as watch_date
//        |from activity_consumer""".stripMargin).createOrReplaceTempView("activity_consumer")
//
//
//    val ret_DS = spark.sql(
//      """
//        |select
//        |getIndexId(activity_id, case when consumer_uid = 0 then visitor_id else consumer_uid end) as _id,
//        |cast(business_uid as bigint) as business_uid,
//        |cast(activity_id as bigint) as activity_id,
//        |bu,
//        |cast(consumer_uid as bigint) as consumer_uid,
//        |cast(score as bigint) as score,
//        |os,
//        |device,
//        |browser,
//        |sex,
//        |country,
//        |province,
//        |city,
//        |case when live_tt > 0 OR vod_tt > 0 then 1 else 0 end as is_watch,
//        |cast(live_tt as bigint) as live_tt,
//        |cast(vod_tt as bigint) as vod_tt,
//        |TimeStamp2Date(watch_date) as watch_date
//        |from
//        |activity_consumer
//      """.stripMargin)
//
//    ret_DS.printSchema()
//
//    val script =
//      """ctx._source.business_uid=params.business_uid;
//        |ctx._source.activity_id=params.activity_id;
//        |ctx._source.bu=params.bu;
//        |ctx._source.consumer_uid=params.consumer_uid;
//        |ctx._source.score=params.score;
//        |ctx._source.os=params.os;
//        |ctx._source.device=params.device;
//        |ctx._source.browser=params.browser;
//        |ctx._source.sex=params.sex;
//        |ctx._source.country=params.country;
//        |ctx._source.province=params.province;
//        |ctx._source.city=params.city;
//        |ctx._source.is_watch=params.is_watch;
//        |ctx._source.live_tt=params.live_tt;
//        |ctx._source.vod_tt=params.vod_tt;
//        |ctx._source.watch_date=params.watch_date;
//      """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")
//
//    val params =
//      """
//        |business_uid:business_uid,
//        |activity_id:activity_id,
//        |bu:bu,
//        |consumer_uid:consumer_uid,
//        |score:score,
//        |os:os,
//        |device:device,
//        |browser:browser,
//        |sex:sex,
//        |country:country,
//        |province:province,
//        |city:city,
//        |is_watch:is_watch,
//        |live_tt:live_tt,
//        |vod_tt:live_tt,
//        |watch_date:watch_date
//      """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")
//
//    Write2Es.Upsert2Es(ret_DS, script, params)

    spark.stop()

  }

}
