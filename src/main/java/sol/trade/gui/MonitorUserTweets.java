package sol.trade.gui;

import okx.trade.gui.utils.Constant;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admin
 */
public class MonitorUserTweets {
    public static void main(String[] args) throws TwitterException {
        // 设置 Twitter API 认证信息
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("HxKPp3I3scAw5HQN0u8MLm7hk")
                .setOAuthConsumerSecret("aUURaahokSqrU2doguT9dWGJvlcEYmV2fA88YLybbetA1xKHy2")
                .setOAuthAccessToken("1453252173719117828-WBibxVQVd9VpBZefs14SNsNCVgFmvf")
                .setOAuthAccessTokenSecret("8234XOQ0N55zdLhE6rSOUIssZWO2V24vd03xUnfHc1gRD")
                .setHttpProxyHost(Constant.PROXY)
                .setHttpProxyPort(Constant.PORT);

        // 创建 Twitter 实例
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        twitter.updateStatus("hello world");
//        try {
//            // 设置要监控的用户
//            String username = "@shawmakesmagic"; // 替换为目标用户名
//            User user = twitter.showUser(username);
//
//            // 获取用户最近的推文
//            Paging paging = new Paging(1, 10); // 获取最新的 10 条推文
//            List<Status> statuses = twitter.getUserTimeline(user.getId(), paging);
//
//            // 遍历推文，提取其中的 Solana 合约地址
//            for (Status status : statuses) {
//                System.out.println("Tweet: " + status.getText());
//                String ca = extractSolCA(status.getText());
//                if (ca != null) {
//                    System.out.println("Found Solana CA: " + ca);
//                }
//            }
//        } catch (TwitterException te) {
//            te.printStackTrace();
//        }
    }

    // 提取推文中的 Solana 合约地址
    private static String extractSolCA(String tweetText) {
        // 使用正则表达式匹配 Solana 合约地址（例：以 '0x' 开头，长度为 42 位）
        String regex = "\\b[1-9A-HJ-NP-Za-km-z]{32,44}\\b";  // Solana 地址的正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tweetText);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;  // 没有找到 Solana 合约地址
    }
}
