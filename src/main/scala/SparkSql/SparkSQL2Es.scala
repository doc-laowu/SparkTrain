package SparkSql

import org.apache.spark.sql.SparkSession

/**
  * @Title: SparkSQL2Es
  * @ProjectName SparkTrain
  * @Description: TODO spark-sql upsert方式写入es中
  * @Author yisheng.wu
  * @Date 2020/3/2617:12
  */
case class Person(_id: String, age: Integer, name: String, surname: String)

object SparkSQL2Es {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    val query= """{
                 |	"_source": ["_id","age", "name"],
                 |	"query": {
                 |		"bool": {
                 |			"filter": {
                 |				"term": {
                 |					"age": 1026
                 |				}
                 |			}
                 |		}
                 |	}
                 |}""".stripMargin

    val orders = spark.read
      .format("org.elasticsearch.spark.sql")
      .option("es.read.metadata", "true")
      .option("es.nodes.wan.only","true")
      .option("pushdown", "true")
      .option("es.port","9200")
      .option("es.net.ssl","false")
      .option("es.nodes", "192.168.1.171")
      .option("query", query)
//      .option("es.mapping.include", "age,name")
//      .option("es.mapping.exclude", "surname")
//      .option("es.read.source.filter", "age,name")w
      .option("es.read.field.include", "age,name")
      .option("es.read.field.exclude", "surname")
      .option("es.read.metadata", false)  // 是否读取元数据字段
//      .option("es.read.metadata.field", "_id")  // 元数据信息所在的字段
      .load("test_new_client")

//    orders.printSchema()
//    orders.show(100)
//
//
//    val orders = Seq(
//      Person("1026", 21321321, "迪丽热巴", "迪丽热巴小姐姐"),
//      Person("1027", 6785656, "迪丽热巴02", "迪丽热巴XXX"),
//      Person("1028", 54634636, "热依扎", "热依扎")
//    )
//
//  import spark.implicits._
//
//    val ds = orders.toDF()
//
//    ds.write
//      .format("org.elasticsearch.spark.sql")
//      .option("es.mapping.id", "_id")
//      .option("es.nodes", "192.168.1.171")
//      .option("es.port", "9200")
//      .option("es.update.script.inline",
//        "ctx._source.age = params.age")
//      .option("es.update.script.params", "age:age")
//      .option("es.write.operation", "upsert")
//      .option("es.batch.write.retry.count", 2)
////      .option("es.mapping.include", "age,name,surname")
//      .mode("append")
//      .save("test_new_client")

    spark.stop()

  }

}
