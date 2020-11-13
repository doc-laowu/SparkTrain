package SparkSql

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * 一下包含加载jdbc数据源和spark sql优化的知识
  */

object LoadDataFromJdbc {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("LoadDataFromJdbc").setMaster("local[*]")

    val spark = SparkSession.builder().config(conf)
      //Spark SQL将根据数据统计信息自动为每列选择压缩编解码器。
      .config("spark.sql.inMemoryColumnarStorage.compressed", "true")
      //控制批量列式缓存的大小，注意进行数据缓存的时候可能发生OOM
      .config("spark.sql.inMemoryColumnarStorage.batchSize", "10000")
      //可以在同一时间扫描通过字节数测量的打开文件的估计成本。 将多个文件放入分区时使用。
      // 过度估计会更好，那么带有小文件的分区将比具有更大文件的分区（首先安排的分区）更快。
      .config("spark.sql.files.openCostInBytes", "4194304 ")
      //广播连接中广播等待时间的超时（以秒为单位）
      .config("spark.sql.broadcastTimeout", "300")
      //配置在执行连接时将广播到所有工作节点的表的最大大小（以字节为单位）。 通过将此值设置为-1，可以禁用广播。
      // 请注意，目前仅支持运行命令ANALYZE TABLE <tableName> COMPUTE STATISTICS noscan的Hive Metastore表的统计信息。
      .config("spark.sql.autoBroadcastJoinThreshold", "10485760 ")
      //配置为连接或聚合数据移动数据时要使用的分区数。
      .config("spark.sql.shuffle.partitions", "200")
      .getOrCreate()

    //广播表和其他表的join操作
    import org.apache.spark.sql.functions.broadcast
    broadcast(spark.table("src")).join(spark.table("records"), "key").show()

    import spark.implicits._

    // Note: JDBC loading and saving can be achieved via either the load/save or jdbc methods
    // Loading data from a JDBC source
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:postgresql:dbserver")
      .option("dbtable", "schema.tablename")
      .option("user", "username")
      .option("password", "password")
      .load()

    //缓存表到内存中 第一种方式
    spark.catalog.cacheTable("tablename")
    spark.catalog.uncacheTable("tablename")
    //第二种方式
    jdbcDF.cache()
    //Mark the Dataset as non-persistent, and remove all blocks for it from memory and disk.
    jdbcDF.unpersist()

    val connectionProperties = new Properties()
    connectionProperties.put("user", "username")
    connectionProperties.put("password", "password")
    val jdbcDF2 = spark.read
      .jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties)
    // Specifying the custom data types of the read schema
    //指定从数据库读取数据的时候的字段类型
    connectionProperties.put("customSchema", "id DECIMAL(38, 0), name STRING")
    val jdbcDF3 = spark.read
      .jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties)

    // Saving data to a JDBC source
    jdbcDF.write
      .format("jdbc")
      .option("url", "jdbc:postgresql:dbserver")
      .option("dbtable", "schema.tablename")
      .option("user", "username")
      .option("password", "password")
      .save()

    jdbcDF2.write
      .jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties)

    // Specifying create table column data types on write
    //在写数据库的时候指定每个字段的类型
    jdbcDF.write
      .option("createTableColumnTypes", "name CHAR(64), comments VARCHAR(1024)")
      .jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties)

  }
}
