package SparkSql

import org.apache.spark.sql.{SparkSession}

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

//    val orders = spark.read
//      .format("org.elasticsearch.spark.sql")
//      .option("es.read.metadata", "true")
//      .option("es.nodes.wan.only","true")
//      .option("pushdown", "true")
//      .option("es.port","9200")
//      .option("es.net.ssl","false")
//      .option("es.nodes", "192.168.1.171")
//      .option("query", query)
////      .option("es.mapping.include", "age,name")
////      .option("es.mapping.exclude", "surname")
////      .option("es.read.source.filter", "age,name")w
//      .option("es.read.field.include", "age,name")
//      .option("es.read.field.exclude", "surname")
//      .option("es.read.metadata", false)  // 是否读取元数据字段
////      .option("es.read.metadata.field", "_id")  // 元数据信息所在的字段
//      .load("test_new_client")

//    orders.printSchema()
//    orders.show(100)


    spark.udf.register("tag2arr", (id:String)=>tag2Arr(id))

    val orders = Seq(
      Person("1024", 21321321, "迪丽热巴", "迪丽热巴小姐姐"),
      Person("1025", 6785656, "迪丽热巴02", "迪丽热巴XXX")
    )

    import spark.implicits._

    val ds = orders.toDF()

    ds.createOrReplaceTempView("orders")

    val retds = spark.sql(
      """select
        | tag2arr(_id) as arr,
        | _id,
        | name,
        | age,
        | surname
        |from
        |orders
      """.stripMargin)

    retds.printSchema()

    retds.show()

    val script1 =
      """ctx._source.age.clear();
        |for(int i=0; i<params.arr.size();i++)
        |{
        | ctx._source.age.add(params.arr[i]);
        |}
        """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")

    val script2 =
      """ctx._source.age=params.age;
      """.stripMargin.replaceAll("""[\\|\s*|\t|\r|\n]""", "")

    retds.write
      .format("org.elasticsearch.spark.sql")
      .option("es.mapping.id", "_id")
      .option("es.nodes", "192.168.1.171")
      .option("es.port", "9200")
      .option("es.update.script.inline",script2)
      .option("es.update.script.lang","painless")
      .option("es.update.script.params", "age:arr")
      .option("es.write.operation", "upsert")
      .option("es.batch.write.retry.count", 2)
//      .option("es.mapping.include", "age,name,surname")
      .mode("append")
      .save("test_new_client")

    spark.stop()

  }

  private def tag2Arr(id: String): Seq[Long] ={
    Seq(10004,10005, 10006)
  }

}
