/**
 * The api is created by KP for YTPlayer app.
 * https://github.com/KaustubhPatange/YTPlayer/
 * <p>
 * If you want to use it in your project use it without changing the name
 * or this header info.
 * <p>
 * Support the work!
 **/

package com.kpstv.youtube.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTStatistics {

    private static String TAG = "YTStatistics";
    private String viewCount, likeCount, dislikeCount;

    public String getViewCount() {
        if (viewCount != null) return viewCount;
        else return "0";
    }

    public String getLikeCount() {
        if (likeCount != null) return likeCount;
        else return "0";
    }

    public String getDislikeCount() {
        if (dislikeCount != null) return dislikeCount;
        else return "0";
    }

    public YTStatistics(String videoID) {
        try {
            String url = "https://www.youtube.com/watch?v=" + videoID;
            URLConnection connection = (new URL(url)).openConnection();
            connection.connect();
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // View count...

                Pattern mPattern = Pattern.compile(",\"viewCount\":\"\\d+\",");
                Matcher matcher = mPattern.matcher(line);

                if (matcher.find()) {
                    Matcher matcher1 = Pattern.compile("\\d+").matcher(matcher.group());
                    if (matcher1.find())
                        viewCount = matcher1.group();
                }

                // Likes count...

                Matcher matcher3 = Pattern.compile(
                        "\\{\"label\":\"(.*?)likes").matcher(line);
                if (matcher3.find()) {
                    String[] strs = matcher3.group().split("\"");
                    String torep = strs[strs.length - 1];
                    torep = torep.replace(",", "").
                            replace("likes", "").trim();
                    likeCount = torep;
                }

                // DisLikes count...

                Matcher matcher2 = Pattern.compile(
                        "\\{\"label\":\"(.*?)dislikes").matcher(line);
                if (matcher2.find()) {
                    String[] strs = matcher2.group().split("\"");
                    String torep = strs[strs.length - 1];
                    torep = torep.replace(",", "").
                            replace("dislikes", "").trim();
                    dislikeCount = torep;
                }


                Log.e(TAG, "ViewCount: " + viewCount + ", likeCount: " + likeCount + ", dislikeCount: " + dislikeCount);
            }

            if (viewCount != null && viewCount.contains("No")) viewCount = "0";
            if (likeCount != null && likeCount.contains("No")) likeCount = "0";
            if (dislikeCount != null && dislikeCount.contains("No")) dislikeCount = "0";

            in.close();

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
