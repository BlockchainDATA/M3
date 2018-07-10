/*
 * Copyright (C) 2018 Blockchain Data Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package eco.data.m3.routing.api.utils;

import junit.framework.TestCase;
import org.junit.Test;


/**
* @Author: xquan
* @Description: Os Infomation Test. 
**/
public class MD5UtilTest extends TestCase {

    @Test
    public void testMD5Util() {

        byte[] input = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 49};
        String output = MD5Util.makeMD5(input);
        String expect = "499351bfa5f412772850df0c87a965f5";
        assertEquals(expect, output);

    }

}
