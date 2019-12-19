package SparkStreaming

import java.util.Date

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.util.LongAccumulator

/**
  * @Title: TimingUpdateDim
  * @ProjectName SparkTrain
  * @Description: TODO 定时更新维表join
  * @Author yisheng.wu
  * @Date 2019/12/1916:23
  */
object TimingUpdateDim {

  def main(args: Array[String])={
    val conf = new SparkConf().setAppName("TimingUpdateDim").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.sparkContext.setLogLevel("error")
    val lines = ssc.socketTextStream("192.168.1.171", 9999)
    // Split each line into words
    lines.foreachRDD(rdd=>{
      val dimTable = DimTable.getInstance(rdd.sparkContext)
      val currentAccumulatorInterval = new Date().getTime - UpdateTimeCount.getInstance(rdd.sparkContext).value
      if(currentAccumulatorInterval>20000){
        DimTable.update(rdd.sparkContext)
        UpdateTimeCount.getInstance(rdd.sparkContext).add(currentAccumulatorInterval)
      }
      dimTable.value.map(print)
      println()
    })
    ssc.start()     // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate
  }
}

/**
 * Use this singleton to get or register a Broadcast variable.
  */
object DimTable {

  @volatile
  private var instance: Broadcast[Array[String]] = null

  def getInstance(sc: SparkContext): Broadcast[Array[String]] = {
    if (instance == null) {
      synchronized {
        if (instance == null) {
          val dimMapTmp = sc.parallelize(Array("1", "2", "3")).cache().collect()
          instance = sc.broadcast(dimMapTmp)
        }
      }
    }
    instance
  }

  def update(sc: SparkContext, blocking: Boolean = false): Unit = {
    if (instance != null)
      instance.unpersist(blocking)
    println("=====begin update=========")
    val dimMapTmp = sc.parallelize(Array("3", "4", "5")).cache().collect()
    instance = sc.broadcast(dimMapTmp)
  }
}

/**
 * Use this singleton to get or register an Accumulator.
  */
object UpdateTimeCount {
  @volatile
  private var instance: LongAccumulator = null
  def getInstance(sc: SparkContext): LongAccumulator= {
    if (instance == null){
      synchronized {
        if (instance == null) {
          instance = sc.longAccumulator("UpdateTimeCount")
          //初始化时为当前时间
          instance.add( new Date().getTime)
        }
      }
    }
    instance
  }
}
