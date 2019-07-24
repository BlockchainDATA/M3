package eco.data.m3.demo.netperf.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;
import java.util.Random;

import eco.data.m3.net.core.serializer.MIdDeserializer;
import eco.data.m3.net.core.serializer.MIdSerializer;
import eco.data.m3.net.util.Base64Util;
import io.netty.buffer.ByteBuf;

public class MAPi {
    private static MId mId;
    public static void init(String appId,String appKey){
         mId = new MId();
    }

    public static void connectNet(){
        if(mid == null){
            if (M3Constants.DEBUGGING_LOG){
                System.out.println("Mid is not init");
            }
            return;
        }
        check();
    }

    private static boolean check(){
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE};


        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }


    private static boolean isInDuration(String startTime, String endTime) {
        Calendar c = Calendar.getInstance();
        String str = date2Str(c.getTime());
        String st = str + startTime;
        String et = str + endTime;
        long sUtc = utcToLocal(st);
        long eUtc = utcToLocal(et);
        long cUtc = System.currentTimeMillis();
        return cUtc >= sUtc && cUtc <= eUtc;
    }

    private static String date2Str(Date d) {
        if (d == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        String s = sdf.format(d);
        return s;
    }


    private static String getCurrent() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(c.getTime());
    }

    private static long utcToLocal(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate.getTime();
    }

}
