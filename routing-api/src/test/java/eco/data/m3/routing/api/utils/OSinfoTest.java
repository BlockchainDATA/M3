package eco.data.m3.routing.api.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class OSinfoTest extends TestCase {

    @Test
    public void testOsinfo(){
        assertTrue( OSinfo.isWindows()||OSinfo.isLinux()||OSinfo.isMacOS());
    }
}
