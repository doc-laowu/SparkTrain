//package StructuredStreaming
//
//import java.sql.Timestamp
//
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.streaming.Trigger
//import org.apache.spark.sql.functions._
//
//object StructedStreamingFromKafka {
//
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder
//      .appName("StructedStreamingFromKafka")
//      .master("local[*]").getOrCreate()
//
//    //set the recovry path
//    spark.sparkContext.setCheckpointDir("E:/ck")
//
//    import spark.implicits._
//
//    // Create DataFrame representing the stream of input lines from connection to localhost:9999
//    val lines = spark.readStream
//      .format("socket")
//      .option("host", "mymaster01")
//      .option("port", 9999)
//      .option("includeTimestamp", true)
//      .load()
//
//    // Split the lines into words
//    val words = lines.as[(String, Timestamp)]
//      .flatMap(line=>line._1.split(" ").map(word=>(word, line._2)))
//      .toDF("word", "timestamp")
//
//    // Generate running word count
//    val wordCounts = words
//      .withWatermark("timestamp", "10 seconds")
//      .groupBy(window($"timestamp", "10 seconds", "5 seconds"), $"word").agg() .count()
//
//    // Start running the query that prints the running counts to the console
//    val query = wordCounts.writeStream
//      .outputMode("Append")
//      .format("console")
//      .trigger(Trigger.ProcessingTime("5 seconds"))
//      //      .trigger(Trigger.Once())
//      //      .trigger(Trigger.Continuous("5 seconds"))
//      .option("truncate", "false")
//      .start()
//
//    query.awaitTermination()
//
//  }
//
//}
