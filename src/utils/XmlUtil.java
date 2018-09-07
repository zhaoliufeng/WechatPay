package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtil {

    private static final String TAG = XmlUtil.class.getSimpleName();

    private XmlUtil() {
    }

    private static XmlUtil instance;

    private String xml;

    //prepay_id
    private String PREPAYID = "prepay_id";

    private String APPID = "appid";

    private String MCHID = "mch_id";

    private String NONCESTR = "nonce_str";

    private String SIGN = "sign";

    private String TRADETYPE = "trade_type";

    //返回结果
    private String RETURNMSG = "return_msg";

    private String RESULTCODE = "result_code";

    private String RETURNCODE = "return_code";

    public static XmlUtil getInstance() {
        if (instance == null) {
            synchronized (XmlUtil.class) {
                if (instance == null) {
                    instance = new XmlUtil();
                }
            }
        }
        return instance;
    }

    public PrepayBean getPrepayBean(String xml) {
        this.xml = xml;
        PrepayBean prepayBean = new PrepayBean();
        Element root = doXMLParse();
        String returnCode = root.element(RETURNCODE).getText();
        prepayBean.setReturnCode(returnCode);
        if (prepayBean.getReturnCode().equals("SUCCESS")) {
            Log.info(TAG, "统一下单请求成功");
            prepayBean.setAppId(root.element(APPID).getText());
            prepayBean.setPrepayId(root.element(PREPAYID).getText());
            prepayBean.setMchId(root.element(MCHID).getText());
            //生成随机字符串
            prepayBean.setNonceStr(OrderUtils.getRandomString());
            //获取当前秒级时间戳
            prepayBean.setTimeStamp(OrderUtils.getTimeStamp());
            prepayBean.setTradeType("MD5");
            prepayBean.setResultCode(root.element(RESULTCODE).getText());
            prepayBean.setReturnMsg(root.element(RETURNMSG).getText());
            prepayBean.setSign(
                    ParamUtil.getInstance().getPaysign(
                            OrderUtils.paramKV,
                            prepayBean.getPrepayId(),
                            prepayBean.getTimeStamp(),
                            prepayBean.getNonceStr()));
        } else {
            prepayBean.setReturnMsg(root.element(RETURNMSG).getText());
            Log.info(TAG, "统一下单请求失败 原因 --> " + prepayBean.getReturnMsg());
        }
        return prepayBean;
    }

    private Element doXMLParse() {
        Element root = null;
        try {
            Document myDoc = DocumentHelper.parseText(xml);
            root = myDoc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return root;
    }

}
