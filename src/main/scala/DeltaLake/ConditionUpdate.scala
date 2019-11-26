package DeltaLake

import io.delta.tables._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * @Title: ConditionUpdate
  * @ProjectName SparkTrain
  * @Description: TODO 有条件的更新而不覆盖
  * @Author yisheng.wu
  * @Date 2019/11/2616:39
  */
object ConditionUpdate {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    // 使用deltalake的加载数据的方式
    val deltaTable = DeltaTable.forPath("/deltalake/delta-table")
    // Update every even value by adding 100 to it
    deltaTable.update(
      condition = expr("id % 2 == 0"),
      set = Map("id" -> expr("id + 100"))
    )

    // Delete every even value
    deltaTable.delete(condition = expr("id % 2 == 0"))

    // Upsert (merge) new data
    val newData = spark.range(0, 20).toDF

    deltaTable.as("oldData")
      .merge(
        newData.as("newData"),
        "oldData.id = newData.id")
      .whenMatched
      .update(Map("id" -> col("newData.id")))
      .whenNotMatched
      .insert(Map("id" -> col("newData.id")))
      .execute()

    deltaTable.toDF.show()

  }
}
