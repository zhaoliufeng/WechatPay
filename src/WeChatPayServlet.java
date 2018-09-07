import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 统一下单接口 获取prepay_id
 * 接口需要以下参数 ：
 * appid  「小程序 ID」
 * body   「商品描述」
 * openId 「临时登录凭证 小程序调用login 返回的code」
 * fee    「总金额」
 */
public class WeChatPayServlet extends HttpServlet {

    private final String TAG = "WeChatPayServlet";

    /**
     * var bodyData = '<xml>';
     * bodyData += '<appid>' + wxConfig.AppID + '</appid>';  // 小程序ID
     * bodyData += '<body>' + body + '</body>'; // 商品描述
     * bodyData += '<mch_id>' + wxConfig.Mch_id + '</mch_id>'; // 商户号
     * bodyData += '<nonce_str>' + nonce_str + '</nonce_str>'; // 随机字符串
     * bodyData += '<notify_url>' + notify_url + '</notify_url>'; // 支付成功的回调地址
     * bodyData += '<openid>' + openid + '</openid>'; // 用户标识
     * bodyData += '<out_trade_no>' + out_trade_no + '</out_trade_no>'; // 商户订单号
     * bodyData += '<spbill_create_ip>' + spbill_create_ip + '</spbill_create_ip>'; // 终端IP
     * bodyData += '<total_fee>' + total_fee + '</total_fee>'; // 总金额 单位为分
     * bodyData += '<trade_type>JSAPI</trade_type>'; // 交易类型 小程序取值如下：JSAPI
     */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        //获取客户端IP
        String ipAddresses = req.getHeader("X-Real-IP");
        Log.info(TAG, "来自 ip --> " + ipAddresses + " 的访问");
        ParamUtil.getInstance().request(req).setPrepayParams(OrderUtils.paramKV);
        String requestParams = OrderUtils.getPrepayParams();
        String backXml = NetUtils.getRemotePortData(NetUtils.PREPAY_API, requestParams);
        Log.info(TAG, "收到统一下单接口返回的数据 --> " + backXml);
        PrepayBean prepayBean = XmlUtil.getInstance().getPrepayBean(backXml);

        //输出当前获取的prepayId
        JSONObject jsonObject = JSONObject.fromObject(prepayBean);
        PrintWriter out = resp.getWriter();
        Log.info(TAG, "输出prepayId : " + jsonObject.toString());
        //输出后需要移除参数 map 中的所有信息
        OrderUtils.paramKV.clear();
        out.print(jsonObject);
        out.flush();
        out.close();
    }
}
