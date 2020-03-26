package SparkSql

import org.apache.spark.sql.SparkSession

/**
  * @Title: SparkSQL2Es
  * @ProjectName SparkTrain
  * @Description: TODO spark-sql upsert方式写入es中
  * @Author yisheng.wu
  * @Date 2020/3/2617:12
  */
case class Person(id: String, age: Integer, name: String, surname: String)

object SparkSQL2Es {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    val orders = Seq(
      Person("1025", 1025, "迪丽热巴", "迪丽热巴小姐姐"),
      Person("1024", 1024, "迪丽热巴02", "迪丽热巴XXX"),
      Person("1007", 1024, "热依扎", "热依扎")
    )

    import spark.implicits._

    val ds = orders.toDF()

    ds.write
      .format("org.elasticsearch.spark.sql")
      .option("es.mapping.id", "id")
      .option("es.nodes", "192.168.1.171")
      .option("es.port", "9200")
      .option("es.update.script.inline",
        "ctx._source.age = params.age;ctx._source.name = params.name;ctx._source.surname = params.surname")
      .option("es.update.script.params", "age:age, name:name, surname:surname")
      .option("es.write.operation", "upsert")
      .option("es.batch.write.retry.count", 2)
      .mode("append")
      .save("test_new_client")

    spark.stop()

  }

}
