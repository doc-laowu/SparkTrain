package DeltaLake

import org.apache.spark.sql.SparkSession

/**
  * @Title: ReadStream
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2711:37
  */
object ReadStream {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .config("spark.sql.shuffle.partitions", 12)
      .master("local[*]")
      .getOrCreate();
    // deltalake读取流表
    spark.readStream
      .format("delta")
      //  ignore transactions that delete data at partition boundaries
      .option("ignoreDeletes", "true")
      // 如果由于更新、合并、删除(在分区内)或覆盖等数据更改操作导致文件必须在源表中重写，则ignoreChanges重新处理更新
      .option("ignoreChanges", "true")
      .load("/deltalake/delta-table")

  }

}
