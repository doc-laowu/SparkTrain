//package StructuredStreaming
//
//import org.apache.spark.sql.{Row, SparkSession}
//import org.apache.spark.sql.streaming.Trigger
//import org.apache.spark.sql.types.{StructType}
//
//object StructuredStreamingTest {
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder
//      .appName("StructuredStreamingTest")
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
//      .load()
//
//    val userSchema = new StructType().add("eventTime", "timestamp").add("value", "string")
//
//    // Split the lines into words
//    val words = lines.as[String].flatMap(_.split(" "))
//
//    // Generate running word count
//    val wordCounts = words
//      .withWatermark("eventTime", "5 minute")
//      .groupBy("value").mean() .count()
//
//    // Start running the query that prints the running counts to the console
//    val query = wordCounts.writeStream
//      .outputMode("append")
//      .format("console")
//      .trigger(Trigger.ProcessingTime("5 seconds"))
////      .trigger(Trigger.Once())
////      .trigger(Trigger.Continuous("5 seconds"))
//      .start()
//
//    query.awaitTermination()
//
//  }
//}
