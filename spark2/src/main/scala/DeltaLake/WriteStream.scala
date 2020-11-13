package DeltaLake

import java.sql.Timestamp

import Util.TimeUtil
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.collection.mutable.ListBuffer
;

/**
 * @Title: WriteStream
 * @ProjectName SparkTrain
 * @Description: TODO
 * @Author yisheng.wu
 * @Date 2019/11/2617:03
 */
object WriteStream {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .config("spark.sql.shuffle.partitions", 12)
      .config("spark.streaming.backpressure.enabled", true)
      //                .config("spark.cleaner.ttl", 120)
      .config("spark.streaming.stopGracefullyOnShutdown", true)
      .appName("JavaStructuredNetworkWordCountWindowed")
      .master("local[*]")
      .getOrCreate();

    import spark.implicits._

    val lines: Dataset[Row] = spark.readStream
      .format("socket")
      .option("host", "192.168.1.171")
      .option("port", 9999)
      //.option("includeTimestamp", true)
      .load();

    val words: Dataset[Row] = lines.as[String].flatMap((line)=>{

      val list = ListBuffer[Tuple3[String, Int, Timestamp]]()
      val arr = line.split(";")
      list.+=( (arr(0), Integer.parseInt(arr(1)), TimeUtil.formatStr2TimeStamp(arr(2))) )
      list.toIterator

    }).toDF("s", "service_names", "timestamp")

    words.withWatermark("timestamp", "4 minutes").createOrReplaceTempView("table")

    val windowedCounts = words.sparkSession.sql("""SELECT
                             |s,
                             |COUNT(1) AS COUNT,
                             |MAX(service_names) AS service_names
                             |FROM TABLE
                             |GROUP BY window(TIMESTAMP, '1 minutes', '1 minutes'), s""".stripMargin)
      .filter("service_names < 999")

    val query = windowedCounts
      .writeStream
      .format("delta")
      // 默认情况下为append模式 也可以为complete模式
      .outputMode("append")
      .option("checkpointLocation", "/tmp/checkpoint")
      // 当文件中写入的delta表中字段比当前要写入的字段少时，需要开启schema合并
      .option("mergeSchema", "true")
      .start("/deltalake/delta-table")

    query.awaitTermination()

  }

}
