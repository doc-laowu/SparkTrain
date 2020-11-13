package SparkStreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{HashPartitioner, SparkConf, TaskContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object StreamingOperation {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("StreamingOperation").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))

    ssc.checkpoint("E:/ck")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.52.129:9092,192.168.52.130:9092,192.168.52.131:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test_group_id",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val topics = Array("test_new_client")

    /**
      * LocationStrategies：新的Kafka消费者API将预取消息到缓冲区。因此，出于性能方面的原因，Spark集成将缓存的使用者保存在执行器上(而不是为每个批重新创建它们)，并且更愿意在拥有适当
      * 使用者的主机位置上调度分区，这一点非常重要。在大多数情况下，您应该使用LocationStrategies。如上所示，首选一致性。这将在可用的执行器之间均匀地分配分区
      *
      * PreferBrokers：如果执行器与Kafka代理位于相同的主机上，那么使用PreferBrokers，它更愿意为该分区在Kafka leader上调度分区
      *
      * PreferFixed：如果分区之间的负载有明显的倾斜，请使用PreferFixed。这允许您指定分区到主机的显式映射(任何未指定的分区都将使用一致的位置
      */
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

//    val updateFunc = (iter : Iterator[(String, Seq[Int], Option[Int])]) =>{
//      iter.flatMap{case(x, y ,z)=> Some(y.sum + z.getOrElse(0)).map(sum=>(x, sum))}
//    }



    val results = stream.flatMap(_.value().split(" ")).map((_, 1)).reduceByKey(_ + _)
//      .updateStateByKey(updateFunc, new HashPartitioner(ssc.sparkContext.defaultParallelism), true)

    //获取消费到的offset
//    stream.foreachRDD{
//      rdd =>{
//        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//        rdd.foreachPartition { iter =>
//          val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
//          println(s"主题:${o.topic} 分区:${o.partition} 开始的offset:${o.fromOffset} 结束的offset:${o.untilOffset}")
//        }
//      }
//    }

//    stream.map(record=>(record.key(), record.value())).print()

    results.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
