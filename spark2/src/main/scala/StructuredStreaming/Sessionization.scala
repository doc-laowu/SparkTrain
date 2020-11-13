package StructuredStreaming

import java.sql.Timestamp

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{GroupState, GroupStateTimeout, OutputMode}

/**
  * @Title: Sessionization
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/12/3017:44
  */
case class InputRow(uid:String, timestamp:java.sql.Timestamp, x:Double, activity:String)
case class UserSession(val uid:String, var timestamp:java.sql.Timestamp, var activities: Array[String], var values: Array[Double])
case class UserSessionOutput(val uid:String, var activities: Array[String], var xAvg:Double)

object Sessionization {

  def updateWithEvent(state:UserSession, input:InputRow):UserSession = {
    // handle malformed dates
    if (Option(input.timestamp).isEmpty) {
      return state
    }
    state.timestamp = input.timestamp
    state.values = state.values ++ Array(input.x)
    if (!state.activities.contains(input.activity)) {
      state.activities = state.activities ++ Array(input.activity)
    }
    state
  }

  def updateAcrossEvents(uid:String, inputs: Iterator[InputRow], oldState: GroupState[UserSession]):Iterator[UserSessionOutput] = {
    inputs.toSeq.sortBy(_.timestamp.getTime).toIterator.flatMap { input =>
      val state = if (oldState.exists) oldState.get else UserSession(
        uid,
        new java.sql.Timestamp(6284160000000L),
        Array(),
        Array())
      val newState = updateWithEvent(state, input)
      if (oldState.hasTimedOut) {
        val state = oldState.get
        oldState.remove()
        Iterator(UserSessionOutput(uid,
          state.activities,
          newState.values.sum / newState.values.length.toDouble))
      } else if (state.values.length > 1000) {
        val state = oldState.get
        oldState.remove()
        Iterator(UserSessionOutput(uid,
          state.activities,
          newState.values.sum / newState.values.length.toDouble))
      } else {
        oldState.update(newState)
        oldState.setTimeoutTimestamp(newState.timestamp.getTime(), "5 seconds")
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
    val withEventTime = lines
      .as[(String, Timestamp)]
      .map {
        case (line, timestamp) => {
          val arr = line.split(" ")
          InputRow(arr(0), timestamp, arr(1).toDouble, arr(2))
        }
      }

    val query = withEventTime.where("x is not null")
      .selectExpr("user as uid",
        "cast(Creation_Time/1000000000 as timestamp) as timestamp",
        "x", "gt as activity")
      .as[InputRow]
      .withWatermark("timestamp", "5 seconds")
      .groupByKey(_.uid)
      .flatMapGroupsWithState(OutputMode.Append,
        GroupStateTimeout.EventTimeTimeout)(updateAcrossEvents)
      .writeStream
      .queryName("count_based_device")
      .format("console")

    query.start().recentProgress
  }

}
