package okx.trade.gui;

import okx.trade.gui.utils.Constant;
import okx.trade.gui.utils.OkxBaseUtil;
import okx.trade.gui.utils.SSLUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * @InterfaceName okx.trade.gui.TradeApplication
 * @Description TODO
 * @Author jmGui
 * @Date 2022/11/2 15:08
 **/

/**
 *
 （输入参数）几小时K线数据
 找出（输入参数）最近几天的最高点和最低点
 实时获取当前价格计算当前价格是不是与之前的最高点或者最低点
 价差2个点左右

 满足上述价差2点之后 算出当前价格时间 最高点或最低点相差得天数



 提供一个方法 要求
 1获取所有得币币交易对，

 2.剔除稳定交易对
 3.只需要USDT交易对
 4.筛选出24小时交易额大于 （输入参数 可空）wUSDT
 或者交易额排名 前 （输入参数 可空） 名

 5.并最后返回交易额由高到低顺序返回
 * @author admin

 */
public class TradeApplication {

    public static void main(String[] args) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Constant.PROXY, Constant.PORT));
            SSLUtil.trustAllCertificates();
            Double minVolume = 3000000D;
            Integer topRank = 30;
            List<String> symbols = OkxBaseUtil.getFilteredTradingPairs(minVolume, topRank, proxy);
//            System.out.println("监测的币种\n"+symbols);
            // 4小时K线，可以更换成1H, 1D等
            String bar = "1H"; 
            // 最近7天
            int recentDays = 3;
            // 相差天数大于3天
            int minDaysDifference = -1;
            // 价差
            int priceDiff = 2;
            while (true) {
                List<String> resultSymbols = OkxBaseUtil.filterSymbols(symbols, bar, recentDays, minDaysDifference, priceDiff,proxy);
                System.out.println("满足条件的交易对：\n" +resultSymbols);
                //1分钟
                Thread.sleep(15*60*1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
