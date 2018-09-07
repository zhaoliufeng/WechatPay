import utils.Log;
import utils.XmlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PayCallbackServlet extends HttpServlet {

    private static final String TAG = PayCallbackServlet.class.getSimpleName();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.info(TAG, "接收到微信支付结果回调");
        String inputLine;
        StringBuilder xml = new StringBuilder();
        try {
            //读取返回的xml
            while ((inputLine = req.getReader().readLine()) != null) {
                xml.append(inputLine);
            }
            //关闭流
            req.getReader().close();
            Log.info(TAG, "微信回调内容信息：" + xml);

            //封装 返回值 告诉微信服务器接收成功
            PrintWriter out = resp.getWriter();
            String sb = "<xml>" +
                    "<return_code><![CDATA[SUCCESS]]></return_code>" +
                    "<return_msg><![CDATA[OK]]></return_msg>" +
                    "</xml>";
            out.print(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
