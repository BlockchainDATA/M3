package eco.data.m3.demo.netperf.core;

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

/**
 * MId is a node's unique id on the network.
 *
 */

public class MId implements Serializable{
    
    private byte[] keyBytes;
    private String name;
    private HashMap<String, String> data = new HashMap<>();
    private boolean enableP2P;

    public MId(int id_length_in_byte)
    {
        keyBytes = new byte[id_length_in_byte];
        new Random().nextBytes(keyBytes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public boolean isEnableP2P() {
        return enableP2P;
    }

    public void setEnableP2P(boolean enableP2P) {
        this.enableP2P = enableP2P;
    }
}
