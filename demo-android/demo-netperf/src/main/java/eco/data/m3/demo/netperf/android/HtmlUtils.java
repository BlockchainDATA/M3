package eco.data.m3.demo.netperf.utils;

import android.text.Html;
import android.text.Spanned;

/**
 * @description: textview 样式
 * @author: dai
 * @create date: 2018/6/7
 * @update date: 2018/6/7
 * @update description:
 */
public class HtmlUtils {
    public static String getRedText(String count) {
        return "观看带红包的内容奖励翻倍哦！当前时段还可领取" + count + "个红包内容，快快去视频列表内找红包吧～";
    }

    public static Spanned getRedTxtFormat(String startTxt, String redTxt, String endTxt) {
        return Html.fromHtml(startTxt + "<font color='#FF5050'>" + redTxt + "</font>" +
                "<font color='#666666'>" + endTxt + "</font>");
    }

    /**
     * &emsp 空格
     *
     * @param front  前一个text
     * @param behind 后一个text
     * @return 最终text
     */
    public static Spanned setTvFormat(String front, String behind) {
        return Html.fromHtml(front + "&emsp &emsp<font color='#333333'>" + behind + "</font>");
    }

    /**
     * 尚未登录
     *
     * @return
     */
    public static Spanned getLogin() {
        return Html.fromHtml("您未登录，<font color='#007aff'>登录</font><font color='#333333'>后可见</font>");
    }

}
