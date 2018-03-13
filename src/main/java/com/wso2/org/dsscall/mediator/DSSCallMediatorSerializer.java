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

package com.wso2.org.dsscall.mediator;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;


/**
 * Serializer for {@link DSSCallMediator} instances.
 *
 * @see DSSCallMediatorSerializer
 */
public class DSSCallMediatorSerializer extends AbstractMediatorSerializer {

    public OMElement serializeSpecificMediator(Mediator m) {

        if (!(m instanceof DSSCallMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        DSSCallMediator mediator = (DSSCallMediator) m;
        OMElement dssCall = fac.createOMElement("dsscall", synNS);
        saveTracingState(dssCall,mediator);

        return dssCall;
    }

    public String getMediatorClassName() {
        return DSSCallMediator.class.getName();
    }
}