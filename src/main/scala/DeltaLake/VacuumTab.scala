package DeltaLake

import io.delta.tables.DeltaTable
import org.apache.spark.sql.SparkSession

/**
  * @Title: VacuumTab
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/12/317:16
  */
object VacuumTab {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      // 在spark 中使用SQL命令
      .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      .getOrCreate()

    val deltaTable = DeltaTable.forPath("/deltalake/delta-table")
    // vacuum files not required by versions older than the default retention period
    deltaTable.vacuum()
    // vacuum files not required by versions more than 100 hours old
    deltaTable.vacuum(100)

    // 获取表的全部的修改记录
    val fullHistoryDF = deltaTable.history()

    // 获取delta table最近一次的修改记录
    val lastOperationDF = deltaTable.history(1)

    // 将未分区的parquet表转换为delta table
    val deltaTableByParquet = DeltaTable.convertToDelta(spark, "parquet.`/path/to/table`")

    // Convert partitioned parquet table at path '/path/to/table' and partitioned by integer column named 'part'
    val partitionedDeltaTable = DeltaTable.convertToDelta(spark, "parquet.`/path/to/table`", "part int")
  }

}
