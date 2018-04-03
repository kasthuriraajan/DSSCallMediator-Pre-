/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* you may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package com.wso2.org.dsscall.mediator;


import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Properties;

/**
 * Factory for {@link DSSCallMediator} instances.
 * <p>
 * Configuration syntax:
 * <pre>
 * &lt;dsscall&gt;
 *      &lt;dsname/&gt;
 *      &lt;operation/&gt;
 *      &lt;params&gt;
 *          &lt;param name=""&gt; &lt;param/&gt;
 *      &lt;/params&gt;
 *      &lt;target  type="" name=""/&gt;
 * &lt;/dsscall&gt;
 * </pre>
 */

public class DSSCallMediatorFactory extends AbstractMediatorFactory {

    private static final QName DSSCALL_Q =
            new QName(SynapseConstants.SYNAPSE_NAMESPACE, "dsscall");
    private static final QName DS_Name_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "dsname");
    private static final QName Operation_Name_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "operation");
    private static final QName PARAMS_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "params");

    private static final QName PARAM_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "param");

    private static final QName TARGET_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "target");

    private static final QName NAME_Q = new QName("name");
    private static final QName TYPE_Q = new QName("type");


    public Mediator createSpecificMediator(OMElement elem, Properties properties) {

        DSSCallMediator mediator = new DSSCallMediator();
        processAuditStatus(mediator, elem);
        OMElement dsNameElement = elem.getFirstChildWithName(DS_Name_Q);
        if (dsNameElement != null) {
            String dsName = dsNameElement.getText();
            mediator.setDSName(dsName);

        } else {
            throw new SynapseException("dsname element is missing");
        }
        OMElement operationElement = elem.getFirstChildWithName(Operation_Name_Q);
        if (operationElement != null) {
            String operation = operationElement.getText();
            mediator.setOperation(operation);

        } else {
            throw new SynapseException("operation element is missing");
        }

        OMElement paramsElement = elem.getFirstChildWithName(PARAMS_Q);

        if (paramsElement != null) {
            Iterator paramsIterator = paramsElement.getChildrenWithName(PARAM_Q);
            while (paramsIterator.hasNext()) {
                OMElement paramElement = (OMElement) paramsIterator.next();
                OMAttribute nameAtr = paramElement.getAttribute(NAME_Q);

                if (nameAtr != null) {
                    String paramName = nameAtr.getAttributeValue();
                    String paramValue = paramElement.getText();

                    if (paramName != null && paramValue != null) {
                        mediator.setParam(paramName, paramValue);
                    }
                }
            }
        } else {
            throw new SynapseException("params element is missing");
        }

        OMElement targetElement = elem.getFirstChildWithName(TARGET_Q);
        if (targetElement != null) {
            OMAttribute typeAtr = targetElement.getAttribute(TYPE_Q);
            if (typeAtr != null) {
                String type = typeAtr.getAttributeValue();
                mediator.setTargetType(type);
                if (type.equals("property")) {
                    OMAttribute propertyAtr = targetElement.getAttribute(NAME_Q);
                    if (propertyAtr != null) {
                        if (!propertyAtr.getAttributeValue().equals("")) {
                            String propertyName = propertyAtr.getAttributeValue();
                            mediator.setPropertyName(propertyName);
                        } else {
                            throw new SynapseException("name attribute in target element is empty! Please enter a value");
                        }
                    } else {
                        throw new SynapseException("name attribute in target element is missing");
                    }
                }
            } else {
                throw new SynapseException("type attribute in target element is missing");
            }
        } else {
            throw new SynapseException("target element is missing");
        }
        return mediator;
    }

    public QName getTagQName() {
        return DSSCALL_Q;
    }
}

