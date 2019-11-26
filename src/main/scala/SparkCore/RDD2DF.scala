package SparkCore

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}

case class words(name: String, nums: Int)

object RDD2DF {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
    val sc = SparkContext.getOrCreate(conf)
    val spark = SparkSession.builder().config(conf).getOrCreate()

    //转成DF需要导入隐式转换
    import spark.implicits._

    //通过case class 转成DF
//    val rdd1 = sc.textFile("README.md")
//      .flatMap(_.split(" "))
//      .map((_, 1))
//      .reduceByKey(_ + _).map(x => words(x._1, x._2)).toDF().show()

    //通过StructType转成DF

    val schema = StructType(Array(StructField("name", StringType, nullable = true), StructField("nums", IntegerType, nullable = true)))

    val rdd2 = sc.textFile("README.md")
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    spark.udf.register("myAvg", myAvg)

    // Convert records of the RDD (people) to Rows
    val rowRDD = rdd2.map(attributes => Row(attributes._1, attributes._2))

    // Apply the schema to the RDD
    val DF = spark.createDataFrame(rowRDD, schema)

    DF.printSchema()

    DF.createOrReplaceTempView("wordcountTable")

    //注册自定义udf第一种方法   此时注册的方法 只能在sql()中可见，对DataFrame API不可见
    spark.udf.register("valueDouble", (value: Int) => valueDouble(value))
//    spark.sql("select myAvg(nums) from wordcountTable").show()
    spark.sql("select valueDouble(nums) from wordcountTable").show()

    //注册自定义udf第二种方法  此时注册的方法 对DataFrame可见
    import org.apache.spark.sql.functions.udf
    val Value_Double = udf(valueDouble(_: Int))

    DF.select($"name",Value_Double($"nums")).show()

    sc.stop()

  }

  def valueDouble(value: Int) :Int ={
    value * value
  }

}
