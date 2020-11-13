package StructuredStreaming

import java.sql.Timestamp

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{GroupState, GroupStateTimeout, OutputMode}

/**
  * @Title: CountBasedWindows
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/12/3016:47
  */

case class InputRow2(device: String, timestamp: java.sql.Timestamp, x: Double)
case class DeviceState(device: String, var values: Array[Double], var count: Int)
case class OutputRow(device: String, previousAverage: Double)


object CountBasedWindows {

  def updateWithEvent(state:DeviceState, input:InputRow2):DeviceState = {
    state.count += 1
    // maintain an array of the x-axis values
    state.values = state.values ++ Array(input.x)
    state
  }

  def updateAcrossEvents(device:String, inputs: Iterator[InputRow2], oldState: GroupState[DeviceState]):Iterator[OutputRow] = {
    inputs.toSeq.sortBy(_.timestamp.getTime).toIterator.flatMap { input =>
      val state = if (oldState.exists) oldState.get
      else DeviceState(device, Array(), 0)
      val newState = updateWithEvent(state, input)
      if (newState.count >= 500) {
        // One of our windows is complete; replace our state with an empty
        // DeviceState and output the average for the past 500 items from
        // the old state
        oldState.update(DeviceState(device, Array(), 0))
        Iterator(OutputRow(device,
          newState.values.sum / newState.values.length.toDouble))
      }
      else {
        // Update the current DeviceState object in place and output no
        // records
        oldState.update(newState)
        Iterator()
      }
    }
  }


  def main(args: Array[String]): Unit = {

    val host = "192.168.1.171"
    val port = 9999

    val spark = SparkSession
      .builder
      .appName("StructuredSessionization")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // Create DataFrame representing the stream of input lines from connection to host:port
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      .option("includeTimestamp", true)
      .load()

    // Split the lines into words, treat words as sessionId of events
    val events = lines
      .as[(String, Timestamp)]
      .map {
        case (line, timestamp) => {
          val arr = line.split(" ")
          InputRow2(arr(0), timestamp, arr(1).toDouble)
        }
      }

    events
      .selectExpr("Device as device",
        "cast(Creation_Time/1000000000 as timestamp) as timestamp", "x")
      .as[InputRow2]
      .groupByKey(_.device)
      .flatMapGroupsWithState(OutputMode.Append,
        GroupStateTimeout.NoTimeout)(updateAcrossEvents)
      .writeStream
      .queryName("count_based_device")
      .format("console")
      .outputMode("append")
      .start()

  }

}
