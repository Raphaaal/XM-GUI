 package io.github.oliviercailloux.y2018.xmgui.WSCallRank;
 
 import static org.junit.Assert.assertEquals;
 
 import java.io.IOException;
 import java.io.StringWriter;
 import java.net.URL;
 import java.nio.charset.StandardCharsets;
 
 import javax.xml.namespace.QName;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.transform.OutputKeys;
 import javax.xml.transform.Source;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerConfigurationException;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.TransformerFactoryConfigurationError;
 import javax.xml.transform.dom.DOMResult;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.stream.StreamResult;
 import javax.xml.ws.Dispatch;
 import javax.xml.ws.Service;
 import javax.xml.ws.Service.Mode;
 import javax.xml.ws.soap.SOAPBinding;
 
 import org.junit.Test;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.w3c.dom.Attr;
 import org.w3c.dom.CDATASection;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.dom.Text;
 
 import com.google.common.io.Resources;
 
 import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
 import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;
 import io.github.oliviercailloux.y2018.xmgui.contract1.MCProblem;
 import io.github.oliviercailloux.y2018.xmgui.contract2.AlternativesRanking;
 import io.github.oliviercailloux.y2018.xmgui.file1.MCProblemMarshaller;
 import io.github.oliviercailloux.y2018.xmgui.file2.AlternativesRankingMarshaller;
 
 /*
  * This class performs basic web services calls from decision-deck to
  * first test the web services availabilty and then rank Alternatives according to their values.
  */
 public class CallAltsRankCorrected {
 	
	private static final String ENDPOINT_ADDRESS = "http://webservices.decision-deck.org/soap/rankAlternativesValues-RXMCDA.py";
 	@SuppressWarnings("unused")
 	private static final String FAILURE = "The problem submission was unsuccessful";
 	@SuppressWarnings("unused")
 	private static final Logger LOGGER = LoggerFactory.getLogger(CallAltsRank.class);
 	private static final String SUCCESS = "The problem submission was successful!";
 	private Transformer transformer;
 
 	public CallAltsRankCorrected() {
 		transformer = null;
 	}
 
 	public String asString(Node node) throws TransformerException, TransformerFactoryConfigurationError, TransformerConfigurationException {
 		final StringWriter asString = new StringWriter();
 		getTransformer().transform(new DOMSource(node), new StreamResult(asString));
 		return asString.toString();
 	}
 
 	public Transformer getTransformer() throws TransformerFactoryConfigurationError, TransformerConfigurationException {
 		if (transformer == null) {
 			final TransformerFactory tFactory = TransformerFactory.newInstance();
 			transformer = tFactory.newTransformer();
 			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
 			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
 		}
 		return transformer;
 	}
 
 	public Node invoke(Dispatch<Source> dispatch, Source src) throws TransformerException, TransformerFactoryConfigurationError, TransformerConfigurationException {
 		final Source ret = dispatch.invoke(src);
 		final DOMResult result = new DOMResult();
 		getTransformer().transform(ret, result);
 		final Node resultNode = result.getNode();
 		return resultNode;
 	}
 
 	public void setFileContentToNodeValue(String sourceFile, Node destNode) throws IOException {
 		final URL resUrl = getClass().getResource(sourceFile);
 		final String resStr = Resources.toString(resUrl, StandardCharsets.UTF_8);
 		final Text textNode = destNode.getOwnerDocument().createCDATASection(resStr);
 		destNode.appendChild(textNode);
 	}
 
 	/*
 	 * This method tests the availability of the decision-deck's web services.
 	 */
 	@Test
 	public void testHello() throws Exception {
 		final Service svc = Service.create(new QName("ServiceNamespace", "ServiceLocalPart"));
 		final QName portQName = new QName("PortNamespace", "PortLocalPart");
 		svc.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, ENDPOINT_ADDRESS);
 		final Dispatch<Source> dispatch = svc.createDispatch(portQName, Source.class, Mode.PAYLOAD);
 
 		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
 		final Document doc = builder.newDocument();
 		final Element el = doc.createElement("hello");
 		doc.appendChild(el);
 		final DOMSource src = new DOMSource(doc);
 
 		final Node resultNode = invoke(dispatch, src);
 
 		LOGGER.info("Returned answer: {}.", asString(resultNode));
 
 		final NodeList directChildren = resultNode.getChildNodes();
 		assertEquals(1, directChildren.getLength());
 		final Node firstChild = directChildren.item(0);
 		assertEquals("helloResponse", firstChild.getNodeName());
 		final NodeList subChildren = firstChild.getChildNodes();
 		assertEquals(1, subChildren.getLength());
 		final Node firstSubChild = subChildren.item(0);
 		assertEquals("message", firstSubChild.getNodeName());
 	}
 	
 	/*
 	 * This method tests a simple request submission to the decision-deck's webservices
 	 * in order to rank alternatives according to their values.
 	 */
 	@Test
 	public void testSubmitAndRequest() throws Exception {
 		final Service svc = Service.create(new QName("ServiceNamespace", "ServiceLocalPart"));
 		final QName portQName = new QName("PortNamespace", "PortLocalPart");
 		svc.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, ENDPOINT_ADDRESS);
 		final Dispatch<Source> dispatch = svc.createDispatch(portQName, Source.class, Mode.PAYLOAD);
 		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 		factory.setNamespaceAware(true);
 		final DocumentBuilder builder = factory.newDocumentBuilder();
 		final String ticket;
 		
 		{
 			// For the time being we create a raw MCP here, but in the future, the GUI will create and pass thi data.
 			MCProblem mcp = new MCProblem();
			Alternative alt0= new Alternative(0);
			Alternative alt1= new Alternative(1);
			Criterion crt =new Criterion(0);

 			mcp.putEvaluation(alt0, crt, 0.0f);
			mcp.putEvaluation(alt1, crt, 1.0f);
			
 			MCProblemMarshaller mcpMarshaller= new MCProblemMarshaller(mcp);
 			AlternativesRanking altr = new AlternativesRanking(1000,alt0);
 			altr.putAltRank(2,alt1);
 			AlternativesRankingMarshaller altrMarshaller = new AlternativesRankingMarshaller(altr);
 			
 			final Document doc = builder.newDocument();
 			final Element submit = doc.createElement("submitProblem");
 			submit.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
 			
 			final Element sub1 = doc.createElement("overallValues");
 			CDATASection cdataOverVal = doc.createCDATASection(asString(altrMarshaller.altsRankingNodeForWSCall(altrMarshaller, doc)));
 			sub1.appendChild(cdataOverVal);
 			doc.appendChild(submit);
			submit.appendChild(sub1); 
 			
 			final Element sub2 = doc.createElement("alternatives");
 			submit.appendChild(sub2);
 			CDATASection cdataAlt = doc.createCDATASection(asString(mcpMarshaller.altsNodeForWSCall(mcp, doc)));
 			sub2.appendChild(cdataAlt);

 			final Attr attrType1 = doc.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type");
 			attrType1.setValue("xsd:string");
			sub1.setAttributeNodeNS(attrType1);

 			final Attr attrType2 = (Attr) attrType1.cloneNode(true);
			sub2.setAttributeNodeNS(attrType2);

 			
 			LOGGER.info("Sending: {}.", asString(doc));
 
 			final Node ret = invoke(dispatch, new DOMSource(doc));
 
 			LOGGER.info("Returned answer: {}.", asString(ret));
 
 			final NodeList directChildren = ret.getChildNodes();
 			assertEquals(1, directChildren.getLength());
 			final Node firstChild = directChildren.item(0);
 			assertEquals("submitProblemResponse", firstChild.getNodeName());
 			final NodeList subChildren = firstChild.getChildNodes();
 			assertEquals(2, subChildren.getLength());
 			final Node firstSubChild = subChildren.item(0);
 			assertEquals("message", firstSubChild.getNodeName());
 			final NodeList messageChildren = firstSubChild.getChildNodes();
 			assertEquals(1, messageChildren.getLength());
 			final Node messageText = messageChildren.item(0);
 			assertEquals(SUCCESS, messageText.getNodeValue());
 			final Node secondSubChild = subChildren.item(1);
 			assertEquals("ticket", secondSubChild.getNodeName());
 			ticket = secondSubChild.getFirstChild().getTextContent();
 
 		}
 	}
 }
 		
