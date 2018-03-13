package com.wso2.org.dsscall.mediator;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.dataservices.core.DataServiceFault;
import org.wso2.carbon.dataservices.core.DataServiceProcessor;
import org.wso2.carbon.dataservices.core.dispatch.SingleDataServiceRequest;
import org.wso2.carbon.dataservices.core.engine.DataService;
import org.wso2.carbon.dataservices.core.engine.ParamValue;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * DSSCall mediator
 */
public class DSSCallMediator extends AbstractMediator {
	private String DSName = "";
	private String request = "";


	public boolean mediate(MessageContext messageContext) {

		System.out.println("Mediator starts successfully");
		String serviceName = getDSName();
		String serviceRequest = getRequest();
		System.out.println("request"+getRequest());

		/*Casting the synapse message context to axis2 message context*/
		org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext)
				.getAxis2MessageContext();

		System.out.println(axis2MessageContext.getServiceContext().getName());



		try {

			AxisService axisService = axis2MessageContext.getConfigurationContext().getAxisConfiguration()
					.getService(serviceName);
			axis2MessageContext.setAxisService(axisService);

			/*casting data service from axis service*/
			DataService dataService = (DataService)axisService.getParameter("org.wso2.ws.dataservice.dataservice.obj")
					.getValue();

			System.out.println("DS Name: "+dataService.getName());
			System.out.println("DS Operations: "+dataService.getOperationNames());
			System.out.println("DS Queries: "+dataService.getQueries());
			System.out.println(axis2MessageContext.getEnvelope().getBody());
			System.out.println(axis2MessageContext.getEnvelope());
			//axis2MessageContext.getEnvelope();

			System.out.println(axis2MessageContext.getEnvelope().getBody().getFirstElement());
			OMElement element = axis2MessageContext.getEnvelope().getBody().getFirstElement();
			Map<String, ParamValue> paramValue = getSingleInputValuesFromOM(element);
			System.out.println(paramValue);
			SingleDataServiceRequest singleDataServiceRequest
					= new SingleDataServiceRequest(dataService,serviceRequest,paramValue);

			System.out.println(singleDataServiceRequest.processRequest());

			System.out.println("SingleDataServiceRequest completed");

			/*Get the operations(Callable requests) using axis service*/
			Iterator<AxisOperation> axisOperationIterator = axisService.getOperations();
			while(axisOperationIterator.hasNext())
			{
				System.out.println(axisOperationIterator.next().getName());
			}

			System.out.println("Operation list finished");

			QName qName = new QName(serviceRequest);
			axis2MessageContext.getAxisMessage().getAxisOperation().setName(qName);


			System.out.println(axis2MessageContext.getAxisMessage().getAxisOperation().getName());


		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		} catch (DataServiceFault dataServiceFault) {
			dataServiceFault.printStackTrace();
		}
		//access the dss using DataServiceProcessor
		System.out.println("Access the dss using DataServiceProcessor");
		try {
			OMElement omElement = DataServiceProcessor.dispatch(axis2MessageContext);
			System.out.println(omElement);
				//create soap envelope
			/*SOAPFactory fac = getSOAPFactoryHere(axis2MessageContext);
			SOAPEnvelope envelope = fac.getDefaultEnvelope();
			if (omElement!= null) {
				envelope.getBody().addChild(omElement);
			}
			axis2MessageContext.setEnvelope(envelope);*/

		} catch (DataServiceFault dataServiceFault) {
			dataServiceFault.printStackTrace();
		}

		System.out.println("SOAP Envelope");
		System.out.println(axis2MessageContext.getEnvelope());
		System.out.println("Mediator function completed!");
		return true;
	}



	private static Map<String, ParamValue> getSingleInputValuesFromOM(OMElement inputMessage) {
		if (inputMessage == null) {
			return new HashMap();
		} else {
			Map<String, ParamValue> inputs = new HashMap();
			Map<String, List<OMElement>> inputMap = new HashMap();
			Iterator iter = inputMessage.getChildElements();

			List omElList;
			while(iter.hasNext()) {
				OMElement element = (OMElement)iter.next();
				String name = element.getLocalName();
				if (!inputMap.containsKey(name)) {
					inputMap.put(name, new ArrayList());
				}

				omElList = (List)inputMap.get(name);
				omElList.add(element);
			}

			ParamValue paramValue;
			String key;
			for(Iterator var11 = inputMap.keySet().iterator(); var11.hasNext(); inputs.put(key, paramValue)) {
				key = (String)var11.next();
				omElList = (List)inputMap.get(key);
				if (omElList.size() == 1) {
					paramValue = new ParamValue(getTextValueFromOMElement((OMElement)omElList.get(0)));
				} else {
					paramValue = new ParamValue(2);
					Iterator var9 = omElList.iterator();

					while(var9.hasNext()) {
						OMElement omEl = (OMElement)var9.next();
						paramValue.addToArrayValue(new ParamValue(getTextValueFromOMElement(omEl)));
					}
				}
			}

			return inputs;
		}
	}
	private static String getTextValueFromOMElement(OMElement omEl) {
		String nillValue = omEl.getAttributeValue(new QName("http://www.w3.org/2001/XMLSchema-instance", "nil"));
		return nillValue == null || !nillValue.equals("1") && !nillValue.equals("true") ? omEl.getText() : null;
	}

	public void setDSName(String DSName) {

		this.DSName = DSName;
	}

	public String getDSName() {
		return DSName;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
	/**
	 * This is to setup soap envelope
	 */
	/*private SOAPFactory getSOAPFactoryHere(org.apache.axis2.context.MessageContext msgContext) throws AxisFault {
		String nsURI = msgContext.getEnvelope().getNamespace().getNamespaceURI();
		if ("http://www.w3.org/2003/05/soap-envelope".equals(nsURI)) {
			return OMAbstractFactory.getSOAP12Factory();
		} else if ("http://schemas.xmlsoap.org/soap/envelope/".equals(nsURI)) {
			return OMAbstractFactory.getSOAP11Factory();
		} else {
			throw new AxisFault(Messages.getMessage("invalidSOAPversion"));
		}
	}*/
}