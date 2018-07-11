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

package eco.data.m3.cli.utils;

import java.lang.reflect.Field;

/**
* @author: xquan
* Property Printer. 
* Use Java Reflection to load a object's field and value, then print to string.
* Used for show item info.
* @since: 2018-6-30
**/
public class PropertyPrinter {

	public static String print(Object obj)
	{
       Class objCls = (Class) obj.getClass();  
       StringBuffer sb = new StringBuffer();

       Field[] fs = objCls.getDeclaredFields();  
       for(int i = 0 ; i < fs.length; i++){  
           Field f = fs[i];  
           f.setAccessible(true);
           try {
	           Object val = f.get(obj);
	           if(val!=null)
	        	   sb.append(String.format("%-40s", f.getName()) + val + "\n");
	        }catch (Exception e) {
				// TODO: handle exception
			}            
       }  
       return sb.toString();
	}
}
