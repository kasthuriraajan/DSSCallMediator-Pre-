/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package com.wso2.org.dsscall.mediator;/*
*TODO:Class level comment
*/

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;

import javax.xml.namespace.QName;
import java.util.Properties;

/**
 * Factory for {@link DSSCallMediator} instances.
 * <p>
 * Configuration syntax:
 * <pre>
 * &lt;dsscall&gt;
 * &lt;/dsscall&gt;
 * </pre>
 */
public class DSSCallMediatorFactory extends AbstractMediatorFactory {

    private static final QName DSSCALL_Q =
            new QName(SynapseConstants.SYNAPSE_NAMESPACE, "dsscall");
    static final QName DS_Name_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "dsname");
    static final QName Request_Name_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "request");
    /*static final QName DEFAULT_PERCENTAGE_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "defaultPercentage");
*/
    public Mediator createSpecificMediator(OMElement elem, Properties properties) {

        DSSCallMediator mediator = new DSSCallMediator();
        processAuditStatus(mediator, elem);
        OMElement dsNameElement = elem.getFirstChildWithName(DS_Name_Q);
        if (dsNameElement != null) {
            String dsName = dsNameElement.getText();
            mediator.setDSName(dsName);

        } else {
            throw new SynapseException("dsName element is missing");
        }
        OMElement requestElement = elem.getFirstChildWithName(Request_Name_Q);
        if (requestElement != null) {
            String request = requestElement.getText();
            mediator.setRequest(request);

        } else {
            throw new SynapseException("request element is missing");
        }
        return mediator;
    }

    public QName getTagQName() {
        return DSSCALL_Q;
    }
}

