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

import java.util.Locale;  

/**
* @author: xquan
* A Convert Util for Hex and String convertion.
* @since: Created in 2018-6-29
**/
public class CHexConvert  
{        
	
    public static byte[] hexStr2Bytes(String src){  
        src = src.trim().replace(" ", "").toUpperCase(Locale.US);  
 
        int m=0,n=0;  
        int iLen=src.length()/2;  
        byte[] ret = new byte[iLen];  
          
        for (int i = 0; i < iLen; i++){  
            m=i*2+1;  
            n=m+1;  
            ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);  
        }  
        return ret;  
    }  
  
}  