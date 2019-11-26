package SparkCore

import org.apache.spark.{SparkConf, SparkContext}

object wordcount {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
    val sc = SparkContext.getOrCreate(conf)

    val rdd1 = sc.textFile("").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    //进行持久化到内存
    rdd1.cache()
    //rdd1.persist()

    //将缓存删除
    rdd1.unpersist()
    sc.stop()

  }

}
