package io.github.oliviercailloux.y2018.xmgui.file1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import io.github.oliviercailloux.xmcda_2_2_1_jaxb.ObjectFactory;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternative;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternatives;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criteria;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criterion;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.XMCDA;
import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;
import io.github.oliviercailloux.y2018.xmgui.file1.TestMarshal;

public class UnMarshallingXML {
	
	public static Alternative getAltFromXml(int i, NodeList nList) {
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;
			System.out.println("AlternativeID : " + eElement.getAttribute("id"));
			String alt = eElement.getAttribute("id").substring(1);
			int finalAlt = Integer.parseInt(alt);
			Alternative alternative = new Alternative(finalAlt);

		return alternative;
	}
	
	public static Criterion getCrtFromXml(int i, NodeList nList) {
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;
			System.out.println("AlternativeID : " + eElement.getAttribute("id"));
			String crt = eElement.getAttribute("id").substring(1);
			int finalCrt = Integer.parseInt(crt);
			Criterion criterion = new Criterion(finalCrt);

		return criterion;
	}
	
	
	
	// Main Pour test avant commit final
	public static void main(String[] args) throws JAXBException, FileNotFoundException, IOException, ParserConfigurationException, SAXException {

	    try (final FileInputStream fis = new FileInputStream(new File("/Users/Razorin/Desktop/PERSO/MIAGE/DEV/JAVA/XM-GUI/resources/resourcesfile1/testAyoub.xml"))) {
	    	
	    		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    		Document doc = dBuilder.parse(fis);
		
	    		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
	    		NodeList nList = doc.getElementsByTagName("alternative");
		
	    		System.out.println("Alternatives");
	    		System.out.println("-----------");
	    		// for (int i = 0; i < nList.getLength(); i++ ) {
	    			Alternative alt = getAltFromXml(0, nList);
	    		// } 
		
	    		System.out.println();
		
	    		nList = doc.getElementsByTagName("criterion");
	    		System.out.println("Criteria");
	    		System.out.println("-----------");
	    		// for (int i = 0; i < nList.getLength(); i++ ) {
	    			Criterion crt = getCrtFromXml(0, nList);
	    		// }
	    		
	    		TestMarshal tm = new TestMarshal(alt, crt);
	    		tm.marshalAndShow();

	    } // end try
	    
	} // end main
	
}

