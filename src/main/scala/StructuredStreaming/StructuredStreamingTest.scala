package StructuredStreaming

import java.sql.Timestamp

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{GroupState, GroupStateTimeout, OutputMode}

case class PassInput(aid: String, s: String, uid: String, inputtime: Timestamp)
case class AidUvState(aid: String, inputtime: Timestamp, bitmap: Array[String])
case class AidUvResult(aid: String, uv: Long, inputtime: Timestamp, expire: Boolean)

object StructuredStreamingTest {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("StructuredStreamingTest")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines = spark.readStream
      .format("socket")
      .option("host", "192.168.1.171")
      .option("port", 9999)
      .load()

    val query = lines.as[String].map(line=>{
      val arr = line.split(";")
      PassInput(arr(0), arr(1), arr(2), MyTimeUtil.formatTime2TimeStamp(arr(3)))
    }).as[PassInput]
      .withWatermark("inputtime", "4 minutes")
      .groupByKey(row=>row.aid)
      .flatMapGroupsWithState(OutputMode.Append(), GroupStateTimeout.EventTimeTimeout())(updateAcrossEvents)
//      .mapGroupsWithState[AidUvState, AidUvResult](GroupStateTimeout.EventTimeTimeout()) {
//      case (aid: String, events: Iterator[PassInput], oldState: GroupState[AidUvState]) =>
//        if (oldState.hasTimedOut) {
//          val state = oldState.get
//          oldState.remove()
//          AidUvResult(aid, state.bitmap.size, state.inputtime, true)
//        } else {
//          var timemax = events.map(_.inputtime.getTime).toSeq.max
//          val uids = events.map(_.uid).toArray.distinct
//          val updatedSession = if (oldState.exists) {
//            val state = oldState.get
//            var set = Array[String]()
//            uids.foreach(row=>{
//              if (!state.bitmap.contains(row)) {
//                set = state.bitmap ++ Array(row)
//              } else {
//                set = state.bitmap
//              }
//            })
//
//            timemax = math.max(state.inputtime.getTime, timemax)
//            AidUvState(aid, new Timestamp(timemax), set)
//          } else {
//            AidUvState(aid, new Timestamp(timemax), uids)
//          }
//
//          println(s"更新状态时间:${new Timestamp(updatedSession.inputtime.getTime)}")
//          oldState.update(updatedSession)
//          //        val timestamp = if(oldState.getCurrentWatermarkMs() > 0) oldState.getCurrentWatermarkMs() else updatedSession.inputtime.getTime
//          oldState.setTimeoutTimestamp(updatedSession.inputtime.getTime, "5 seconds")
//          println(s"current watermark ${new Timestamp(oldState.getCurrentWatermarkMs())}")
//
//          AidUvResult(aid, updatedSession.bitmap.size, updatedSession.inputtime, false)
//        }
//      }
      .writeStream
      .outputMode(OutputMode.Append())
      .queryName("Aid_Uv_Count")
      .format("console")
      .start()

    query.awaitTermination()
  }

  def updateAcrossEvents(aid:String, events: Iterator[PassInput], oldState: GroupState[AidUvState]):Iterator[AidUvResult] = {

    if (oldState.hasTimedOut) {
      val state = oldState.get
      oldState.remove()
      Iterator(AidUvResult(aid, state.bitmap.size, state.inputtime, true))
    } else {
      var timemax = events.map(_.inputtime.getTime).toSeq.max
      val uids = events.map(_.uid).toArray.distinct
      val updatedSession = if (oldState.exists) {
        val state = oldState.get
        uids.foreach(row => {
          if (!state.bitmap.contains(row)) {
            state.bitmap :+ row
          }
        })
        timemax = math.max(state.inputtime.getTime, timemax)
        AidUvState(aid, new Timestamp(timemax), state.bitmap)
      } else {
        AidUvState(aid, new Timestamp(timemax), uids)
      }

      println(s"更新状态时间:${new Timestamp(updatedSession.inputtime.getTime)}")
      oldState.update(updatedSession)
      oldState.setTimeoutTimestamp(updatedSession.inputtime.getTime, "5 seconds")
      println(s"current watermark ${new Timestamp(oldState.getCurrentWatermarkMs())}")

//      Iterator(AidUvResult(aid, updatedSession.bitmap.size, updatedSession.inputtime, false))
      Iterator()
    }
  }

}
