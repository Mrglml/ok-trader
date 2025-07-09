package sol.trade.gui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okx.trade.gui.utils.Constant;
import okx.trade.gui.utils.SSLUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class Web3APIRequest {
    public static void main(String[] args) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Constant.PROXY, Constant.PORT));
            SSLUtil.trustAllCertificates();
            // 设置 API URL
            String urlString = "https://web3.okx.com/priapi/v1/holder-intelligence/cluster/info?chainId=501&chainIndex=1&tokenAddress=3oQwNvAfZMuPWjVPC12ukY7RPA9JiGwLod6Pr4Lkpump&walletAddress=0x0c9b5750308e385223474dc53e3d0365584371c9&t=1752045052367";

            // 创建 URL 对象
            URL url = new URL(urlString);

            // 打开连接，使用代理
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

            // 设置请求方法
            connection.setRequestMethod("GET");

            // 设置请求头
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("app-type", "web");
            connection.setRequestProperty("devid", "ac52e131-283d-4315-abe7-47594c86fea9");
            connection.setRequestProperty("ok-timestamp", "1752045052372");
            connection.setRequestProperty("ok-verify-sign", "rt8zKMO+7GAtvbEfTt6V/CGGY/6eG6byRYkLQkvYp4c=");
            connection.setRequestProperty("ok-verify-token", "dcb8a287-5179-4dfd-aecd-0772fb79b02b");
            connection.setRequestProperty("priority", "u=1, i");
            connection.setRequestProperty("referer", "https://web3.okx.com/zh-hans/holder-intelligence/solana/3oQwNvAfZMuPWjVPC12ukY7RPA9JiGwLod6Pr4Lkpump");
            connection.setRequestProperty("sec-ch-ua", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
            connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            connection.setRequestProperty("sec-fetch-dest", "empty");
            connection.setRequestProperty("sec-fetch-mode", "cors");
            connection.setRequestProperty("sec-fetch-site", "same-origin");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");
            connection.setRequestProperty("x-cdn", "https://web3.okx.com");
            connection.setRequestProperty("x-fptoken", "eyJraWQiOiIxNjgzMzgiLCJhbGciOiJFUzI1NiJ9...");
            connection.setRequestProperty("x-fptoken-signature", "{P1363}XAVfKKc8q+iADoACB9eB7XiBGP7oGFpIoKGSp56cu8p...");
            connection.setRequestProperty("x-id-group", "2120320450394060001-c-22");
            connection.setRequestProperty("x-locale", "zh_CN");
            connection.setRequestProperty("x-request-timestamp", "1752045052367");
            connection.setRequestProperty("x-simulated-trading", "undefined");
            connection.setRequestProperty("x-site-info", "==QfxojI5RXa05WZiwiIMFkQPx0Rfh1SPJiOiUGZvNmIsIyUVJiOi42bpdWZyJye");
            connection.setRequestProperty("x-utc", "8");
            connection.setRequestProperty("x-zkdex-env", "0");

            // 设置连接和读取超时
            connection.setConnectTimeout(10000);  // 10秒
            connection.setReadTimeout(10000);     // 10秒

            // 发送请求并获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 输出响应内容
                JSONObject jsonResponse = JSON.parseObject(response.toString());
                JSONObject data = jsonResponse.getJSONObject("data");

                if (data != null) {
                    JSONArray clusterList = data.getJSONArray("clusterList");

                    for (int i = 0; i < clusterList.size(); i++) {
                        JSONObject cluster = clusterList.getJSONObject(i);
                        String clusterName = cluster.getString("clusterName");

                        if ("Rank-1".equals(clusterName)) {
                            JSONArray children = cluster.getJSONArray("children");
                            System.out.println("Found Rank-1 cluster with " + children.size() + " addresses:");

                            for (int j = 0; j < children.size(); j++) {
                                JSONObject child = children.getJSONObject(j);
                                String address = child.getString("address");
                                System.out.println(address+":" + "poke"+(j+1));
                            }
                        }
                    }
                }

            } else {
                System.out.println("GET request failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
