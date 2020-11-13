package SparkSql

import org.apache.spark.sql.SparkSession

/**
  * @Title: WirteSeqToESArr
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2020/4/2317:58
  */
case class My_test(_id: String, test_integer: Long)

object WirteSeqToESArr {

  def udfMapping(id: Long): Seq[Long] ={

    var tag_seq = Seq[Long]()

    tag_seq = tag_seq :+ 1L
    tag_seq = tag_seq :+ 2L
    tag_seq = tag_seq :+ 3L
    tag_seq = tag_seq :+ 4L

    tag_seq
  }

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession
      .builder()
      .master("local[4]")
      .appName("test")
      .getOrCreate()

    val arr = Seq(
      My_test("46", 20),
      My_test("47", 30)
    )

    import sparkSession.implicits._

    sparkSession.udf.register("Tags2TagIds",(id: Long)=>udfMapping(id))

    arr.toDF().createOrReplaceTempView("my_test")

    val ret_DS = sparkSession.sql("select _id, test_integer, Tags2TagIds(test_integer) as system_tags_array from my_test")

    ret_DS.show()

    //    val df = sparkSession
    //      .createDataFrame(arr)
    //      .toDF("_id", "test_integer", "test_keyword", "test_keyword_array")

    val script =
      """
        |ctx._source.system_tags_array = params.system_tags_array;
        |ctx._source.test_integer = params.test_integer;
      """.stripMargin.replaceAll("""\r|\n""", "")

    val params =
      """
        |test_keyword:test_keyword_array,
        |test_integer:test_integer
      """.stripMargin.replaceAll("""\r|\n""", "")


    ret_DS.write
      .format("org.elasticsearch.spark.sql")
      .option("es.mapping.id", "_id")
      .option("es.mapping.exclude", "_id")
      .option("es.nodes.wan.only", "true")
      .option("es.nodes", "10.10.200.247")
      .option("es.port", "9200")
      .option("es.update.script.inline", script)
      .option("es.update.script.params", params)
      .option("es.write.operation", "upsert")
      //      .option("es.read.field.as.array.include", "test_keyword_array") // 天坑：需要指定数组字段
      .option("es.batch.write.retry.count", 2)
      .mode("append")
      .save("my_test/my_test")

    //    df.show()

    sparkSession.stop()


  }

}
