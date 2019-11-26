package SparkCore

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Rdd累加器
  */
object RddAccumulators {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("RddAccumulators").setMaster("local[*]")
    val sc= SparkContext.getOrCreate(conf)
    val accum = sc.longAccumulator("My Accumulator")
    val rdd = sc.parallelize(Array(1, 2, 3, 4))
    rdd.foreach(r=> accum.add(r))
    sc.stop()
    print(accum.value.intValue())
  }

}
