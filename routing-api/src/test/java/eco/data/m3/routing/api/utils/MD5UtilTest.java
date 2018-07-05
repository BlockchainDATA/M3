package eco.data.m3.routing.api.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class MD5UtilTest extends TestCase {

    @Test
    public void testMD5Util() {

        byte[] input = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 49};
        String output = MD5Util.makeMD5(input);
        String expect = "499351bfa5f412772850df0c87a965f5";
        assertEquals(expect, output);

    }

}
