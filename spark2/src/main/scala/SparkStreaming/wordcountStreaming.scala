package SparkStreaming

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}

object wordcountStreaming {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("wordcountStreaming")
    val ssc = new StreamingContext(conf, Seconds(5))

    ssc.sparkContext.setCheckpointDir("E:/ck")

    //从socket端口消费数据
    val lines = ssc.socketTextStream("192.168.52.128", 9999)
    val wordcounts = lines.flatMap(_.split(" "))

    //还可以从文件系统消费数据
//    val wordcounts = ssc.textFileStream("resources/kv1.txt").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    val updateFunc = (iter:Iterator[(String, Seq[Int], Option[Int])]) => {
      iter.flatMap { case (x, y, z) => Some(y.sum + z.getOrElse(0)).map(m => (x, m)) }
    }

    //使用updateStateByKey必须checkpoint操作。
    //使用到updateStateByKey要开启checkpoint机制和功能。
    //如果batchInterval设置的时间小于10秒，那么10秒写入磁盘一份。如果batchInterval设置的时间大于10秒，那么就会batchInterval时间间隔写入磁盘一份。
    val results = wordcounts.map(word => (word, 1)).updateStateByKey(updateFunc,
      new HashPartitioner(ssc.sparkContext.defaultParallelism), true)

    //可以使用fileStream进行文件过滤操作
//    ssc.fileStream()

    results.print()
    //启动程序
    ssc.start()
    //等待程序运行完成
    ssc.awaitTermination()
  }
}
