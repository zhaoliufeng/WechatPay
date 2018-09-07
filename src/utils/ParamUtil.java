package utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 获取参数工具类
 */
public class ParamUtil {

    private static final String TAG = ParamUtil.class.getSimpleName();

    //获取用户 openId 参数
    private final static String APPID = "appid";
    private final static String MCHID = "mch_id";
    private final static String NONCESTR = "nonce_str";
    private final static String BODY = "body";
    private final static String OUTTRADENO = "out_trade_no";
    private final static String FEE = "total_fee";
    private final static String IP = "spbill_create_ip";
    private final static String NOTIFYURL = "notify_url";
    private final static String TRADETYPE = "trade_type";
    private final static String OPENID = "openid";

    private final static String PAYSIGN_APPID = "appId";
    private final static String PAYSIGN_TIMESTAMP = "timeStamp";
    private final static String PAYSIGN_PACKAGE = "package";
    private final static String PAYSIGN_SIGNTYPE = "signType";
    private final static String PAYSIGN_NONCESTR = "nonceStr";

    private final static String SIGN = "sign";

    private HttpServletRequest request;

    private ParamUtil() {
    }

    private static ParamUtil instance;

    public static ParamUtil getInstance() {
        if (instance == null) {
            synchronized (ParamUtil.class) {
                if (instance == null) {
                    instance = new ParamUtil();
                }
            }
        }
        return instance;
    }

    public ParamUtil request(HttpServletRequest request) {
        this.request = request;
        return instance;
    }


    /**
     * 设置统一下单接口参数集合
     * @param map 参数集合引用
     */
    public void setPrepayParams(Map<String, String> map) {
        map.clear();
        map.put(BODY, getStringParams(BODY));
        map.put(FEE, getStringParams(FEE));
        map.put(OPENID, getStringParams(OPENID));

        map.put(APPID, OrderUtils.APPID);
        map.put(MCHID, OrderUtils.MCHID);
        map.put(NONCESTR, OrderUtils.getRandomString());
        map.put(OUTTRADENO, OrderUtils.getOrderNum());
        map.put(IP, getIP());
        map.put(NOTIFYURL, NetUtils.NOTIFYURL);
        map.put(TRADETYPE, "JSAPI");
        Log.info(TAG, "统一下单接口参数");
        for (String key : map.keySet()) {
            Log.info(TAG, key + " : " + map.get(key));
        }
        //填入所有待发送参数之后 开始生成签名
        map.put(SIGN, OrderUtils.getMD5Sign());
    }

    /**
     * 设置二次签名参数
     * 需要参与签名的参数
     * appId timeStamp nonceStr package signType
     */
    public void setPaySignParams(Map<String, String> map, String prepayId, String timStamp, String nonceStr){
        map.clear();
        map.put(PAYSIGN_APPID, OrderUtils.APPID);
        map.put(PAYSIGN_TIMESTAMP, timStamp);
        map.put(PAYSIGN_NONCESTR, nonceStr);
        map.put(PAYSIGN_PACKAGE, "prepay_id=" + prepayId);
        map.put(PAYSIGN_SIGNTYPE, "MD5");
    }

    public String getPaysign(Map<String, String> map, String prepayId, String timStamp, String nonceStr){
        setPaySignParams(map, prepayId, timStamp, nonceStr);
        return OrderUtils.getMD5Sign();
    }
    //获取当前请求的ip地址
    private String getIP(){
        if (request != null)
            return request.getHeader("X-Real-IP");
        return "0:0:0:0";
    }

    private String getStringParams(String key) {
        if (request != null)
            return request.getParameter(key);
        throw new RuntimeException("Param Util is null");
    }
}
