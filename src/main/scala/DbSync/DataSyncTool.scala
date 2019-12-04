package DbSync

import org.apache.spark.sql.{Dataset, Row, SparkSession, functions}

/**
  * @Title: consumer_business_info_sync
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2710:36
  */
object DataSyncTool {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .config("spark.sql.shuffle.partitions", 12)
      .getOrCreate()

//    val query = """SELECT
//                  |*
//                  |FROM consumer_business_info
//                  |WHERE business_uid = 3700002 AND dc_updated_time = '2019-11-27 06:00:19'""".stripMargin
//
//    val ds:Dataset[Row] = ReadFromDb.ReadData(spark, query)
//
//    Write2Db.Save2Rds(ds, "consumer_business_info")

//    val query_2 = """SELECT
//                    |*
//                    |FROM consumer_tags_of_business
//                    |WHERE business_uid = 3700002 AND dc_updated_time = '2019-11-27 06:00:19'""".stripMargin


    import spark.implicits._

//    val query_3 = """SELECT
//                    |business_uid,
//                    |consumer_uid,
//                    |visitor_id,
//                    |event,
//                    |data,
//                    |activity_id,
//                    |type,
//                    |bu,
//                    |generated_at
//                    |FROM user_log_statistics
//                    |WHERE activity_id = 56538301""".stripMargin
//
//    val ds:Dataset[Row] = ReadFromDb.ReadData(spark, query_3)
//
//    val ret_DS:Dataset[Row] = ds.withColumn("t_version", functions.lit("v_0"))
//
//    Write2Db.Save2Rds(ret_DS, "user_log_statistics")

    val query_4 = """SELECT
                    |*
                    |FROM consumer_activity_info
                    |WHERE activity_id = 56538301 AND dc_updated_time = '2019-11-27 03:29:28'""".stripMargin

    val ds_0: Dataset[Row] = ReadFromDb.ReadData(spark, query_4)withColumn("t_version", functions.lit("v_0"))

    Write2Db.Save2Rds(ds_0, "consumer_activity_info")

    val query_5 = """SELECT
                    |*
                    |FROM consumer_activity_behavior_scores
                    |WHERE activity_id = 56538301 AND dc_updated_time = '2019-11-27 03:29:28'""".stripMargin

    val ds_1: Dataset[Row] = ReadFromDb.ReadData(spark, query_5)withColumn("t_version", functions.lit("v_0"))

    Write2Db.Save2Rds(ds_1, "consumer_activity_behavior_scores")

    spark.stop()

  }

}
