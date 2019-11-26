package SparkStreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._

object KafkaCreateRdd {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("KafkaCreateRdd").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.52.129:9092,192.168.52.130:9092,192.168.52.131:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test_group_id",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val offsetRanges = Array(
      // 主题, 分区, 包含起始偏移量, 独占结束偏移量
      OffsetRange("flume-data", 0, 0, 100),
      OffsetRange("flume-data", 1, 0, 100)
    )
    //这个api还在实验阶段
//    val rdd = KafkaUtils.createRDD[String, String](ssc, kafkaParams, offsetRanges, LocationStrategies)

    ssc.start()
    ssc.awaitTermination()
  }

}
