// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package streams.jmx.client.cli;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

/**
 * IParameterValidator that verifies that a parameter value is a filesystem path
 * that points to an existing file.
 */
public class KeyValueConverter implements IStringConverter<AbstractMap.SimpleImmutableEntry<String,String>> {

    @Override
    public AbstractMap.SimpleImmutableEntry<String,String> convert(String keyValuePair) {
		String[] s = keyValuePair.split("=");
		if (s.length !=2 ) {
			throw new ParameterException(getErrorMessage(keyValuePair));
		}
		return new AbstractMap.SimpleImmutableEntry<String,String>(s[0],s[1]);
    		//return convertInstanceList(instanceList);
	}
	
	private String getErrorMessage(String value) {
		return "The format of the following property specification is not valid: " + value + ". The correct syntax is: <name>=<value>";
	}
    
}
