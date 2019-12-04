package DbSync

import org.apache.spark.sql.{Dataset, Row, SaveMode, functions}
import org.slf4j.LoggerFactory

/**
  * @Title: Write2Db
  * @ProjectName batch_data_zhike_bc
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/10/1215:59
  */
object Write2Db {

  // 获取logger
  val logger = LoggerFactory.getLogger(Write2Db.getClass)

  /**
    *  写入数据库的代码
    * @param ds
    * @param tabname
    * @param dc_updated_time
    */
  def Save2Rds(ds: Dataset[Row], tabname: String): Unit = {

    try {

      val host = "jdbc:mysql://rm-uf62sa98d0fip1xvfo.mysql.rds.aliyuncs.com"
      val port = 3306
      val db_name = "market_saas_bigdata_result"

      val url: String = s"""${host}:${port}/${db_name}?useUnicode=true&characterEncoding=UTF-8&useSSL=false"""

      ds.write.format("jdbc")
        .mode(SaveMode.Append)
        .option("driver", "com.mysql.jdbc.Driver")
        .option("url", url)
        .option("dbtable", tabname)
        .option("user", "msbr_write")
        .option("password", "Msbr_write")
        .save()

    } catch {

      case e: Exception => {

        val msg = s"Ocuur Error When Write The Data To The Table [ ${tabname} ], The Error is: ${e.getMessage}"
        logger.error(msg)
        // 把异常抛到最外面处理
        throw new RuntimeException(msg)

      }
    }
  }
}
