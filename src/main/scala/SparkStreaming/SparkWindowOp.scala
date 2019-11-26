package SparkStreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Durations, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object SparkWindowOp {

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
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("flume-data")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val result = stream.flatMap(_.value().split(" ")).map((_, 1))
      //第一个函数是当前的窗口的值求和  第二个函数是当前的窗口的值减去前一个窗口的值  这个是优化后的
      .reduceByKeyAndWindow((v1:Int, v2:Int)=>{v1+v2}, (v1:Int, v2:Int)=>{v1-v2}, Durations.seconds(20), Durations.seconds(10))

    result.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
