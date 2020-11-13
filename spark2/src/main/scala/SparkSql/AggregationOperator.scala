package SparkSql

import org.apache.spark.sql.{DataFrame, SparkSession, functions}

/**
  * @Title: AggregationOperator
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/12/2517:23
  */
case class MemberOrderInfo(area:String,memberType:String,product:String,price:Int)

object AggregationOperator {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("AggregationOperator")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    val orders = Seq(
      MemberOrderInfo("深圳","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("深圳","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("深圳","钻石会员","钻石会员3个月",70),
      MemberOrderInfo("深圳","钻石会员","钻石会员12个月",300),
      MemberOrderInfo("深圳","铂金会员","铂金会员3个月",60),
      MemberOrderInfo("深圳","铂金会员","铂金会员3个月",60),
      MemberOrderInfo("深圳","铂金会员","铂金会员6个月",120),
      MemberOrderInfo("深圳","黄金会员","黄金会员1个月",15),
      MemberOrderInfo("深圳","黄金会员","黄金会员1个月",15),
      MemberOrderInfo("深圳","黄金会员","黄金会员3个月",45),
      MemberOrderInfo("深圳","黄金会员","黄金会员12个月",180),
      MemberOrderInfo("北京","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("北京","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("北京","铂金会员","铂金会员3个月",60),
      MemberOrderInfo("北京","黄金会员","黄金会员3个月",45),
      MemberOrderInfo("上海","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("上海","钻石会员","钻石会员1个月",25),
      MemberOrderInfo("上海","铂金会员","铂金会员3个月",60),
      MemberOrderInfo("上海","黄金会员","黄金会员3个月",45)
    )

    //把seq转换成DataFrame
    val memberDF:DataFrame =orders.toDF()
    //把DataFrame注册成临时表
    memberDF.createOrReplaceTempView("orderTempTable")

    /**
      * grouping_sets 聚合操作
      *
      *
      * GROUPING SETS(area, memberType, product)可以表示为：
      *
      * GROUP BY area
      * UNION GROUP BY memberType
      * UNION GROUP BY product 操作
      *
      */
//    val query_groupping_sets = """SELECT
//                                 |area,
//                                 |memberType,
//                                 |product,
//                                 |SUM(price) AS total
//                                 |FROM orderTempTable
//                                 |GROUP BY area, memberType, product GROUPING SETS(area, memberType, product)""".stripMargin
//
//    spark.sql(query_groupping_sets).show(1000)

    /**
      *
      * ROLL UP 聚合操作
      *
      * ROLL UP area, memberType, product可以表示为:
      *
      * GROUP BY area, memberType, product
      * UNION
      * GROUP BY area, memberType
      * UNION
      * GROUP BY area
      * UNION
      * GROUP BY 全表
      *
      */
//    val query_rollup = """SELECT
//                         |area,
//                         |memberType,
//                         |product,
//                         |SUM(price) AS total
//                         |FROM orderTempTable
//                         |GROUP BY area, memberType, product WITH ROLLUP""".stripMargin
//
//    spark.sql(query_rollup).show(1000)

    /**
      *
      * CUBE  area, memberType, product可以表示为:
      *
      * GROUP BY area, memberType, product
      * UNION
      * GROUP BY area, memberType
      * UNION
      * GROUP BY memberType, product
      * UNION
      * GROUP BY area, product
      * UNION
      * GROUP BY area
      * UNION
      * GROUP BY memberType
      * UNION
      * GROUP BY product
      * UNION
      * GROUP BY 全表
      *
      *
      */
//    val query_cube = """SELECT
//                       |area,
//                       |memberType,
//                       |product,
//                       |SUM(price) AS total
//                       |FROM orderTempTable
//                       |GROUP BY area, memberType, product WITH CUBE""".stripMargin
//
//    spark.sql(query_cube).show(1000)


    /**
      *  透视和逆透视
      */
    memberDF.groupBy("area").pivot("product").sum().show()

    memberDF.cube("area", "memberType")
      .agg(functions.grouping_id(), functions.sum("product"))
      .orderBy(functions.expr("grouping_id()").desc)
      .show()

    spark.stop()

  }

}
