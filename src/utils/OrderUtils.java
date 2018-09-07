package utils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微信支付订单相关工具类
 */
public class OrderUtils {

    private final static String TAG = OrderUtils.class.getSimpleName();

    private final static int RANDOM_LENGTH = 32;

    /**
     * 获取32位内的随机字符串 nonce_str
     */
    public static String getRandomString() {
        String seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";
        StringBuilder sb = new StringBuilder();
        for (int count = 0; count < RANDOM_LENGTH; count++) {
            int randomIndex = (int) (Math.random() * seed.length());
            sb.append(seed.charAt(randomIndex));
        }
        Log.info(TAG, "随机字符串 --> " + sb.toString());
        return sb.toString();
    }

    /**
     * 获取订单号 根据当前时间戳生成 避免重复 out_trade_no
     * 订单号由 当前年月日时分秒 「 20180809120521 」+ 随机数 （0 - 100000）
     */
    public static String getOrderNum() {
        String randomNum = String.format("%05d", (int) (Math.random() * 10000));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String orderNum = String.format("%s%s",
                dateFormat.format(new Date(System.currentTimeMillis())),
                randomNum);
        Log.info(TAG, "订单号 --> " + orderNum);
        return orderNum;
    }

    //按 key 的首字母的 ASCII 排序
    public static Map<String, String> paramKV = new TreeMap<>(String::compareTo);

    /**
     * 获取签名
     */
    public static String getMD5Sign() {
        //对参数按照 key=value 的格式，并按 key 的 ASCII 字典排序组合成字符串
        String kvString = mapToKeyValue();
        //拼接API密钥
        String signTemp = kvString + "&key=" + Config.SECRET_KEY;
        Log.info(TAG, "MD5加密前 源字符串 --> " + signTemp);
        String sign = MD5(signTemp);
        Log.info(TAG, "MD5签名 --> " + sign);
        return sign;
    }

    private static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将 map 转换成 key=value 格式 并以 & 连接
     */
    private static String   mapToKeyValue() {
        StringBuilder sb = new StringBuilder();
        for (String key : paramKV.keySet()) {
            sb.append(key).append("=").append(paramKV.get(key));
            //添加连接符
            sb.append("&");
        }
        //去除末尾多余的连接符
        sb.deleteCharAt(sb.length() - 1);
        Log.info(TAG, "kv字符串 --> " + sb.toString());
        return sb.toString();
    }

    public static String getPrepayParams(){
        return mapToXml();
    }

    /**
     * 将 map 转换成 xml 数据
     */
    private static String mapToXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (String key : paramKV.keySet()) {
            sb.append("<").append(key).append(">")
                    .append(paramKV.get(key))
                    .append("</").append(key).append(">");
        }
        sb.append("</xml>");
        Log.info(TAG, "生成的xml数据 --> " + sb.toString());
        return sb.toString();
    }

    /**
     * 获取订单起始时间和结束时间 订单有效时间定为 5 min （5 * 60)s
     *
     * @return 订单时间数组 下标为 0 代表「起始时间」 下标为 1 「代表结束时间」
     */
    public static String[] getOrderTime() {
        String orderTime[] = new String[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        orderTime[0] = dateFormat.format(new Date(System.currentTimeMillis()));
        orderTime[1] = dateFormat.format(new Date(System.currentTimeMillis() + (5 * 60 * 1000)));
        Log.info(TAG, "订单起始时间 --> " + orderTime[0] + " 订单结束时间 --> " + orderTime[1]);
        return orderTime;
    }

    /**
     * 获取当前时间戳 精确到秒
     * @return 秒级时间戳字符串
     */
    static String getTimeStamp(){
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static void main(String[] arg) {
//        paramKV.put("body", "微信支付测试");
//        paramKV.put("appid", "ASJIQBBHA1231");
//        paramKV.put("zhao", "test");
//        paramKV.put("mch_id", "1000000");
//        paramKV.put("sign", getMD5Sign());
//        mapToXml();
        String xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<return_msg><![CDATA[OK]]></return_msg>\n" +
                "<appid><![CDATA[wx84bf6ecf29b6b796]]></appid>\n" +
                "<mch_id><![CDATA[1226570502]]></mch_id>\n" +
                "<nonce_str><![CDATA[O2lr4Q7SV1UIybzb]]></nonce_str>\n" +
                "<sign><![CDATA[8FCCDE5B800BFDE72C6576A9315B4443]]></sign>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<prepay_id><![CDATA[wx10100517569104d23738a3572976618664]]></prepay_id>\n" +
                "<trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "</xml>";
    }
}
