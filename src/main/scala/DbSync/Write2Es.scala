package DbSync

import org.apache.spark.sql.{Dataset, Row}

/**
  * @Title: Write2Es
  * @ProjectName batch_data_zhike_bc
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2020/3/2617:45
  */
object Write2Es {

  /**
    * 写入ES的代码
    * @param ds
    * @param tabname
    * @param dc_updated_time
    */
  def Upsert2Es(ds: Dataset[Row], script: String, params: String): Unit = {

    val host = "10.10.200.247"
    val port = "9200"
    val index_name = "consumer_activity_rel/consumer"

    ds.write
      .format("org.elasticsearch.spark.sql")
      .option("es.mapping.id", "_id")
      .option("es.nodes", host)
      .option("es.port", port)
      .option("es.update.script.lang","painless")
      .option("es.update.script",script)  // es.update.script.inline 6.0以及之后的版本
      .option("es.update.script.params", params)
      .option("es.write.operation", "upsert")
      .option("es.batch.write.retry.count", 3)
      .option("es.mapping.exclude", "_id")
      .option("es.http.timeout","60000ms")
      .option("es.nodes.wan.only", "true")
      .mode("append")
      .save(index_name)

  }

}
