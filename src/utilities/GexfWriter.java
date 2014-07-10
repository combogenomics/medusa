package utilities;

import graphs.MyGraph;
import graphs.MyEdge;
import graphs.MyNode;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GexfWriter {

	private GexfWriter() {

	}

	public static void write(MyGraph graph, String fileName)
			throws IOException, TransformerException,
			ParserConfigurationException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element gexf = doc.createElementNS("http://www.gexf.net/1.1draft",
				"gexf");
		gexf.setAttribute("version", "1.1");
		gexf.setAttribute("xmlns:viz", "http://www.gexf.net/1.1draft/viz");
		gexf.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		gexf.setAttribute("xsi:schemaLocation",
				"http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(gexf);
		Element graphElement = doc.createElement("graph");
		gexf.appendChild(graphElement);
		// dichiarazione attributi archi
		Element attributesElement = doc.createElement("attributes");
		graphElement.appendChild(attributesElement);
		attributesElement.setAttribute("class", "edge");
		attributesElement.setAttribute("mode", "static");
		Element distanceElement = doc.createElement("attribute");
		distanceElement.setAttribute("id", "1");
		distanceElement.setAttribute("title", "distance");
		distanceElement.setAttribute("type", "integer");
		attributesElement.appendChild(distanceElement);
		Element orientation_max = doc.createElement("attribute");
		orientation_max.setAttribute("id", "2");
		orientation_max.setAttribute("title", "orientation_max");
		orientation_max.setAttribute("type", "string");
		attributesElement.appendChild(orientation_max);
		// dichiarazione attributi nodo
		Element nodeAttributesElement = doc.createElement("attributes");
		graphElement.appendChild(nodeAttributesElement);
		nodeAttributesElement.setAttribute("class", "node");
		nodeAttributesElement.setAttribute("mode", "static");
		Element lengthElement = doc.createElement("attribute");
		lengthElement.setAttribute("id", "0");
		lengthElement.setAttribute("title", "orientation");
		lengthElement.setAttribute("type", "integer");
		nodeAttributesElement.appendChild(lengthElement);
		// nodes block
		Element nodesBlock = doc.createElement("nodes");
		graphElement.appendChild(nodesBlock);

		// node elements
		for (MyNode n : graph.getNodes()) {
			Element node = doc.createElement("node");
			nodesBlock.appendChild(node);
			node.setAttribute("label", n.getLabel());
			node.setAttribute("id", n.getId());
			Element attvalues = doc.createElement("attvalues");
			node.appendChild(attvalues);
			Element attvalue = doc.createElement("attvalue");
			attvalue.setAttribute("for", "0");
			attvalue.setAttribute("value", String.valueOf(n.getOrientation()));
			attvalues.appendChild(attvalue);
		}

		// edges block
		Element edgesBlock = doc.createElement("edges");
		graphElement.appendChild(edgesBlock);

		// edges
		for (MyEdge e : graph.getEdges()) {
			Element edge = doc.createElement("edge");
			edgesBlock.appendChild(edge);
			edge.setAttribute("target", e.getTarget().getId());
			edge.setAttribute("source", e.getSource().getId());
			edge.setAttribute("id", e.getId());
			Element attvalues = doc.createElement("attvalues");
			edge.appendChild(attvalues);
			Element attvalue = doc.createElement("attvalue");
			attvalue.setAttribute("for", "2");
			attvalue.setAttribute("value", String.valueOf(e.orientationString()));
			attvalues.appendChild(attvalue);

		}

		// write the xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		// StreamResult result = new StreamResult(System.out);
		transformer.transform(source, result);
		System.out.println("File saved: "+fileName);
	}

}
