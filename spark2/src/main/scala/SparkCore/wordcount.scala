package SparkCore

import org.apache.spark.{SparkConf, SparkContext}

object wordcount {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("wordcount").setMaster("local[1]")
    val sc = SparkContext.getOrCreate(conf)

    sc.textFile("D:\\workspace\\git\\SparkTrain\\spark2\\src\\main\\resources\\people.csv")
      .flatMap(_.split(";"))
      .map((_, 1))
      .reduceByKey(_ + _)
      .foreach(println(_))

    sc.stop()
  }

}
