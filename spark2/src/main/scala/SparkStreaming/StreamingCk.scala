package SparkStreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}

object StreamingCk {

  def functionToCreateContext() :StreamingContext = {

    val conf = new SparkConf().setAppName("StreamingCk").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))

    //在设置检查点的时候，默认时间间隔是批处理间隔的倍数，至少为10秒。可以使用dstream.checkpoint（checkpointInterval）进行设置。
    // 通常，DStream的5-10个滑动间隔的检查点间隔是一个很好的设置。
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

    stream.checkpoint(Duration(25))

    ssc
  }

  def main(args: Array[String]): Unit = {

    val context = StreamingContext.getOrCreate("E:/ck", functionToCreateContext)

    context.start()
    context.awaitTermination()
  }
}
