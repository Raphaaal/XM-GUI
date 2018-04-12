package io.github.oliviercailloux.y2018.xmgui.file1;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import io.github.oliviercailloux.y2018.xmgui.contract1.Alternative;
import io.github.oliviercailloux.y2018.xmgui.contract1.Criterion;
import io.github.oliviercailloux.y2018.xmgui.contract1.MCProblem;

import com.google.common.collect.UnmodifiableIterator;

public class App {

	public static void main(String[] args) throws FileNotFoundException, JAXBException, IOException {
		Alternative alt= new Alternative(1);
		Criterion crt =new Criterion(1);
		Criterion crt2 = new Criterion(2);
		Alternative alt2 = new Alternative(2);
		MCProblem mcp = new MCProblem();
		mcp.putValue(alt, crt, 2.0f);
		mcp.putValue(alt2, crt2, 13.3f);
		Marshalling tm = new Marshalling(mcp);
		tm.marshalAndWrite();
		
		//lecture de file1
		Unmarshalling u = new Unmarshalling();
		MCProblem unmarshalledMcp = u.unmarshalAndStore();
		
		UnmodifiableIterator<Alternative> it =unmarshalledMcp.getTableEval().rowKeySet().iterator();
		while(it.hasNext()){
			Alternative a=it.next();
			System.out.println("-------------------------------------------------------");
			System.out.println("Alternative : " + a.getId());
			for(Criterion c: unmarshalledMcp.getValueList(a).keySet().asList())
				System.out.println("Criterion : " + c.getId());
			System.out.println("Value : " + unmarshalledMcp.getValueList(a).values());
			System.out.println("-------------------------------------------------------");
		}
	}

}