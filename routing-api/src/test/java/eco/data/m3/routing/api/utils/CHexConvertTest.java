package eco.data.m3.routing.api.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class CHexConvertTest extends TestCase {

    @Test
    public void testCHextConvert(){
        byte[] output = CHexConvert.hexStr2Bytes("3132333435363738393031323334353637383931");
        byte[] expect = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 49};


        assertEquals(expect.length, output.length);
        for(int i=0; i<expect.length; i++)
            assertEquals(expect[i], output[i]);
    }

}
