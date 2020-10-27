package com.lxl.essence.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2016/6/17.
 * 常用工具
 * java -jar E:\sign\801\signapk.jar E:\myWork\2016\r16\platform.x509.pem E:\myWork\2016\r16\platform.pk8 E:\myWork\2016\r16\Launcher\bin\Launcher.apk C:\Users\PANPAN\Desktop\Launcher-R16.apk
 */
public class Common {
    private static final String TAG = Common.class.getSimpleName();

    private Common() {
    }


    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return "";
        }
    }

    public static String getFileSuffix(String pathandname) {
        int start = pathandname.lastIndexOf(".");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return "";
        }
    }


    /**
     * @param gridIndex 格口的索引
     * @return 十六进制字符串  不足两位补零
     */
    public static String formatGridNo(int gridIndex) {
        return String.format(Locale.getDefault(), "%02x", gridIndex);
    }

    public static final boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {
                return true;
            }
        }
        return false;
    }


    //截取数字
    public static int getNumbers(String content) {
        int num = -1;
        if (TextUtils.isEmpty(content)) {
            return num;
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            if (!TextUtils.isEmpty(matcher.group(0))) {
                try {
                    num = Integer.parseInt(matcher.group(0));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return num;
    }

    public static String packageInfo(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//            code = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }


}