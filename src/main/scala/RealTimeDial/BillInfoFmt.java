//package RealTimeDial;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//
///**
// * 账单信息 gag_bill.bill_info
// *
// * @author jmb
// * @update 2018-12-13 for mengyao
// */
//public class BillInfoFmt implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 预结单
//     */
//    public static final String BILLTYPE_JUJIEDAN = "2";
//    /**
//     * 结账单
//     */
//    public static final String BILLTYPE_JIEZHANGDAN = "1";
//    /**
//     * 日结账单
//     */
//    public static final String BILLTYPE_RIJIEDAN = "3";
//    /**
//     * 点菜单
//     */
//    public static final String BILLTYPE_DIANCAIDAN = "7";
//
//    /**
//     * 账单编号（系统产生），UUID
//     */
//    private String id;
//
//    /**
//     * 账单编号（系统产生），HBase主键
//     */
//    private String rowKey;
//
//    /**
//     * 商家编号（系统产生）
//     */
//    private String shopId;
//
//    /**
//     * 商家名称（系统产生）
//     */
//    private String shopName;
//
//    /**
//     * 实体店编号（系统产生）
//     */
//    private String shopEntityId;
//
//    /**
//     * 实体店名称（系统产生）
//     */
//    private String shopEntityName;
//
//    /**
//     * 创建时间（系统产生）
//     */
//    private String createTime;
//
//    /**
//     * 最后一次修改时间（系统产生）
//     */
//    private String cTimeStamp;
//
//    /**
//     * 信息上传时间（以header中创建时间为准yyyyMMddHHmmss）
//     */
//    private String hcTime;
//
//    /**
//     * 流水号(header中的流水号)
//     */
//    private String hserial;
//
//    /**
//     * 修正账单数据的设备编号（如果没有修正，则与原始上传账单的采集终端编号相同）
//     */
//    private String fixTerminal;
//
//    /**
//     * 采集终端编号（截获）
//     */
//    private String terminalNumber;
//
//    /**
//     * 账单文件名称，唯一（截获），12位MAC地址+17位时间
//     */
//    private String billfileName;
//
//    /**
//     * 反扫支付时终端生成的终端流水号UUID（全球唯一）[先支付后打单必填]
//     */
//    private String tsuuid;
//
//    /**
//     * 历史账单文件名称，记录合并过程中历次账单文件名称，全量，mongodb为Array
//     */
//    private List<String> billfileNameHis;
//
//    /**
//     * 账单序号，不保证唯一（截获）
//     */
//    private String billNo;
//
//    /**
//     * 截获时间（截获）
//     */
//    private String interceptTime;
//
//    /**
//     * 店铺全名（截获）
//     */
//    private String shopEntityFullName;
//
//    /**
//     * 店铺地址（截获）
//     */
//    private String shopEntityAddress;
//
//    /**
//     * 电话（截获）
//     */
//    private String telephone;
//
//    /**
//     * 售货员（截获）
//     */
//    private String saler;
//
//    /**
//     * 收银台（截获）
//     */
//    private String checkstand;
//
//    /**
//     * 收银员（截获）
//     */
//    private String cashier;
//
//    /**
//     * 应收金额（截获）
//     */
//    private Double receivableAmount;
//
//    /**
//     * 原始应收金额（截获）
//     */
//    private Double defaultReceivableAmount;
//
//    /**
//     * 商品数量（截获）
//     */
//    private Double totalNum;
//
//    /**
//     * 原始商品数量（截获）
//     */
//    private Double defaultTotalNum;
//
//    /**
//     * 小票流水号（截获）
//     */
//    private String billSerialNumber;
//
//    /**
//     * 总金额（截获）
//     */
//    private Double totalFee;
//
//    /**
//     * 原始总金额（截获）
//     */
//    private Double defaultTotalFee;
//
//    /**
//     * 实收金额（截获）
//     */
//    private Double paidAmount;
//
//    /**
//     * 原始实收金额（截获）
//     */
//    private Double defaultPaidAmount;
//
//    /**
//     * 折扣金额（截获）
//     */
//    private Double discountAmount;
//
//    /**
//     * 原始折扣金额（截获）
//     */
//    private Double defaultDiscountAmount;
//
//    /**
//     * 优惠金额（截获）
//     */
//    private Double couponAmount;
//
//    /**
//     * 原始优惠金额（截获）
//     */
//    private Double defaultCouponAmount;
//
//    /**
//     * 找零金额（截获）
//     */
//    private Double changeAmount;
//
//    /**
//     * 原始找零金额（截获）
//     */
//    private Double defaultChangeAmount;
//
//    /**
//     * 结算方式（支持多项）（截获）例：[{"a":5.0,"p":"现金"},{"a":10.01,"p":"书券"}],"a":结算金额，小数[截获，必填],
//     * "p":"结算方式[截获，必填]"
//     */
//    private List<String> settlementWay;
//
//    /**
//     * 销售时间（截获）
//     */
//    private String saleTime;
//
//    /**
//     * 会员卡号（截获）
//     */
//    private String memberCardNumber;
//
//    /**
//     * 累计消费（截获）
//     */
//    private Double totalConsumption;
//
//    /**
//     * 原始累计消费（截获）
//     */
//    private Double defaultTotalConsumption;
//
//    /**
//     * 网址（截获）
//     */
//    private String website;
//
//    /**
//     * 小票图片（截获）,只存url
//     */
//    private String billImage;
//
//    /**
//     * 商品详情（支持多项）（截获）[{"name":"可乐","itemserial":"PUMU00123","price":5.01,"totalnum":5.0,"totalprice":25.05},{"name":"金枪鱼","itemserial":"FOOD02012","price":10.55,"totalnum":1.5,"totalprice":15.83}]
//     * ,"name":"商品名称[截获，选填]","itemserial":"条形码[截获，选填]","price":单价,小数[截获，选填],"totalnum":总数,小数[截获，选填],"totalprice":总价,小数[截获，选填]
//     */
//    private List<String> goodsDetails;
//
//    /**
//     * 房间号（截获）（酒店特有）
//     */
//    private String roomNo;
//
//    /**
//     * 入住姓名（截获）（酒店特有）
//     */
//    private String checkinName;
//
//    /**
//     * 桌号（截获）（餐饮特有）
//     */
//    private String deskNo;
//
//    /**
//     * 消费人数（截获）（一般用于餐饮）
//     */
//    private Double consumeNum;
//
//    /**
//     * 原始消费人数（截获）（一般用于餐饮）
//     */
//    private Double defaultConsumeNum;
//
//    /**
//     * 原始账单所有文本信息(截获)
//     */
//    private String billText;
//
//    /**
//     * 入住时间(截获)(酒店特有)
//     */
//    private String inTime;
//
//    /**
//     * 离店时间(截获)(钟点房特有)
//     */
//    private String outTime;
//    /**
//     * 默认打印时间
//     */
//    private String defaultPrintDate;
//    /**
//     * 默认入住时间
//     */
//    private String defaultInTime;
//    /**
//     * 默认离店时间
//     */
//    private String defaultOutTime;
//
//    /**
//     * 上传类型 1：全单上传 2. 筛选账单上传
//     */
//    private String uploadType;
//
//    /**
//     * 除了上面截获外的自定义数据（截获），json串（Map<String, Object>）,json串中的key,value由采集终端自定义。
//     */
//    private Map<String, String> customRecord;
//
//    /**
//     * 账单类型1:结账单2:预结单 3:日结单 4:处方 5:预付押金单 6:退货单 7:点菜单 8:发票单 [选填]
//     */
//    private String billType;
//
//    /**
//     * 账单修改方式 0:默认值 1:收银员重打 2:人工修改金额 3:自动重解析
//     */
//    private String modifyType = "0";
//
//    /**
//     * 账单来源 1:设备采集 2:人工补录3、解析服务 4.第三方
//     */
//    private String billSource = "1";
//
//    /**
//     * 服务端解析路径[选填，用于服务端解析分析问题]
//     */
//    private String analyzPath;
//
//    /**
//     * 刷卡，钱包等匹配账单的key,格式BILL_MAC_金额_截获时间
//     */
//    private String billMatchKey;
//
//    /**
//     * 数据匹配支付结果等成功后,此字段保存支付结果等的主键 [选填] 匹配成功后为必填,理论上应该保证此值为全局唯一
//     */
//    private String matchId;
//
//    /**
//     * 默认销售时间;
//     */
//    private String defaultSaleTime;
//
//    /**
//     * 客户名称[截获，选填]
//     */
//    private String customerName;
//    /**
//     * 会员编号[截获，选填]
//     */
//    private String membershipId;
//    /**
//     * 会员级别[截获，选填]
//     */
//    private String memberLevels;
//    /**
//     * 宠物名称[截获，选填]
//     */
//    private String petName;
//    /**
//     * 宠物编号[截获，选填]
//     */
//    private String petNumber;
//    /**
//     * 负责人（医生）[截获，选填]
//     */
//    private String principal;
//    /**
//     * 预交押金[截获，选填]
//     */
//    private String deposit;
//    /**
//     * 打印时间yyyyMMddHHmmss[截获，选填，如果截获位数不够，后面补0]
//     */
//    private String printDate;
//    /**
//     * 默认拦截时间
//     */
//    private String defaultInterceptTime;
//    /**
//     * 匹配模型 sk0001:先刷卡后打单,单次刷卡; dd0001:先打单后刷卡,单次刷卡
//     */
//    private String printMatchType;
//
//    /**
//     * 账单合并时使用,唯一
//     */
//    private String billMergeKey;
//    /**
//     * 存重打单的应收金额
//     */
//    private Double modifyAmount;
//    /**
//     * 存重打单的应收金额List
//     */
//    private List<Double> modifyAmountList;
//    /**
//     * 存重打单的应收金额的销售时间List
//     */
//    private List<String> modifyAmountSaleTimeList;
//    /**
//     * 存重打单的应收金额的截获时间List
//     */
//    private List<String> modifyAmountInterceptTimeList;
//    /**
//     * 唯一标识[选填]（目前可用来做积分标识使用）
//     */
//    private String uniqueId;
//    /**
//     * 历史唯一标识，记录合并过程中历次唯一标识，全量，mongodb为Array（目前可用来做积分标识使用）
//     */
//    private List<String> uniqueIdHis;
//    /**
//     * 是否追加二维码 1：是 2：否 [必填]
//     */
//    private String ifqrcode;
//    /**
//     * 优惠券券码
//     */
//    private String couponNum;
//    /**
//     * ERP会员
//     */
//    private String erpMemberCard;
//    /**
//     * 凭证类型 11:水单（商户pos、ERP打印）12:收银凭证（大pos打印） 13:支付凭证（签购单）99：类型未知 [选填，默认写99，表示未知]
//     */
//    private String voucherType;
//    /**
//     * 小票二维码[选填]
//     */
//    private String qrCode;
//    /**
//     * 积分标识 0：本次积分字段没找到或没有配置 1：有本次积分字段[选填]
//     */
//    private String integralmark;
//    /**
//     * 本次积分[选填]
//     */
//    private String thisintegral;
//    /**
//     * 备注[选填]
//     */
//    private String remarks;
//    /**
//     * 账单子类型，庖丁用于配置文件分类
//     */
//    private String templateName;
//    /**
//     * 购货方名称[截获，选填]
//     */
//    private String custName;
//    /**
//     * 购货方税号[截获，选填]
//     */
//    private String custTaxNo;
//    /**
//     * 购货方地址、电话[截获，选填]
//     */
//    private String custAdress;
//    /**
//     * 购货方银行及账号[截获，选填]
//     */
//    private String custBankAccount;
//
//    /**
//     * 第三方订单号(第三方系统唯一)
//     */
//    private String thirdPartyOrderNo;
//    /**
//     * 充值卡消费金额[截获，选填]
//     */
//    private Double rechargeableCardConsumeAmount;
//    /**
//     * 原始充值卡消费金额[截获，选填]
//     */
//    private Double defaultRechargeableCardConsumeAmount;
//    /**
//     * 实付金额[截获，选填]
//     */
//    private Double outOfPocketAmount;
//    /**
//     * 原始实付金额[截获，选填]
//     */
//    private Double defaultOutOfPocketAmount;
//    /**
//     * 充值金额[截获，选填]
//     */
//    private Double rechargeAmount;
//    /**
//     * 原始充值金额[截获，选填]
//     */
//    private Double defaultRechargeAmount;
//    /**
//     * 会员价[截获，选填]
//     */
//    private Double memberPrice;
//    /**
//     * 原始会员价[截获，选填]
//     */
//    private Double defaultMemberPrice;
//    /**
//     * 会员折扣率[截获，选填]
//     */
//    private Double memberDiscountrate;
//    /**
//     * 原始会员折扣率[截获，选填]
//     */
//    private Double defaultMemberDiscountrate;
//    /**
//     * 会员累计消费[截获，选填]
//     */
//    private Double memberTotalConsumption;
//    /**
//     * 原始会员累计消费[截获，选填]
//     */
//    private Double defaultMemberTotalConsumption;
//    /**
//     * 外卖单 1:外卖单 2:非外卖单[截获，选填]
//     */
//    private String takeout;
//
//    /**
//     * 优惠商品详情（支持多项）（截获），{"name":"可乐","price":5.01}
//     */
//    private List<String> discountDetails;
//
//    public String getThirdPartyOrderNo() {
//        return thirdPartyOrderNo;
//    }
//
//    public void setThirdPartyOrderNo(String thirdPartyOrderNo) {
//        this.thirdPartyOrderNo = thirdPartyOrderNo;
//    }
//
//    public String getRemarks() {
//        return remarks;
//    }
//
//    public void setRemarks(String remarks) {
//        this.remarks = remarks;
//    }
//
//    public String getTemplateName() {
//        return templateName;
//    }
//
//    public void setTemplateName(String templateName) {
//        this.templateName = templateName;
//    }
//
//    public String getDefaultPrintDate() {
//        return this.defaultPrintDate;
//    }
//
//    public void setDefaultPrintDate(String defaultPrintDate) {
//        this.defaultPrintDate = defaultPrintDate;
//    }
//
//    public String getDefaultInTime() {
//        return this.defaultInTime;
//    }
//
//    public void setDefaultInTime(String defaultInTime) {
//        this.defaultInTime = defaultInTime;
//    }
//
//    public String getDefaultOutTime() {
//        return this.defaultOutTime;
//    }
//
//    public void setDefaultOutTime(String defaultOutTime) {
//        this.defaultOutTime = defaultOutTime;
//    }
//
//    public String getCouponNum() {
//        return this.couponNum;
//    }
//
//    public void setCouponNum(String couponNum) {
//        this.couponNum = couponNum;
//    }
//
//    public String getErpMemberCard() {
//        return this.erpMemberCard;
//    }
//
//    public void setErpMemberCard(String erpMemberCard) {
//        this.erpMemberCard = erpMemberCard;
//    }
//
//    public String getVoucherType() {
//        return this.voucherType;
//    }
//
//    public void setVoucherType(String voucherType) {
//        this.voucherType = voucherType;
//    }
//
//    public String getAnalyzPath() {
//        return this.analyzPath;
//    }
//
//    public void setAnalyzPath(String analyzPath) {
//        this.analyzPath = analyzPath;
//    }
//
//    public List<Double> getModifyAmountList() {
//        return this.modifyAmountList;
//    }
//
//    public String getUniqueId() {
//        return this.uniqueId;
//    }
//
//    public void setUniqueId(String uniqueId) {
//        this.uniqueId = uniqueId;
//    }
//
//    public String getIfqrcode() {
//        return this.ifqrcode;
//    }
//
//    public void setIfqrcode(String ifqrcode) {
//        this.ifqrcode = ifqrcode;
//    }
//
//    public void setModifyAmountList(List<Double> modifyAmountList) {
//        this.modifyAmountList = modifyAmountList;
//    }
//
//    public Double getModifyAmount() {
//        return this.modifyAmount;
//    }
//
//    public void setModifyAmount(Double modifyAmount) {
//        this.modifyAmount = modifyAmount;
//    }
//
//    public String getDefaultSaleTime() {
//        return this.defaultSaleTime;
//    }
//
//    public void setDefaultSaleTime(String defaultSaleTime) {
//        this.defaultSaleTime = defaultSaleTime;
//    }
//
//    public String getBillMergeKey() {
//        return this.billMergeKey;
//    }
//
//    public void setBillMergeKey(String billMergeKey) {
//        this.billMergeKey = billMergeKey;
//    }
//
//    public String getCustName() {
//        return custName;
//    }
//
//    public void setCustName(String custName) {
//        this.custName = custName;
//    }
//
//    public String getCustTaxNo() {
//        return custTaxNo;
//    }
//
//    public void setCustTaxNo(String custTaxNo) {
//        this.custTaxNo = custTaxNo;
//    }
//
//    public String getCustAdress() {
//        return custAdress;
//    }
//
//    public void setCustAdress(String custAdress) {
//        this.custAdress = custAdress;
//    }
//
//    public String getCustBankAccount() {
//        return custBankAccount;
//    }
//
//    public void setCustBankAccount(String custBankAccount) {
//        this.custBankAccount = custBankAccount;
//    }
//
//    public List<String> getUniqueIdHis() {
//        return uniqueIdHis;
//    }
//
//    public void setUniqueIdHis(List<String> uniqueIdHis) {
//        this.uniqueIdHis = uniqueIdHis;
//    }
//
//    public Double getRechargeableCardConsumeAmount() {
//        return rechargeableCardConsumeAmount;
//    }
//
//    public void setRechargeableCardConsumeAmount(Double rechargeableCardConsumeAmount) {
//        this.rechargeableCardConsumeAmount = rechargeableCardConsumeAmount;
//    }
//
//    public Double getDefaultRechargeableCardConsumeAmount() {
//        return defaultRechargeableCardConsumeAmount;
//    }
//
//    public void setDefaultRechargeableCardConsumeAmount(Double defaultRechargeableCardConsumeAmount) {
//        this.defaultRechargeableCardConsumeAmount = defaultRechargeableCardConsumeAmount;
//    }
//
//    public Double getOutOfPocketAmount() {
//        return outOfPocketAmount;
//    }
//
//    public void setOutOfPocketAmount(Double outOfPocketAmount) {
//        this.outOfPocketAmount = outOfPocketAmount;
//    }
//
//    public Double getDefaultOutOfPocketAmount() {
//        return defaultOutOfPocketAmount;
//    }
//
//    public void setDefaultOutOfPocketAmount(Double defaultOutOfPocketAmount) {
//        this.defaultOutOfPocketAmount = defaultOutOfPocketAmount;
//    }
//
//    public Double getRechargeAmount() {
//        return rechargeAmount;
//    }
//
//    public void setRechargeAmount(Double rechargeAmount) {
//        this.rechargeAmount = rechargeAmount;
//    }
//
//    public Double getDefaultRechargeAmount() {
//        return defaultRechargeAmount;
//    }
//
//    public void setDefaultRechargeAmount(Double defaultRechargeAmount) {
//        this.defaultRechargeAmount = defaultRechargeAmount;
//    }
//
//    public Double getMemberPrice() {
//        return memberPrice;
//    }
//
//    public void setMemberPrice(Double memberPrice) {
//        this.memberPrice = memberPrice;
//    }
//
//    public Double getDefaultMemberPrice() {
//        return defaultMemberPrice;
//    }
//
//    public void setDefaultMemberPrice(Double defaultMemberPrice) {
//        this.defaultMemberPrice = defaultMemberPrice;
//    }
//
//    public Double getMemberDiscountrate() {
//        return memberDiscountrate;
//    }
//
//    public void setMemberDiscountrate(Double memberDiscountrate) {
//        this.memberDiscountrate = memberDiscountrate;
//    }
//
//    public Double getDefaultMemberDiscountrate() {
//        return defaultMemberDiscountrate;
//    }
//
//    public void setDefaultMemberDiscountrate(Double defaultMemberDiscountrate) {
//        this.defaultMemberDiscountrate = defaultMemberDiscountrate;
//    }
//
//    public Double getMemberTotalConsumption() {
//        return memberTotalConsumption;
//    }
//
//    public void setMemberTotalConsumption(Double memberTotalConsumption) {
//        this.memberTotalConsumption = memberTotalConsumption;
//    }
//
//    public Double getDefaultMemberTotalConsumption() {
//        return defaultMemberTotalConsumption;
//    }
//
//    public void setDefaultMemberTotalConsumption(Double defaultMemberTotalConsumption) {
//        this.defaultMemberTotalConsumption = defaultMemberTotalConsumption;
//    }
//
//    public String getTakeout() {
//        return takeout;
//    }
//
//    public void setTakeout(String takeout) {
//        this.takeout = takeout;
//    }
//
//    public List<String> getDiscountDetails() {
//        return discountDetails;
//    }
//
//    public void setDiscountDetails(List<String> discountDetails) {
//        this.discountDetails = discountDetails;
//    }
//
//    public String getId() {
//        return this.id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getShopId() {
//        return this.shopId;
//    }
//
//    public void setShopId(String shopId) {
//        this.shopId = shopId;
//    }
//
//    public String getShopName() {
//        return this.shopName;
//    }
//
//    public void setShopName(String shopName) {
//        this.shopName = shopName;
//    }
//
//    public String getShopEntityId() {
//        return this.shopEntityId;
//    }
//
//    public void setShopEntityId(String shopEntityId) {
//        this.shopEntityId = shopEntityId;
//    }
//
//    public String getShopEntityName() {
//        return this.shopEntityName;
//    }
//
//    public void setShopEntityName(String shopEntityName) {
//        this.shopEntityName = shopEntityName;
//    }
//
//    public String getCreateTime() {
//        return this.createTime;
//    }
//
//    public void setCreateTime(String createTime) {
//        this.createTime = createTime;
//    }
//
//    public String getCTimeStamp() {
//        return this.cTimeStamp;
//    }
//
//    public void setCTimeStamp(String cTimeStamp) {
//        this.cTimeStamp = cTimeStamp;
//    }
//
//    public String getHcTime() {
//        return this.hcTime;
//    }
//
//    public void setHcTime(String hcTime) {
//        this.hcTime = hcTime;
//    }
//
//    public String getHserial() {
//        return this.hserial;
//    }
//
//    public void setHserial(String hserial) {
//        this.hserial = hserial;
//    }
//
//    public String getFixTerminal() {
//        return this.fixTerminal;
//    }
//
//    public void setFixTerminal(String fixTerminal) {
//        this.fixTerminal = fixTerminal;
//    }
//
//    public String getTerminalNumber() {
//        return this.terminalNumber;
//    }
//
//    public void setTerminalNumber(String terminalNumber) {
//        this.terminalNumber = terminalNumber;
//    }
//
//    public String getBillfileName() {
//        return this.billfileName;
//    }
//
//    public void setBillfileName(String billfileName) {
//        this.billfileName = billfileName;
//    }
//
//    public List<String> getBillfileNameHis() {
//        return this.billfileNameHis;
//    }
//
//    public void setBillfileNameHis(List<String> billfileNameHis) {
//        this.billfileNameHis = billfileNameHis;
//    }
//
//    public String getBillNo() {
//        return this.billNo;
//    }
//
//    public void setBillNo(String billNo) {
//        this.billNo = billNo;
//    }
//
//    public String getInterceptTime() {
//        return this.interceptTime;
//    }
//
//    public void setInterceptTime(String interceptTime) {
//        this.interceptTime = interceptTime;
//    }
//
//    public String getShopEntityFullName() {
//        return this.shopEntityFullName;
//    }
//
//    public void setShopEntityFullName(String shopEntityFullName) {
//        this.shopEntityFullName = shopEntityFullName;
//    }
//
//    public String getShopEntityAddress() {
//        return this.shopEntityAddress;
//    }
//
//    public void setShopEntityAddress(String shopEntityAddress) {
//        this.shopEntityAddress = shopEntityAddress;
//    }
//
//    public String getTelephone() {
//        return this.telephone;
//    }
//
//    public void setTelephone(String telephone) {
//        this.telephone = telephone;
//    }
//
//    public String getSaler() {
//        return this.saler;
//    }
//
//    public void setSaler(String saler) {
//        this.saler = saler;
//    }
//
//    public String getCheckstand() {
//        return this.checkstand;
//    }
//
//    public void setCheckstand(String checkstand) {
//        this.checkstand = checkstand;
//    }
//
//    public String getCashier() {
//        return this.cashier;
//    }
//
//    public void setCashier(String cashier) {
//        this.cashier = cashier;
//    }
//
//    public Double getReceivableAmount() {
//        return this.receivableAmount;
//    }
//
//    public void setReceivableAmount(Double receivableAmount) {
//        this.receivableAmount = receivableAmount;
//    }
//
//    public Double getTotalNum() {
//        return this.totalNum;
//    }
//
//    public void setTotalNum(Double totalNum) {
//        this.totalNum = totalNum;
//    }
//
//    public String getBillSerialNumber() {
//        return this.billSerialNumber;
//    }
//
//    public void setBillSerialNumber(String billSerialNumber) {
//        this.billSerialNumber = billSerialNumber;
//    }
//
//    public Double getTotalFee() {
//        return this.totalFee;
//    }
//
//    public void setTotalFee(Double totalFee) {
//        this.totalFee = totalFee;
//    }
//
//    public Double getPaidAmount() {
//        return this.paidAmount;
//    }
//
//    public void setPaidAmount(Double paidAmount) {
//        this.paidAmount = paidAmount;
//    }
//
//    public Double getDiscountAmount() {
//        return this.discountAmount;
//    }
//
//    public void setDiscountAmount(Double discountAmount) {
//        this.discountAmount = discountAmount;
//    }
//
//    public Double getCouponAmount() {
//        return this.couponAmount;
//    }
//
//    public void setCouponAmount(Double couponAmount) {
//        this.couponAmount = couponAmount;
//    }
//
//    public Double getChangeAmount() {
//        return this.changeAmount;
//    }
//
//    public void setChangeAmount(Double changeAmount) {
//        this.changeAmount = changeAmount;
//    }
//
//    public List<String> getSettlementWay() {
//        return this.settlementWay;
//    }
//
//    public void setSettlementWay(List<String> settlementWay) {
//        this.settlementWay = settlementWay;
//    }
//
//    public String getSaleTime() {
//        return saleTime;
//    }
//
//    public void setSaleTime(String saleTime) {
//        this.saleTime = saleTime;
//    }
//
//    public String getMemberCardNumber() {
//        return this.memberCardNumber;
//    }
//
//    public void setMemberCardNumber(String memberCardNumber) {
//        this.memberCardNumber = memberCardNumber;
//    }
//
//    public Double getTotalConsumption() {
//        return this.totalConsumption;
//    }
//
//    public void setTotalConsumption(Double totalConsumption) {
//        this.totalConsumption = totalConsumption;
//    }
//
//    public String getWebsite() {
//        return this.website;
//    }
//
//    public void setWebsite(String website) {
//        this.website = website;
//    }
//
//    public String getBillImage() {
//        return this.billImage;
//    }
//
//    public void setBillImage(String billImage) {
//        this.billImage = billImage;
//    }
//
//    public List<String> getGoodsDetails() {
//        return this.goodsDetails;
//    }
//
//    public void setGoodsDetails(List<String> goodsDetails) {
//        this.goodsDetails = goodsDetails;
//    }
//
//    public String getRoomNo() {
//        return this.roomNo;
//    }
//
//    public void setRoomNo(String roomNo) {
//        this.roomNo = roomNo;
//    }
//
//    public String getCheckinName() {
//        return this.checkinName;
//    }
//
//    public void setCheckinName(String checkinName) {
//        this.checkinName = checkinName;
//    }
//
//    public String getDeskNo() {
//        return this.deskNo;
//    }
//
//    public void setDeskNo(String deskNo) {
//        this.deskNo = deskNo;
//    }
//
//    public Double getConsumeNum() {
//        return this.consumeNum;
//    }
//
//    public void setConsumeNum(Double consumeNum) {
//        this.consumeNum = consumeNum;
//    }
//
//    public String getBillText() {
//        return this.billText;
//    }
//
//    public void setBillText(String billText) {
//        this.billText = billText;
//    }
//
//    public Map<String, String> getCustomRecord() {
//        return this.customRecord;
//    }
//
//    public void setCustomRecord(Map<String, String> customRecord) {
//        this.customRecord = customRecord;
//    }
//
//    public String getBillType() {
//        return this.billType;
//    }
//
//    public void setBillType(String billType) {
//        this.billType = billType;
//    }
//
//    public String getInTime() {
//        return this.inTime;
//    }
//
//    public String getOutTime() {
//        return this.outTime;
//    }
//
//    public void setInTime(String inTime) {
//        this.inTime = inTime;
//    }
//
//    public void setOutTime(String outTime) {
//        this.outTime = outTime;
//    }
//
//    public String getBillMatchKey() {
//        return this.billMatchKey;
//    }
//
//    public void setBillMatchKey(String billMatchKey) {
//        this.billMatchKey = billMatchKey;
//    }
//
//    public String getMatchId() {
//        return this.matchId;
//    }
//
//    public void setMatchId(String matchId) {
//        this.matchId = matchId;
//    }
//
//    public String getPrintMatchType() {
//        return this.printMatchType;
//    }
//
//    public void setPrintMatchType(String printMatchType) {
//        this.printMatchType = printMatchType;
//    }
//
//    public String getTsuuid() {
//        return this.tsuuid;
//    }
//
//    public void setTsuuid(String tsuuid) {
//        this.tsuuid = tsuuid;
//    }
//
//    public String getUploadType() {
//        return this.uploadType;
//    }
//
//    public void setUploadType(String uploadType) {
//        this.uploadType = uploadType;
//    }
//
//    public String getCustomerName() {
//        return this.customerName;
//    }
//
//    public void setCustomerName(String customerName) {
//        this.customerName = customerName;
//    }
//
//    public String getMembershipId() {
//        return this.membershipId;
//    }
//
//    public void setMembershipId(String membershipId) {
//        this.membershipId = membershipId;
//    }
//
//    public String getMemberLevels() {
//        return this.memberLevels;
//    }
//
//    public void setMemberLevels(String memberLevels) {
//        this.memberLevels = memberLevels;
//    }
//
//    public String getPetName() {
//        return this.petName;
//    }
//
//    public void setPetName(String petName) {
//        this.petName = petName;
//    }
//
//    public String getPetNumber() {
//        return this.petNumber;
//    }
//
//    public void setPetNumber(String petNumber) {
//        this.petNumber = petNumber;
//    }
//
//    public String getPrincipal() {
//        return this.principal;
//    }
//
//    public void setPrincipal(String principal) {
//        this.principal = principal;
//    }
//
//    public String getDeposit() {
//        return this.deposit;
//    }
//
//    public void setDeposit(String deposit) {
//        this.deposit = deposit;
//    }
//
//    public String getPrintDate() {
//        return this.printDate;
//    }
//
//    public void setPrintDate(String printDate) {
//        this.printDate = printDate;
//    }
//
//    public String getDefaultInterceptTime() {
//        return this.defaultInterceptTime;
//    }
//
//    public void setDefaultInterceptTime(String defaultInterceptTime) {
//        this.defaultInterceptTime = defaultInterceptTime;
//    }
//
//    public String getModifyType() {
//        return this.modifyType;
//    }
//
//    public void setModifyType(String modifyType) {
//        this.modifyType = modifyType;
//    }
//
//    public String getBillSource() {
//        return this.billSource;
//    }
//
//    public void setBillSource(String billSource) {
//        this.billSource = billSource;
//    }
//
//    public String getQrCode() {
//        return qrCode;
//    }
//
//    public void setQrCode(String qrCode) {
//        this.qrCode = qrCode;
//    }
//
//    public String getIntegralmark() {
//        return integralmark;
//    }
//
//    public void setIntegralmark(String integralmark) {
//        this.integralmark = integralmark;
//    }
//
//    public String getThisintegral() {
//        return thisintegral;
//    }
//
//    public void setThisintegral(String thisintegral) {
//        this.thisintegral = thisintegral;
//    }
//
//    public List<String> getModifyAmountSaleTimeList() {
//        return modifyAmountSaleTimeList;
//    }
//
//    public void setModifyAmountSaleTimeList(List<String> modifyAmountSaleTimeList) {
//        this.modifyAmountSaleTimeList = modifyAmountSaleTimeList;
//    }
//
//    public List<String> getModifyAmountInterceptTimeList() {
//        return modifyAmountInterceptTimeList;
//    }
//
//    public void setModifyAmountInterceptTimeList(List<String> modifyAmountInterceptTimeList) {
//        this.modifyAmountInterceptTimeList = modifyAmountInterceptTimeList;
//    }
//
//    public Double getDefaultReceivableAmount() {
//        return defaultReceivableAmount;
//    }
//
//    public void setDefaultReceivableAmount(Double defaultReceivableAmount) {
//        this.defaultReceivableAmount = defaultReceivableAmount;
//    }
//
//    public Double getDefaultTotalNum() {
//        return defaultTotalNum;
//    }
//
//    public void setDefaultTotalNum(Double defaultTotalNum) {
//        this.defaultTotalNum = defaultTotalNum;
//    }
//
//    public Double getDefaultTotalFee() {
//        return defaultTotalFee;
//    }
//
//    public void setDefaultTotalFee(Double defaultTotalFee) {
//        this.defaultTotalFee = defaultTotalFee;
//    }
//
//    public Double getDefaultPaidAmount() {
//        return defaultPaidAmount;
//    }
//
//    public void setDefaultPaidAmount(Double defaultPaidAmount) {
//        this.defaultPaidAmount = defaultPaidAmount;
//    }
//
//    public Double getDefaultDiscountAmount() {
//        return defaultDiscountAmount;
//    }
//
//    public void setDefaultDiscountAmount(Double defaultDiscountAmount) {
//        this.defaultDiscountAmount = defaultDiscountAmount;
//    }
//
//    public Double getDefaultCouponAmount() {
//        return defaultCouponAmount;
//    }
//
//    public void setDefaultCouponAmount(Double defaultCouponAmount) {
//        this.defaultCouponAmount = defaultCouponAmount;
//    }
//
//    public Double getDefaultChangeAmount() {
//        return defaultChangeAmount;
//    }
//
//    public void setDefaultChangeAmount(Double defaultChangeAmount) {
//        this.defaultChangeAmount = defaultChangeAmount;
//    }
//
//    public Double getDefaultTotalConsumption() {
//        return defaultTotalConsumption;
//    }
//
//    public void setDefaultTotalConsumption(Double defaultTotalConsumption) {
//        this.defaultTotalConsumption = defaultTotalConsumption;
//    }
//
//    public Double getDefaultConsumeNum() {
//        return defaultConsumeNum;
//    }
//
//    public void setDefaultConsumeNum(Double defaultConsumeNum) {
//        this.defaultConsumeNum = defaultConsumeNum;
//    }
//
//    public String getRowKey() {
//        return rowKey;
//    }
//
//    public void setRowKey(String rowKey) {
//        this.rowKey = rowKey;
//    }
//
//    @Override
//    public String toString() {
//        return id + "\t" + rowKey + "\t" + shopId + "\t" + shopName + "\t" + shopEntityId + "\t" + shopEntityName + "\t"
//                + createTime + "\t" + cTimeStamp + "\t" + hcTime + "\t" + hserial + "\t" + fixTerminal + "\t"
//                + terminalNumber + "\t" + billfileName + "\t" + tsuuid + "\t" + billfileNameHis + "\t" + billNo + "\t"
//                + interceptTime + "\t" + shopEntityFullName + "\t" + shopEntityAddress + "\t" + telephone + "\t" + saler
//                + "\t" + checkstand + "\t" + cashier + "\t" + receivableAmount + "\t" + defaultReceivableAmount + "\t"
//                + totalNum + "\t" + defaultTotalNum + "\t" + billSerialNumber + "\t" + totalFee + "\t" + defaultTotalFee
//                + "\t" + paidAmount + "\t" + defaultPaidAmount + "\t" + discountAmount + "\t" + defaultDiscountAmount
//                + "\t" + couponAmount + "\t" + defaultCouponAmount + "\t" + changeAmount + "\t" + defaultChangeAmount
//                + "\t" + settlementWay + "\t" + saleTime + "\t" + memberCardNumber + "\t" + totalConsumption + "\t"
//                + defaultTotalConsumption + "\t" + website + "\t" + billImage + "\t" + goodsDetails + "\t" + roomNo
//                + "\t" + checkinName + "\t" + deskNo + "\t" + consumeNum + "\t" + defaultConsumeNum + "\t" + billText
//                + "\t" + inTime + "\t" + outTime + "\t" + defaultPrintDate + "\t" + defaultInTime + "\t"
//                + defaultOutTime + "\t" + uploadType + "\t" + customRecord + "\t" + billType + "\t" + modifyType + "\t"
//                + billSource + "\t" + analyzPath + "\t" + billMatchKey + "\t" + matchId + "\t" + defaultSaleTime + "\t"
//                + customerName + "\t" + membershipId + "\t" + memberLevels + "\t" + petName + "\t" + petNumber + "\t"
//                + principal + "\t" + deposit + "\t" + printDate + "\t" + defaultInterceptTime + "\t" + printMatchType
//                + "\t" + billMergeKey + "\t" + modifyAmount + "\t" + modifyAmountList + "\t" + modifyAmountSaleTimeList
//                + "\t" + modifyAmountInterceptTimeList + "\t" + uniqueId + "\t" + uniqueIdHis + "\t" + ifqrcode + "\t"
//                + couponNum + "\t" + erpMemberCard + "\t" + voucherType + "\t" + qrCode + "\t" + integralmark + "\t"
//                + thisintegral + "\t" + remarks + "\t" + templateName + "\t" + custName + "\t" + custTaxNo + "\t"
//                + custAdress + "\t" + custBankAccount + "\t" + thirdPartyOrderNo + "\t" + rechargeableCardConsumeAmount
//                + "\t" + defaultRechargeableCardConsumeAmount + "\t" + outOfPocketAmount + "\t"
//                + defaultOutOfPocketAmount + "\t" + rechargeAmount + "\t" + defaultRechargeAmount + "\t" + memberPrice
//                + "\t" + defaultMemberPrice + "\t" + memberDiscountrate + "\t" + defaultMemberDiscountrate + "\t"
//                + memberTotalConsumption + "\t" + defaultMemberTotalConsumption + "\t" + takeout + "\t"
//                + discountDetails;
//    }
//
//    public static BillInfoFmt cloneBill(BillInfo fromBean) {
//        if (null == fromBean) {
//            return null;
//        }
//        BillInfoFmt toBean = new BillInfoFmt();
//        toBean.setId(stringRemove(fromBean.getId()));
//        toBean.setRowKey(stringRemove(fromBean.getRowKey()));
//        toBean.setShopId(stringRemove(fromBean.getShopId()));
//        toBean.setShopName(stringRemove(fromBean.getShopName()));
//        toBean.setShopEntityId(stringRemove(fromBean.getShopEntityId()));
//        toBean.setShopEntityName(stringRemove(fromBean.getShopEntityName()));
//        toBean.setCreateTime(DateTimeUtils.getYmdhmsForNo(fromBean.getCreateTime()));
//        toBean.setCTimeStamp(DateTimeUtils.getYmdhmsForNo(fromBean.getCTimeStamp()));
//        toBean.setHcTime(stringRemove(fromBean.getHcTime()));
//        toBean.setHserial(stringRemove(fromBean.getHserial()));
//        toBean.setFixTerminal(stringRemove(fromBean.getFixTerminal()));
//        toBean.setTerminalNumber(stringRemove(fromBean.getTerminalNumber()));
//        toBean.setBillfileName(stringRemove(fromBean.getBillfileName()));
//        toBean.setTsuuid(stringRemove(fromBean.getTsuuid()));
//        toBean.setBillfileNameHis(fromBean.getBillfileNameHis());
//        toBean.setBillNo(stringRemove(fromBean.getBillNo()));
//        toBean.setInterceptTime(DateTimeUtils.getYmdhmsForNo(fromBean.getInterceptTime()));
//        toBean.setShopEntityFullName(stringRemove(fromBean.getShopEntityFullName()));
//        toBean.setShopEntityAddress(stringRemove(fromBean.getShopEntityAddress()));
//        toBean.setTelephone(stringRemove(fromBean.getTelephone()));
//        toBean.setSaler(stringRemove(fromBean.getSaler()));
//        toBean.setCheckstand(stringRemove(fromBean.getCheckstand()));
//        toBean.setCashier(stringRemove(fromBean.getCashier()));
//        toBean.setReceivableAmount(fromBean.getReceivableAmount());
//        toBean.setDefaultReceivableAmount(fromBean.getDefaultReceivableAmount());
//        toBean.setTotalNum(fromBean.getTotalNum());
//        toBean.setDefaultTotalNum(fromBean.getDefaultTotalNum());
//        toBean.setBillSerialNumber(stringRemove(fromBean.getBillSerialNumber()));
//        toBean.setTotalFee(fromBean.getTotalFee());
//        toBean.setDefaultTotalFee(fromBean.getDefaultTotalFee());
//        toBean.setPaidAmount(fromBean.getPaidAmount());
//        toBean.setDefaultPaidAmount(fromBean.getDefaultPaidAmount());
//        toBean.setDiscountAmount(fromBean.getDiscountAmount());
//        toBean.setDefaultDiscountAmount(fromBean.getDefaultDiscountAmount());
//        toBean.setCouponAmount(fromBean.getCouponAmount());
//        toBean.setDefaultCouponAmount(fromBean.getDefaultCouponAmount());
//        toBean.setChangeAmount(fromBean.getChangeAmount());
//        toBean.setDefaultChangeAmount(fromBean.getDefaultChangeAmount());
//        if (null != fromBean.getSettlementWay()) {
//            List<SettlementWayInfo> rawList = fromBean.getSettlementWay();
//            List<String> list = new ArrayList<>();
//            for (SettlementWayInfo raw : rawList) {
//                list.add(raw.toString());
//            }
//            toBean.setSettlementWay(list);
//        }
//        toBean.setSaleTime(fromBean.getSaleTime());
//        toBean.setMemberCardNumber(stringRemove(fromBean.getMemberCardNumber()));
//        toBean.setTotalConsumption(fromBean.getTotalConsumption());
//        toBean.setDefaultTotalConsumption(fromBean.getDefaultTotalConsumption());
//        toBean.setWebsite(stringRemove(fromBean.getWebsite()));
//        toBean.setBillImage(stringRemove(fromBean.getBillImage()));
//        if (null != fromBean.getGoodsDetails()) {
//            List<GoodsDetailInfo> rawList = fromBean.getGoodsDetails();
//            List<String> list = new ArrayList<>();
//            for (GoodsDetailInfo raw : rawList) {
//                list.add(raw.toString());
//            }
//            toBean.setGoodsDetails(list);
//        }
//        toBean.setRoomNo(stringRemove(fromBean.getRoomNo()));
//        toBean.setCheckinName(stringRemove(fromBean.getCheckinName()));
//        toBean.setDeskNo(stringRemove(fromBean.getDeskNo()));
//        toBean.setConsumeNum(fromBean.getConsumeNum());
//        toBean.setDefaultConsumeNum(fromBean.getDefaultConsumeNum());
//        toBean.setBillText(stringRemove(fromBean.getBillText()));
//        toBean.setInTime(DateTimeUtils.getYmdhmsForNo(fromBean.getInTime()));
//        toBean.setOutTime(DateTimeUtils.getYmdhmsForNo(fromBean.getOutTime()));
//        toBean.setDefaultPrintDate(DateTimeUtils.getYmdhmsForNo(fromBean.getDefaultPrintDate()));
//        toBean.setDefaultInTime(DateTimeUtils.getYmdhmsForNo(fromBean.getDefaultInTime()));
//        toBean.setDefaultOutTime(DateTimeUtils.getYmdhmsForNo(fromBean.getDefaultOutTime()));
//        toBean.setUploadType(stringRemove(fromBean.getUploadType()));
//        toBean.setCustomRecord(fromBean.getCustomRecord());
//        toBean.setBillType(stringRemove(fromBean.getBillType()));
//        toBean.setModifyType(stringRemove(fromBean.getModifyType()));
//        toBean.setBillSource(stringRemove(fromBean.getBillSource()));
//        toBean.setAnalyzPath(stringRemove(fromBean.getAnalyzPath()));
//        toBean.setBillMatchKey(stringRemove(fromBean.getBillMatchKey()));
//        toBean.setMatchId(stringRemove(fromBean.getMatchId()));
//        toBean.setDefaultSaleTime(DateTimeUtils.getYmdhmsForNo(fromBean.getDefaultSaleTime()));
//        toBean.setCustomerName(stringRemove(fromBean.getCustomerName()));
//        toBean.setMembershipId(stringRemove(fromBean.getMembershipId()));
//        toBean.setMemberLevels(stringRemove(fromBean.getMemberLevels()));
//        toBean.setPetName(stringRemove(fromBean.getPetName()));
//        toBean.setPetNumber(stringRemove(fromBean.getPetNumber()));
//        toBean.setPrincipal(stringRemove(fromBean.getPrincipal()));
//        toBean.setDeposit(stringRemove(fromBean.getDeposit()));
//        toBean.setPrintDate(DateTimeUtils.getYmdhmsForNo(fromBean.getPrintDate()));
//        toBean.setDefaultInterceptTime(DateTimeUtils.getYmdhmsForNo(fromBean.getDefaultInterceptTime()));
//        toBean.setPrintMatchType(stringRemove(fromBean.getPrintMatchType()));
//        toBean.setBillMergeKey(stringRemove(fromBean.getBillMergeKey()));
//        toBean.setModifyAmount(fromBean.getModifyAmount());
//        toBean.setModifyAmountList(fromBean.getModifyAmountList());
//        toBean.setModifyAmountSaleTimeList(DateTimeUtils.getYmdhmsForNo(fromBean.getModifyAmountSaleTimeList()));
//        toBean.setModifyAmountInterceptTimeList(
//                DateTimeUtils.getYmdhmsForNo(fromBean.getModifyAmountInterceptTimeList()));
//        toBean.setUniqueId(stringRemove(fromBean.getUniqueId()));
//        toBean.setUniqueIdHis(fromBean.getUniqueIdHis());
//        toBean.setIfqrcode(stringRemove(fromBean.getIfqrcode()));
//        toBean.setCouponNum(stringRemove(fromBean.getCouponNum()));
//        toBean.setErpMemberCard(stringRemove(fromBean.getErpMemberCard()));
//        toBean.setVoucherType(stringRemove(fromBean.getVoucherType()));
//        toBean.setQrCode(stringRemove(fromBean.getQrCode()));
//        toBean.setIntegralmark(stringRemove(fromBean.getIntegralmark()));
//        toBean.setThisintegral(stringRemove(fromBean.getThisintegral()));
//        toBean.setRemarks(stringRemove(fromBean.getRemarks()));
//        toBean.setTemplateName(stringRemove(fromBean.getTemplateName()));
//        toBean.setCustName(stringRemove(fromBean.getCustName()));
//        toBean.setCustName(stringRemove(fromBean.getCustTaxNo()));
//        toBean.setCustAdress(stringRemove(fromBean.getCustAdress()));
//        toBean.setCustBankAccount(stringRemove(fromBean.getCustBankAccount()));
//        toBean.setThirdPartyOrderNo(stringRemove(fromBean.getThirdPartyOrderNo()));
//        toBean.setRechargeableCardConsumeAmount(fromBean.getRechargeableCardConsumeAmount());
//        toBean.setDefaultRechargeableCardConsumeAmount(fromBean.getDefaultRechargeableCardConsumeAmount());
//        toBean.setOutOfPocketAmount(fromBean.getOutOfPocketAmount());
//        toBean.setDefaultOutOfPocketAmount(fromBean.getDefaultOutOfPocketAmount());
//        toBean.setRechargeAmount(fromBean.getRechargeAmount());
//        toBean.setDefaultRechargeAmount(fromBean.getDefaultRechargeAmount());
//        toBean.setMemberPrice(fromBean.getMemberPrice());
//        toBean.setDefaultMemberPrice(fromBean.getDefaultMemberPrice());
//        toBean.setMemberDiscountrate(fromBean.getMemberDiscountrate());
//        toBean.setDefaultMemberDiscountrate(fromBean.getDefaultMemberDiscountrate());
//        toBean.setMemberTotalConsumption(fromBean.getMemberTotalConsumption());
//        toBean.setDefaultMemberTotalConsumption(fromBean.getDefaultMemberTotalConsumption());
//        toBean.setTakeout(stringRemove(fromBean.getTakeout()));
//        if (null != fromBean.getDiscountDetails()) {
//            List<DiscountDetailsInfo> rawList = fromBean.getDiscountDetails();
//            List<String> list = new ArrayList<>();
//            for (DiscountDetailsInfo raw : rawList) {
//                list.add(raw.toString());
//            }
//            toBean.setDiscountDetails(list);
//        }
//        return toBean;
//    }
//
//    /**
//     * 原始数据格式化处理，待完善
//     *
//     * @param jsonStr
//     * @return
//     */
//    private static String stringRemove(String strVal) {
//        if (StringUtils.isEmpty(strVal)) {
//            return "";
//        }
//        strVal = strVal.replace("\t", "");
//        strVal = strVal.replace("\n", "");
//        strVal = strVal.replace("\r", "");
//        return strVal;
//    }
//}
