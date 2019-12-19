//package RealTimeDial;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.apache.spark.sql.Column;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.functions;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//
///**
// * 大屏指标分析
// * @author mengyao
// *
// */
//public class SaleAnalysisService implements Serializable {
//
//    private static final long serialVersionUID = 8289368096001689148L;
//    //解决区域名称hash重复问题
//    private static final String SALT="aAb12";
//
//    /**
//     * 每日重置大屏指标值
//     */
//    @Deprecated
//    public void reset() {
//        RedisUtil.setObject("dtsbn_6", "{\"结账单数\":{\"val\":7270}}\"");
//        RedisUtil.setObject("dpt_7", "{\"高峰时段\":{\"val\":13}}\"");
//        RedisUtil.setObject("dtr_5", "{\"退款金额\":{\"val\":7301.8}}\"");
//        RedisUtil.setObject("dts_4", "{\"总销售额\":{\"val\":1300523.8000000005}}\"");
//        RedisUtil.setObject("curDay", "20190227\"");
//        RedisUtil.setObject("dastfa_2_20190227", "{\"各区域销售额发展趋势\":{\"重庆天地\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,9.5,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],\"创智天地\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,43.3,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],\"上海瑞虹\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,16.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],\"上海新天地\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,553.75,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]}}\"");
//        RedisUtil.setObject("dasp_1", "{\"各区域销售额占比\":{\"重庆天地\":[{\"bn\":1,\"n\":\"重庆天地\",\"sa\":9.5}],\"创智天地\":[{\"bn\":3,\"n\":\"壹方\",\"sa\":43.3}],\"上海新天地\":[{\"bn\":4,\"n\":\"新天地时尚购物中心\",\"sa\":152.0},{\"bn\":3,\"n\":\"上海新天地南里北里\",\"sa\":74.0},{\"bn\":2,\"n\":\"湖滨道购物中心\",\"sa\":100.0},{\"bn\":10,\"n\":\"新天地广场\",\"sa\":227.75}],\"上海瑞虹\":[{\"bn\":1,\"n\":\"瑞虹天地星星堂\",\"sa\":16.0}]}}\"");
//        RedisUtil.setObject("dpc_8", "{\"各项目销售额对比\":{\"重庆天地\":[{\"bn\":1,\"n\":\"重庆天地\",\"sa\":9.5}],\"创智天地\":[{\"bn\":3,\"n\":\"壹方\",\"sa\":43.3}],\"上海新天地\":[{\"bn\":4,\"n\":\"新天地时尚购物中心\",\"sa\":152.0},{\"bn\":3,\"n\":\"上海新天地南里北里\",\"sa\":74.0},{\"bn\":2,\"n\":\"湖滨道购物中心\",\"sa\":100.0},{\"bn\":10,\"n\":\"新天地广场\",\"sa\":227.75}],\"上海瑞虹\":[{\"bn\":1,\"n\":\"瑞虹天地星星堂\",\"sa\":16.0}]}}\"");
//        RedisUtil.setObject("dpac_10", "{\"各项目单均消费\":{\"重庆天地\":[{\"bn\":1,\"n\":\"重庆天地\",\"sa\":9.5}],\"创智天地\":[{\"bn\":3,\"n\":\"壹方\",\"sa\":43.3}],\"上海新天地\":[{\"bn\":4,\"n\":\"新天地时尚购物中心\",\"sa\":152.0},{\"bn\":3,\"n\":\"上海新天地南里北里\",\"sa\":74.0},{\"bn\":2,\"n\":\"湖滨道购物中心\",\"sa\":100.0},{\"bn\":10,\"n\":\"新天地广场\",\"sa\":227.75}],\"上海瑞虹\":[{\"bn\":1,\"n\":\"瑞虹天地星星堂\",\"sa\":16.0}]}}\"");
//        RedisUtil.setObject("dsfst_9", "{\"店铺销售排行\":[{\"pn\":\"新天地时尚购物中心\",\"sa\":152.0,\"sn\":\"GREYBOX COFFEE\"},{\"pn\":\"新天地广场\",\"sa\":114.75,\"sn\":\"Arabica\"},{\"pn\":\"湖滨道购物中心\",\"sa\":100.0,\"sn\":\"LOKAL\"},{\"pn\":\"上海新天地南里北里\",\"sa\":74.0,\"sn\":\"哈肯铺_手感烘焙\"},{\"pn\":\"壹方\",\"sa\":60.3,\"sn\":\"新一天便利\"},{\"pn\":\"新天地广场\",\"sa\":41.0,\"sn\":\"奈雪的茶\"},{\"pn\":\"新天地广场\",\"sa\":39.0,\"sn\":\"蒲石小点\"},{\"pn\":\"新天地广场\",\"sa\":18.0,\"sn\":\"Fresh_Every_Day\"},{\"pn\":\"瑞虹天地星星堂\",\"sa\":16.0,\"sn\":\"老盛昌\"},{\"pn\":\"新天地广场\",\"sa\":15.0,\"sn\":\"多几谷\"}]}\"");
//        RedisUtil.setObject("dptsc_11", "{\"各项目业态销售额对比\":{\"上海新天地南里北里\":[{\"n\":\"餐饮\",\"sa\":74.0,\"sn\":3}],\"新天地广场\":[{\"n\":\"餐饮\",\"sa\":227.75,\"sn\":10}],\"重庆天地\":[{\"n\":\"其它\",\"sa\":9.5,\"sn\":0}],\"湖滨道购物中心\":[{\"n\":\"餐饮\",\"sa\":100.0,\"sn\":2}],\"壹方\":[{\"n\":\"餐饮\",\"sa\":43.3,\"sn\":3}],\"瑞虹天地星星堂\":[{\"n\":\"餐饮\",\"sa\":16.0,\"sn\":1}],\"新天地时尚购物中心\":[{\"n\":\"餐饮\",\"sa\":152.0,\"sn\":4}]}}\"");
//        RedisUtil.setObject("dpsn_12", "{\"各项目店铺数量\":{\"上海新天地南里北里\":[{\"n\":\"餐饮\",\"sa\":74.0,\"sn\":3}],\"新天地广场\":[{\"n\":\"餐饮\",\"sa\":227.75,\"sn\":10}],\"重庆天地\":[{\"n\":\"其它\",\"sa\":9.5,\"sn\":0}],\"湖滨道购物中心\":[{\"n\":\"餐饮\",\"sa\":100.0,\"sn\":2}],\"壹方\":[{\"n\":\"餐饮\",\"sa\":43.3,\"sn\":3}],\"瑞虹天地星星堂\":[{\"n\":\"餐饮\",\"sa\":16.0,\"sn\":1}],\"新天地时尚购物中心\":[{\"n\":\"餐饮\",\"sa\":152.0,\"sn\":4}]}}\"");
//    }
//
//    /**
//     * 总销售额
//     * @param ds
//     */
//    public void totalSale(Dataset<Row> bill) {
//        Row[] rows = (Row[])bill
//                .filter("billType=1 and receivableAmount>=0")
//                .agg(functions.sum(new Column("receivableAmount")).alias("totalSale"), functions.count("receivableAmount"))
//                .head(1);
//        Map<String, TotalSale> map = new HashMap<String, TotalSale>();
//        double totalSale=0D;
//        if(rows.length>0){
//            Row row = rows[0];
//            if (!row.isNullAt(0)) {
//                //销售额
//                totalSale=row.getDouble(0);
//            }
//            if (!row.isNullAt(1)) {
//                //结账单数
//                totalSettlementBillNumber(new TotalSettlementBillNumber(row.getLong(1)));
//            }
//        }
//        map.put("总销售额", new TotalSale(totalSale));
//        String str = JSONObject.toJSONString(map);
//        System.out.println("====####--totalSale##" + str);
//        // 将总销售额存入redis
//        RedisUtil.setObject("dts_4", str);
//    }
//
//    /**
//     * 退款金额
//     * @param ds
//     */
//    public void totalRefund(Dataset<Row> bill) {
//        Row[] rows = (Row[]) bill
//                .filter("((billType=6) or (billType=1 and receivableAmount<0))")
//                .agg(functions.sum(functions.abs(new Column("receivableAmount"))).alias("totalSale"))
//                .head(1);
//        Map<String, TotalRefund> map=new HashMap<>();
//        map.put("退款金额", new TotalRefund(0));
//        if(rows.length>0){
//            Row row = rows[0];
//            if (!row.isNullAt(0)) {
//                map.put("退款金额", new TotalRefund(row.getDouble(0)));
//            }
//        }
//        String str=JSONObject.toJSONString(map);
//        System.out.println("====####--totalRefund##"+str);
//        //将退款金额存入redis
//        RedisUtil.setObject("dtr_5",str);
//    }
//
//    /**
//     * 高峰时段
//     *  1、时段：小时；
//     *    2、高峰：当日每小时结账单数累计最大；
//     *    3、高峰时段：所有mall累计每小时结账单数；
//     * @param ds
//     * @return {"高峰时段":{"val":12}}
//     */
//    public void peakTime(Dataset<Row> bill) {
//        //账单表本身有mallid(shopId) 因此无需关联店铺表
//        Map<String, PeakTime> map=new HashMap<>();
//        map.put("高峰时段", new PeakTime(8));
//        Row[] rows = (Row[])bill
//                .filter("billType=1")
//                .groupBy("hour")
//                .agg(functions.sum("receivableAmount").alias("totalSale"))
//                .orderBy(new Column("totalSale").desc())
//                .limit(1)
//                .head(1);
//        if(rows.length>0){
//            Row row = rows[0];
//            if (!row.isNullAt(0)) {
//                map.put("高峰时段", new PeakTime(row.getAs(0)));
//            }
//        }
//        String str=JSONObject.toJSONString(map);
//        System.out.println("====####--peakTime##"+str);
//        //将高峰时段放入redis
//        RedisUtil.setObject("dpt_7", str);
//    }
//
//    /**
//     * 各区域销售发展趋势-多个区域（每个区域下有多个mall）当日0点~24点的累计销售额
//     * @param bill
//     * @param ruian
//     * @param curDay
//     */
//    public void areaSaleTrendForAll(Dataset<Row> bill, Dataset<Row> ruian,Set<String> areaSet, String curDay) {
//        Row[] rows = (Row[])bill
//                .filter("billType=1 and receivableAmount>0")
//                .join(ruian, bill.col("shopId").equalTo(ruian.col("rmid")), "leftouter")
//                .groupBy("area_cn", "hour")
//                .agg(functions.sum("receivableAmount").alias("areaDayHourSale"))
//                .orderBy("areaDayHourSale")
//                .select("area_cn","areaDayHourSale","hour")
//                .collect();
////        Map<String, Map<String,Map<Integer, Double>>> map = new HashMap<>();
//        Map<String, Map<String,Collection<Double>>> map = new HashMap<>();
//        Map<String,AreaSaleTrendAll> maps=new HashMap<>();
//        if(rows.length>0){
//            for (Row row : rows) {
//                String areaCn=null;
//                double areaDayHourSale=0D;
//                int saleHour=0;
//                if(!row.isNullAt(0)){
//                    areaCn=row.getString(0);
//                }
//                if(!row.isNullAt(1)){
//                    areaDayHourSale=row.getDouble(1);
//                }
//                if(!row.isNullAt(2)){
//                    saleHour=row.getInt(2);
//                }
//                if(maps.containsKey(areaCn+SALT)){
//                    AreaSaleTrendAll ast=maps.get(areaCn+SALT);
//                    ast.getVals().put(saleHour, areaDayHourSale);
//                }else{
//                    HashMap<Integer,Double> vals=new HashMap<Integer,Double>();
//                    vals.put(saleHour, areaDayHourSale);
//                    maps.put(areaCn+SALT, new AreaSaleTrendAll(areaCn, saleHour, areaDayHourSale));
//                }
//            }
//        }
//        //填充没有销售额的区域记录，使数据更加完整。
//        areaSet.forEach(areaCn->{
//            if(!maps.containsKey(areaCn+SALT)){
//                maps.put(areaCn+SALT, new AreaSaleTrendAll(areaCn));
//            }
//        });
//
//        System.out.println("==####========填充hou====begin=====================");
//        for (String key:maps.keySet()) {
//            System.out.println("Key:"+key);
//        }
//        System.out.println("==####========填充hou=====end====================");
//
////        Map<String,Map<Integer, Double>> rs=new HashMap<>();
////        for(AreaSaleTrendAll asta:maps.values()){
////            rs.put(asta.getAreaCn(), asta.getVals());
////        }
//        Map<String,Collection<Double>> rs=new HashMap<>();
//        for(AreaSaleTrendAll asta:maps.values()){
//            rs.put(asta.getAreaCn(), asta.getVals().values());
//        }
//        map.put("各区域销售额发展趋势", rs);
//        //转成json 字符串
//        String str=JSONObject.toJSONString(map);
//        System.out.println("====####--areaSaleTrendForAll##"+str);
//        //放入redis
//        RedisUtil.setObject("dastfa_2_"+curDay, str);
//        //维护redis中最新日期
//        if(!RedisUtil.existsObject("curDay")){//如果为空，说明是第一次运行，直接将当前日期设置到redis中
//            RedisUtil.setObject("curDay", curDay);
//        }else{
//            String curDayRedis=(String) RedisUtil.getObject("curDay");
//            if((curDayRedis.compareTo(curDay))<0){//说明当前日期大于redis中的日期，更新
//                RedisUtil.setObject("curDay", curDay);
//            }
//        }
//    }
//
//    /**
//     * 各项目销售额对比
//     *     1、各项目：各个mall；
//     *    2、单一项目销售额：mall的当日开始营业时间到当前时间累计销售额；
//     */
//    public void projectContrast(Dataset<Row> bill, Dataset<Row> ruian,Map<String, Set<String>> ruianMallAll) {
//        Row[] rows = (Row[])bill
//                .filter("billType=1 and receivableAmount>0")
//                .join(ruian, bill.col("shopId").equalTo(ruian.col("rmid")), "leftouter")
//                .groupBy("name", "area_cn")
//                .agg(functions.sum("receivableAmount").alias("mallDaySale"), functions.count("receivableAmount").alias("mallDayBillNum"))
////                .orderBy("area_cn")
//                .select("name","area_cn","mallDaySale","mallDayBillNum")
//                .collect();
//        Map<String,List<AreaProjSale>> rs=new HashMap<>();
//        if(rows.length>0) {
//            for (Row row : rows) {
//                String mallName=null;
//                String areaCn=null;
//                double mallDaySale=0D;
//                long mallDayBillNum=0L;
//                AreaProjSale obj=new AreaProjSale();
//                if(!row.isNullAt(0)){//项目、mall的名称
//                    mallName=row.getString(0);
//                    obj.setN(mallName);
//                }
//                if(!row.isNullAt(1)){//区域名称
//                    areaCn=row.getString(1);
//                }
//                if(!row.isNullAt(2)){//mall的日销售额
//                    mallDaySale=row.getDouble(2);
//                    obj.setSa(mallDaySale);
//                }
//                if(!row.isNullAt(3)){//mall的日结账单数
//                    mallDayBillNum=row.getLong(3);
//                    obj.setBn(mallDayBillNum);
//                }
//                //判断是否有该区域
//                if(rs.containsKey(areaCn)){
//                    rs.get(areaCn).add(obj);
//                }else{//不存在该区域
//                    List<AreaProjSale> list=new ArrayList<>();
//                    list.add(obj);
//                    rs.put(areaCn, list);
//                }
//            }
//        }
//        //填充未产生账单的数据，默认0
//        //获取全部瑞安的区域和mallName的集合
//        Set<Entry<String,Set<String>>> entrySet = ruianMallAll.entrySet();
//        for(Map.Entry<String, Set<String>> entry:entrySet){
//            String areaCn=entry.getKey();//获取区域名称
//            //获取每个区域的标准mall的集合
//            Set<String> mallSet=entry.getValue();
//            if(rs.containsKey(areaCn)){//实际数据中已经存在该区域相关数据
//                //判断实际数据mall是否完整
//                //用来存放实际数据中mallname的集合
//                Set<String> mallSetCur=new HashSet<>();
//                //用来存放没有账单的mall的集合
//                Set<String> mallSetNew=new HashSet<>();
//                //遍历该区域下实际数据集合 并填充set
//                for(AreaProjSale obj:rs.get(areaCn)){
//                    mallSetCur.add(obj.getN());
//                }
//                //求两个set的差集合  将标准数据放入
//                mallSetNew.addAll(mallSet);
//                //求差集合  标准数据-实际数据 得到差集
//                mallSetNew.removeAll(mallSetCur);
//                //遍历差集合 ，填充默认值
//                mallSetNew.forEach(mn->{
//                    rs.get(areaCn).add(new AreaProjSale(mn));
//                });
//            }else{//该区域不存在
//                //便利该区域下标准mall集合，逐个放入
//                List<AreaProjSale> list=new ArrayList<>();
//                mallSet.forEach(mn->{
//                    list.add(new AreaProjSale(mn));
//                });
//                rs.put(areaCn, list);
//            }
//        }
//        //各区域销售额占比
//        areaSaleProportion(rs);
//        //各区域单均消费
//        projectAvgConsumer(rs);
//        //转成json
//        Map<String, Map<String,List<AreaProjSale>>> pmap=new HashMap<>();
//        pmap.put("各项目销售额对比", rs);
//        //转成json
//        String str=JSONObject.toJSONString(pmap);
//        System.out.println("====####--projectContrast##"+str);
//        //1 8各项目销售额对比dpc_8
//        RedisUtil.setObject("dpc_8", str);
//    }
//
//    /**
//     * 所有mall中销售额最高的top10店铺
//     * @param bill
//     * @param shop
//     * @param ruian
//     */
//    public void saleForShopTop10(Dataset<Row> bill, Dataset<Row> shop, Dataset<Row> ruian) {
//        Row[] rows = (Row[])bill
//                .filter("billType=1 and receivableAmount>0")
//                .join(shop, bill.col("shopEntityId").equalTo(shop.col("shop_entity_id")), "leftouter")
//                .join(ruian, bill.col("shopId").equalTo(ruian.col("rmid")), "leftouter")
//                .groupBy("shop_entity_name", "name")
//                .agg(functions.sum("receivableAmount").alias("shopDaySale"))
//                .orderBy(new Column("shopDaySale").desc())
//                .select("shop_entity_name","name","shopDaySale")
//                .limit(10)
//                .head(10);
//        List<ShopSaleRank> ssrList=new LinkedList<>();
//        if(rows.length>0) {
//            for (Row row : rows) {
//                ShopSaleRank ssr=new ShopSaleRank();
//                if(!row.isNullAt(0)){//店铺名称
//                    ssr.setSn(row.getString(0));
//                }
//                if(!row.isNullAt(1)){//项目/mall名称
//                    ssr.setPn(row.getString(1));
//                }
//                if(!row.isNullAt(2)){//店铺日销售额
//                    ssr.setSa(row.getDouble(2));
//                }
//                ssrList.add(ssr);
//            }
//        }
//        //转成json
//        Map<String, List<ShopSaleRank>> pmap=new HashMap<>();
//        pmap.put("店铺销售排行", ssrList);
//        String str=JSON.toJSONString(pmap);
//        System.out.println("====####--saleForShopTop10##"+str);
//        //将10个店铺日销售额放入redis
//        RedisUtil.setObject("dsfst_9",str);
//    }
//
//    /**
//     * 各项目业态销售额对比
//     *
//     * @param session
//     * @param beginYMDH
//     * @param endYMDH
//     */
//    public void projectTypeSaleContrast(Dataset<Row> bill, Dataset<Row> shop, Dataset<Row> type, Dataset<Row> ruian,
//                                        Set<String> areaSet,Set<String> mallSet,Set<String> ruianTypeAll) {
//        Row[] rows = (Row[])bill
//                .filter("billType=1 and receivableAmount>0")
//                .join(shop, shop.col("shop_entity_id").equalTo(bill.col("shopEntityId")), "leftouter")
//                .join(type, type.col("id").equalTo(shop.col("shop_entity_type_root")), "leftouter")
//                .join(ruian, ruian.col("rmid").equalTo(bill.col("shopId")))
//                .groupBy("name", "shop_type_name")
//                .agg(functions.sum("receivableAmount").alias("shopTypeDaySale"), functions.countDistinct("shop_entity_id").alias("shopNum"))
//                .select("name","shop_type_name","shopTypeDaySale","shopNum")
//                .collect();
//        Map<String,List<ProjectTypeShopNumber>> map=new HashMap<>();
//        if(rows.length>0) {
//            for (Row row : rows) {
//                ProjectTypeShopNumber pac=new ProjectTypeShopNumber();
//                String mallName=null;
//                if(!row.isNullAt(0)){//项目、mall名称
//                    mallName=row.getString(0);
//                }
//                if(!row.isNullAt(1)){//业态名称
//                    pac.setN(row.getString(1));
//                }else{
//                    pac.setN("其它");
//                }
//                if(!row.isNullAt(2)){//mall的业态日销售额
//                    pac.setSa(row.getDouble(2));
//                }
//                if(!row.isNullAt(3)){//mall的业态店铺数量
//                    pac.setSn(row.getLong(3));
//                }
//                if(map.containsKey(mallName)){//更新map中的list列表
//                    List<ProjectTypeShopNumber> pacList=map.get(mallName);
//                    pacList.add(pac);
//                }else{//新的mall 新建列表放入map
//                    List<ProjectTypeShopNumber> pacList=new LinkedList<>();
//                    pacList.add(pac);
//                    map.put(mallName, pacList);
//                }
//            }
//        }
//        //为没有产生账单的业态或mall填充默认数据，是数据看起来完整
//        mallSet.forEach(mallName->{
//            if(!map.containsKey(mallName)) {//没有账单的mall
//                List<ProjectTypeShopNumber> list=new ArrayList<>();
//                //遍历全量业态，进行填充
//                ruianTypeAll.forEach(type_->{
//                    list.add(new ProjectTypeShopNumber(type_,0.0,0));
//                });
//                map.put(mallName,list);
//            }else{//有账单的mall
//                //用来获取实际账单数据中已存在的业态
//                Set<String> ruianTypeCur = new HashSet<>();
//                //用来存放没有账单的业态
//                Set<String> ruianTypeNew = new HashSet<>();
//                //遍历数据，提取实际数据中的业态
//                for(ProjectTypeShopNumber obj:map.get(mallName)){
//                    ruianTypeCur.add(obj.getN());
//                }
//                //将产生账单的业态和全量业态求差即
//                ruianTypeNew.addAll(ruianTypeAll);
//                ruianTypeNew.removeAll(ruianTypeCur);
//                //将没有实际账单的业态数据填从（默认值填充）
//                for(String type_:ruianTypeNew){
//                    map.get(mallName).add(new ProjectTypeShopNumber(type_,0.0,0));
//                }
//            }
//        });
//        //为==各项目店铺数量==填充数据
//        projectShopNumber(map);
//        //转成json
//        Map<String, Map<String,List<ProjectTypeShopNumber>>> pmap=new HashMap<>();
//        pmap.put("各项目业态销售额对比", map);
//        String str=JSON.toJSONString(pmap);
//        System.out.println("====####--projectTypeSaleContrast##"+str);
//        RedisUtil.setObject("dptsc_11",str);
//    }
//
//    //=============================================================================================================
//    /**
//     * 结账单数
//     *
//     * @param tsb
//     */
//    public void totalSettlementBillNumber(TotalSettlementBillNumber tsb) {
//        Map<String, TotalSettlementBillNumber> map=new HashMap<>();
//        map.put("结账单数", tsb);
//        String str=JSONObject.toJSONString(map);
//        System.out.println("====####--totalSettlementBillNumber##"+str);
//        //将结账单数放入redis
//        RedisUtil.setObject("dtsbn_6", str);
//    }
//
//    /**
//     * 各区域销售占比
//     * 直接将数据封装成json,无需额外处理
//     *
//     * @param rs 已经封装好的数据，请参考{@link com.mengyao.graph.etl.apps.dashboard.service.SaleAnalysisService.projectContrast()}
//     */
//    private void areaSaleProportion(Map<String, List<AreaProjSale>> rs) {
//        Map<String, Map<String,List<AreaProjSale>>> pmap=new HashMap<>();
//        pmap.put("各区域销售额占比 ", rs);
//        //转成json
//        String str=JSONObject.toJSONString(pmap);
//        System.out.println("====####--areaSaleProportion##"+str);
//        //各区域销售占比dasp_1
//        RedisUtil.setObject("dasp_1", str);
//    }
//
//    /**
//     *  各项目单均消费
//     *  版本二 营业时间24小时制
//     *    1、各项目：各个mall；
//     *    2、单均消费：mall的当日开始营业时间到当前时间累计销售额，应收额累计/结账单累计；
//     *  直接将数据封装成json，无需额外处理
//     *
//     *  @param rs 已经封装好的数据，请参考{@link com.mengyao.graph.etl.apps.dashboard.service.SaleAnalysisService.projectContrast()}
//     */
//    private void projectAvgConsumer(Map<String, List<AreaProjSale>> rs) {
//        Map<String, Map<String,List<AreaProjSale>>> pmap=new HashMap<>();
//        pmap.put("各项目单均消费", rs);
//        //转成json
//        String str=JSONObject.toJSONString(pmap);
//        System.out.println("====####--projectAvgConsumer##"+str);
//        //10各项目单均消费dpac_10
//        RedisUtil.setObject("dpac_10", str);
//    }
//
//    /**
//     * 各项目店铺数量
//     * 数据填充自上面的方法
//     *
//     * {@link projectTypeSaleContrast(SparkSession session,String beginYMDH,String endYMDH)}
//     */
//    private void projectShopNumber(Map<String,List<ProjectTypeShopNumber>> map) {
//        //转成json
//        Map<String, Map<String,List<ProjectTypeShopNumber>>> pmap=new HashMap<>();
//        pmap.put("各项目店铺数量", map);
//        String str=JSON.toJSONString(pmap);
//        System.out.println("====####--projectShopNumber##"+str);
//        RedisUtil.setObject("dpsn_12",str);
//    }
//
//
//}
