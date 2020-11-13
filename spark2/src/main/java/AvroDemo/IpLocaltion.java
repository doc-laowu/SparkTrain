package AvroDemo;

/**
 * @Title: IpLocaltion
 * @ProjectName structured_streaming_etl_service_interaction
 * @Description: TODO 加载ip位置信息库的pojo类
 * @Author yisheng.wu
 * @Date 2020/4/3016:04
 */
public class IpLocaltion {

    public IpLocaltion() {
    }

    public IpLocaltion(String startIp, String endIp, String country, String province, String city, String isp) {
        this.startIp = startIp;
        this.endIp = endIp;
        this.country = country;
        this.province = province;
        this.city = city;
        this.isp = isp;
    }

    private String startIp;

    private String endIp;

    private String country;

    private String province;

    private String city;

    private String isp;

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
