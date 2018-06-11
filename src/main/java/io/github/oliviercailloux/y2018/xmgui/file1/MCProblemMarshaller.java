package io.github.oliviercailloux.y2018.xmgui.file1;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.UnmodifiableIterator;

import io.github.oliviercailloux.xmcda_2_2_1_jaxb.ObjectFactory;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternative;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2AlternativeOnCriteriaPerformances;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternatives;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criteria;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criterion;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2PerformanceTable;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Value;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.XMCDA;
import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;
import io.github.oliviercailloux.y2018.xmgui.contract1.MCProblem;

public class MCProblemMarshaller {

	/*
	 * The Multi-Criteria Problem to be marshalled.
	 */
	final JAXBContext jc;
	final Marshaller marshaller;
	private MCProblem mcp;
	
	
	public MCProblemMarshaller(MCProblem mcp) throws JAXBException {
		Objects.requireNonNull(mcp);
		this.mcp = mcp;
		jc=JAXBContext.newInstance(XMCDA.class);
		marshaller=jc.createMarshaller();
;
	}
	
	protected static final ObjectFactory f = new ObjectFactory();

	/**
	 * This method marshalls Alternative and Criterion objects and their respective performance pair values 
	 * from the MCProblem object to output an XML file abiding by the XMCDA standard.
	 * 
	 * @param fos the XML file output
	 * @throws JAXBException
	 */
	public void marshalAndWrite(FileOutputStream fos) throws JAXBException {
		
		
		// Add X2Alternative objects
		final X2Alternatives alternatives = f.createX2Alternatives();
		UnmodifiableIterator<Alternative> itAlts = mcp.getAlternatives().iterator();
		while (itAlts.hasNext()) {
			Alternative a = itAlts.next();
			alternatives.getDescriptionOrAlternative().add(BasicObjectsMarshallerToX2.basicAlternativeToX2(a));

		}
		
		// Add X2Criterion objects
		final X2Criteria criteria = f.createX2Criteria();
		UnmodifiableIterator<Criterion> itCrits = mcp.getCriteria().iterator();
		while (itCrits.hasNext()) {
			criteria.getCriterion().add(BasicObjectsMarshallerToX2.basicCriterionToX2(itCrits.next()));
		}
		
		// Add X2Performances 
		final X2PerformanceTable perfTable = f.createX2PerformanceTable();
		for (Alternative a : mcp.getAlternatives()) {
			X2AlternativeOnCriteriaPerformances performances=f.createX2AlternativeOnCriteriaPerformances();
			UnmodifiableIterator<Entry<Criterion, Float>> itCritsPerf=mcp.getTableEval().row(a).entrySet().iterator();
			while(itCritsPerf.hasNext()){
				performances.getPerformance().add(CreatePerformance.createPerformance(itCritsPerf.next()));
				performances.setAlternativeID(Integer.toString(a.getId()));
			}
				perfTable.getAlternativePerformances().add(performances);
		}

		// Output the corresponding XMCDA file
		final XMCDA xmcda = f.createXMCDA();
		final List<JAXBElement<?>> xmcdaSubElements = xmcda.getProjectReferenceOrMethodMessagesOrMethodParameters();
		xmcdaSubElements.add(f.createXMCDAAlternatives(alternatives));
		xmcdaSubElements.add(f.createXMCDACriteria(criteria));
		xmcdaSubElements.add(f.createXMCDAPerformanceTable(perfTable));
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// Proposer aussi le marshalling vers un NODE plutot qu'un output stream
		marshaller.marshal(xmcda, fos);
	}
	
	/*
	 * Method that outputs a set of alternatives, clean for WSCallRank
	 */
	public Element altsNodeForWSCall(MCProblem mcp, Document doc) throws JAXBException {
		
		// Add X2Alternative objects
		final X2Alternatives alternatives = f.createX2Alternatives();
		UnmodifiableIterator<Alternative> itAlts = mcp.getAlternatives().iterator();
		while (itAlts.hasNext()) {
			Alternative a = itAlts.next();
			alternatives.getDescriptionOrAlternative().add(BasicObjectsMarshallerToX2.basicAlternativeToX2(a));

		}
		
		Element XMCDANode = doc.createElement("xmcda:XMCDA");
		Element alternativesNode = doc.createElement("alternatives");
		Attr altId = doc.createAttribute("alternative id");
		
		XMCDANode.setAttribute("xmlns:xmcda", "http://www.decision-deck.org/2012/XMCDA-2.2.1"); //adds an attribute
		XMCDANode.appendChild(alternativesNode); 
		altId.setValue("1");
		
		alternativesNode.setAttributeNode(altId);
		
		return XMCDANode;
		
		
	}
	
}