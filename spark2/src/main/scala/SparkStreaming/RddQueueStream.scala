package SparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * rdd队列流
  */

object RddQueueStream {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("RddQueueStream").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    val rddQueue =new scala.collection.mutable.SynchronizedQueue[RDD[Int]]()
    val queueStream = ssc.queueStream(rddQueue)
    val mappedStream = queueStream.map(r => (r % 10, 1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    reducedStream.print()
    ssc.start()
    for (i <- 1 to 10){
      rddQueue += ssc.sparkContext.makeRDD(1 to 100,2)
      Thread.sleep(1000)
    }
    ssc.stop()
  }
}
