package io.github.oliviercailloux.y2018.xmgui.file1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import io.github.oliviercailloux.xmcda_2_2_1_jaxb.ObjectFactory;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternative;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Alternatives;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criteria;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.X2Criterion;
import io.github.oliviercailloux.xmcda_2_2_1_jaxb.XMCDA;
import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;

public class TestMarshal {


	private Alternative alternative;
	private Criterion criterion;
	
	public TestMarshal(Alternative alt, Criterion crt) {
		alternative = alt;
		criterion = crt;
	}

	public void marshalAndShow() throws JAXBException, FileNotFoundException, IOException {
		final JAXBContext jc = JAXBContext.newInstance(XMCDA.class);
		final Marshaller marshaller = jc.createMarshaller();
		final ObjectFactory f = new ObjectFactory();
        
		final X2Alternatives alternatives = f.createX2Alternatives();
		final X2Alternative alt = f.createX2Alternative();
		alt.setId("a" + alternative.getId());
		alternatives.getDescriptionOrAlternative().add(alt);

		final X2Criteria criteria = f.createX2Criteria();
		final X2Criterion crit = f.createX2Criterion();
		crit.setId("c" + criterion.getId());
		crit.setMcdaConcept("concept");
		criteria.getCriterion().add(crit);

		final XMCDA xmcda = f.createXMCDA();
		final List<JAXBElement<?>> xmcdaSubElements = xmcda
				.getProjectReferenceOrMethodMessagesOrMethodParameters();
		xmcdaSubElements.add(f.createXMCDAAlternatives(alternatives));
		xmcdaSubElements.add(f.createXMCDACriteria(criteria));
		 
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
		try(final FileOutputStream fos = new FileOutputStream(new File("/Users/Razorin/Desktop/PERSO/MIAGE/DEV/JAVA/XM-GUI/resources/resourcesfile1/testAyoub.xml"))) {
				marshaller.marshal(xmcda,fos);
		}
		
	}

	// main pour tester à enlever avant le commit final -> Attention, cela envoie un "WARNING: An illegal reflective access operation has occurred" en Java 9

	public static void main(String[] args) throws JAXBException, FileNotFoundException, IOException {
		Criterion crt = new Criterion(1);
		Alternative alt = new Alternative(1);
		TestMarshal tm = new TestMarshal(alt, crt);
		tm.marshalAndShow();

	}
}