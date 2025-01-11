package okx.trade.gui.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Administrator
 */
public class OkxBaseUtil extends RequestUtil {

    /**
     *  Get Kline data for a specific symbol
     * @param symbol
     * @param bar
     * @param proxy
     * @return
     */
    public static JSONArray getKlines(String symbol, String bar, Proxy proxy) {
        try {
            String endpoint = String.format("https://www.okx.com/api/v5/market/candles?instId=%s&bar=%s", symbol, bar);
            String response = sendGetRequest(endpoint, proxy);
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    /**
     * Analyze Kline data
     * @param klines
     * @param days
     * @param currentPrice
     * @param minDaysDiff
     * @param priceDiff
     * @return
     */
    public static boolean   analyzeKlines(JSONArray klines, int days, double currentPrice, int minDaysDiff,int priceDiff) {
        double highestPrice = Double.MIN_VALUE;
        double lowestPrice = Double.MAX_VALUE;
        long highestTime = 0;
        long lowestTime = 0;

        // Calculate the current time in milliseconds
        long currentTime = System.currentTimeMillis();
        long daysInMillis = days * 24 * 60 * 60 * 1000L;

        // Calculate the highest and lowest prices in the given period
        for (int i = 0; i < klines.length(); i++) {
            JSONArray kline = klines.getJSONArray(i);
            double highPrice = kline.getDouble(2);
            double lowPrice = kline.getDouble(3);
            long time = kline.getLong(0);

            // Only consider prices within the specified number of days
            if (currentTime - time <= daysInMillis) {
                if (highPrice > highestPrice) {
                    highestPrice = highPrice;
                    highestTime = time;
                }
                if (lowPrice < lowestPrice) {
                    lowestPrice = lowPrice;
                    lowestTime = time;
                }
            }
        }

        // Calculate the time differences in days
        long highestTimeDiff = (currentTime - highestTime) / (1000 * 60 * 60 * 24);
        long lowestTimeDiff = (currentTime - lowestTime) / (1000 * 60 * 60 * 24);

        long highestTimeDiffHour = (currentTime - highestTime) / (1000 * 60 * 60 );
        long lowestTimeDiffHour = (currentTime - lowestTime) / (1000 * 60 * 60 );

        // Check if the current price is within 2 points of the highest or lowest prices and if the time difference is greater than minDaysDiff
        boolean isPriceNearHighest = Math.abs(currentPrice - highestPrice) <= currentPrice * priceDiff/100.0;
        boolean isPriceNearLowest = Math.abs(currentPrice - lowestPrice) <= currentPrice * priceDiff/100.0;
        boolean isTimeDiffValid = highestTimeDiff > minDaysDiff || lowestTimeDiff > minDaysDiff;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(highestTime) +"最高价："+ highestPrice+" 与当前时间相差"+highestTimeDiffHour+"小时\n"
                +sdf.format(lowestTime)+"最低价：" + lowestPrice+" 与当前时间相差"+ lowestTimeDiffHour +"小时\n当前价："+ currentPrice+"\n");
        return (isPriceNearHighest || isPriceNearLowest) && isTimeDiffValid;
    }

    /**
     *  Get current price for a specific symbol
     * @param symbol
     * @param proxy
     * @return
     */
    public static double getCurrentPrice(String symbol, Proxy proxy) {
        try {
            String endpoint = "https://www.okx.com/api/v5/market/ticker?instId=" + symbol;
            String response = sendGetRequest(endpoint, proxy);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray data = jsonResponse.getJSONArray("data");
            if (data.length() > 0) {
                return data.getJSONObject(0).getDouble("last");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Method to filter symbols based on criteria
     * @param symbols
     * @param bar
     * @param days
     * @param minDaysDiff
     * @param proxy
     * @return
     */
    public static List<String> filterSymbols(List<String> symbols, String bar, int days, int minDaysDiff,int priceDiff, Proxy proxy) {
        List<String> resultSymbols = new ArrayList<>();

        for (String symbol : symbols) {
            // Get Kline data
            JSONArray klines = getKlines(symbol, bar, proxy);
            if (klines.length() == 0) {
                continue;
            }
            // Get current price
            double currentPrice = getCurrentPrice(symbol, proxy);
            if (currentPrice == 0.0) {
                continue;
            }
            System.out.println("当前币种："+symbol);
            // Analyze Kline data
            if (analyzeKlines(klines, days, currentPrice, minDaysDiff,priceDiff)) {
                resultSymbols.add(symbol);
            }
        }

        return resultSymbols;
    }

    public static List<String> getFilteredTradingPairs(Double minVolume, Integer topRank, Proxy proxy) {
        List<String> results = new ArrayList<>();

        try {
            // Get all trading pairs
            String endpoint = "https://www.okx.com/api/v5/market/tickers?instType=SPOT";
            String response = sendGetRequest(endpoint,proxy);
            JSONObject jsonResponse = new JSONObject(response);

            JSONArray tickers = jsonResponse.getJSONArray("data");
            List<JSONObject> filteredTickers = new ArrayList<>();

            // Filter trading pairs
            for (int i = 0; i < tickers.length(); i++) {
                JSONObject ticker = tickers.getJSONObject(i);
                String symbol = ticker.getString("instId");
                double volume = ticker.getDouble("volCcy24h");

                // Exclude stablecoins and only include USDT pairs
                if (!symbol.contains("USDT") || symbol.contains("USDC") || symbol.contains("DAI") ||
                        symbol.contains("TUSD") || symbol.contains("BUSD") || symbol.contains("PAX") || symbol.contains("TRY")) {
                    continue;
                }

                // Apply volume filter if provided
                if (minVolume != null && volume < minVolume) {
                    continue;
                }

                filteredTickers.add(ticker);
            }

            // Sort by volume
            filteredTickers.sort((a, b) -> Double.compare(b.getDouble("volCcy24h"), a.getDouble("volCcy24h")));

            // Apply rank filter if provided
            if (topRank != null && filteredTickers.size() > topRank) {
                filteredTickers = filteredTickers.subList(0, topRank);
            }

            // Collect results
            for (JSONObject ticker : filteredTickers) {
                results.add(ticker.getString("instId"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}