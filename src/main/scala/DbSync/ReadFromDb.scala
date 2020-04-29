package DbSync

import java.util.Properties

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
  def ReadData(spark: SparkSession, query: String, properties: Properties): Dataset[Row] = {

    val host = properties.getProperty("host")
    val port = properties.getProperty("port")
    val db_name = properties.getProperty("db_name")
    val user = properties.getProperty("user")
    val pwd = properties.getProperty("pwd")

    val url = s"jdbc:mysql://${host}:${port}/${db_name}?useUnicode=true&characterEncoding=UTF-8&useSSL=false"

    println("url======>"+url)

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

        e.printStackTrace()

        val msg = s"Ocuur error when read the data from the table [ ${query} ], the error is: ${e.getMessage}"
        logger.error(msg)
        // 把异常抛到最外面处理
        throw new RuntimeException(msg)

      }

    }
  }
}
