package StructuredStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

object FileSourceTest {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("FileSourceTest")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setCheckpointDir("E:\\ck")

    import spark.implicits._

    // Read all the csv files written atomically in a directory
    val userSchema = new StructType().add("name", "string").add("age", "integer")
    val csvDF = spark
      .readStream
      .option("sep", ";")
      .schema(userSchema)      // Specify schema of the csv files
      .csv("E:\\csv")    // Equivalent to format("csv").load("/path/to/directory")

    val userGroup = csvDF.groupBy("name").count()

    val query = userGroup.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
