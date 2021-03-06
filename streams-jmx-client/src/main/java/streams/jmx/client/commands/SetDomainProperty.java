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

package streams.jmx.client.commands;

import streams.jmx.client.Constants;
import streams.jmx.client.ExitStatus;
import streams.jmx.client.cli.KeyValueConverter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import com.ibm.streams.management.domain.DomainMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Parameters(commandDescription = Constants.DESC_GETDOMAINSTATE)
public class SetDomainProperty extends AbstractDomainCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("root."
    + SetDomainProperty.class.getName());

    // Bug in version 1.72 prevents convert working here
   
    @Parameter(description = "property-name=property-value ...", required=false)
    private List<String> domainPropertiesString;

    //@Parameter(description = "property-name=property-value ...", required=false,
    //   converter = KeyValueConverter.class)
    private List<AbstractMap.SimpleImmutableEntry<String,String>> domainProperties = null;

    public SetDomainProperty() {
    }

    @Override
    public String getName() {
        return (Constants.CMD_SETDOMAINPROPERTY);
    }

    @Override
    public String getHelp() {
        return (Constants.DESC_SETDOMAINPROPERTY);
    }

    @Override
    protected CommandResult doExecute() {
        try {

           LOGGER.debug("domainPropertiesString.size(): {}",(domainPropertiesString == null)?"null":domainPropertiesString.size());
        

            // domain-property or all is required
            if ((domainPropertiesString == null) || (domainPropertiesString.size() == 0)) {
                throw new ParameterException("The following argument is required: property-name=property-value ...");
            }

            KeyValueConverter keyValueConverter = new KeyValueConverter();

            domainProperties = new ArrayList<AbstractMap.SimpleImmutableEntry<String,String>>();
            for (String domainPropertyString : domainPropertiesString) {
                domainProperties.add(keyValueConverter.convert(domainPropertyString));
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonOut = mapper.createObjectNode();

            DomainMXBean domain = getDomainMXBean();

            // Populate the result object
            ArrayNode propertyArray = mapper.createArrayNode();
            jsonOut.put("domain",domain.getName());

            for (AbstractMap.SimpleImmutableEntry<String, String> keyValuePair : domainProperties) {
                LOGGER.debug("About to set property {} = {}",keyValuePair.getKey(), keyValuePair.getValue());
                ObjectNode propertyObject = mapper.createObjectNode();
                DomainMXBean.PropertyId propertyId = null;
                try {
                    propertyId = DomainMXBean.PropertyId.fromString(keyValuePair.getKey());
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("The following property is not valid: {}",keyValuePair.getKey());
                }
                try {
                    if (propertyId != null) {
                        String oldValue = domain.getProperty(propertyId);
                        domain.setProperty(DomainMXBean.PropertyId.fromString(keyValuePair.getKey()),keyValuePair.getValue());
                        propertyObject.put("property",propertyId.toString());
                        propertyObject.put("newvalue",keyValuePair.getValue());
                        propertyObject.put("previousvalue",oldValue);
                        propertyArray.add(propertyObject);
                    }
                } catch (IllegalStateException e) {
                    LOGGER.warn(e.getLocalizedMessage());
                }
            }

            jsonOut.set("properties",propertyArray);
            jsonOut.put("count",propertyArray.size());

            return new CommandResult(jsonOut.toString());
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SetDomainProperty caught Exception: {}", e.toString());
                e.printStackTrace();
            }
            return new CommandResult(ExitStatus.FAILED_COMMAND, null, e.getLocalizedMessage());
        }
    }
}
