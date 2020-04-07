package DbSync

import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.slf4j.LoggerFactory

/**
  * @Title: ReadFromDb
  * @ProjectName batch_data_zhike_bc
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/10/1216:05
  */
object ReadFromDb {

  val logger = LoggerFactory.getLogger(ReadFromDb.getClass)

  /** 从mysql读数据
    * @param spark
    * @param query
    * @param soureProps
    * @return
    */
  def ReadData(spark: SparkSession, query: String): Dataset[Row] = {

    val host = "jdbc:mysql://drdsbggaais5237y.drds.aliyuncs.com"
    val port = 3306
    val db_name = "test_vhall_business"
    val user = "test_vhall_business"
    val pwd = "Testvhallbusiness1"

    val url = s"${host}:${port}/${db_name}?useUnicode=true&characterEncoding=UTF-8&useSSL=false"

    try {

      val ds = spark.read.format("jdbc")
        .option("driver", "com.mysql.jdbc.Driver")
        .option("url", url)
        .option("dbtable", s"(${query}) as newtable")
        .option("user", user)
        .option("password", pwd)
        .load()

      return ds
    }catch {

      case e: Exception => {

        val msg = s"Ocuur error when read the data from the table [ ${query} ], the error is: ${e.getMessage}"
        logger.error(msg)
        // 把异常抛到最外面处理
        throw new RuntimeException(msg)

      }

    }
  }
}
