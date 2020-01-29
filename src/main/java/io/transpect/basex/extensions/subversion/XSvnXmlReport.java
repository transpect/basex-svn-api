package io.transpect.basex.extensions.subversion;

import java.io.StringWriter;
import java.io.StringReader;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;

import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Returns reports or errors as XML (currently only
 * string) that needs to be parsed with parse-xml()
 */
public class XSvnXmlReport {

    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    String nsprefix = "c";
    String nsuri = "http://www.w3.org/ns/xproc-step";
    
    /**
     * Render a HashMap as XML c:param-set
     */
    public String createXmlResult(HashMap<String, String> results) throws XMLStreamException {
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
        writer.writeStartDocument("UTF-8", "1.0");
	writer.writeStartElement(nsprefix, "param-set", nsuri);
	writer.writeNamespace(nsprefix, nsuri);
        for(String key:results.keySet()) {
	    writer.writeStartElement(nsprefix, "param", nsuri);
	    writer.writeAttribute("name", key);
	    writer.writeAttribute("value", results.get(key));
	    writer.writeEndElement();
	}
	writer.writeEndElement();
	writer.writeEndDocument();
        writer.flush();
        sw.flush();
	StringBuffer sb = sw.getBuffer();
	String strResult = sb.toString();
	return strResult;
    }

    public String createXmlResult(String baseURI, String type, String[] results) throws XMLStreamException {
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
	writer.writeStartDocument("UTF-8", "1.0");
	writer.writeStartElement(nsprefix, "param-set", nsuri);
	writer.writeNamespace(nsprefix, nsuri);
        for(int i = 0; i < results.length; i++){
	    writer.writeStartElement(nsprefix, "param", nsuri);
            writer.writeAttribute("name", type);
            writer.writeAttribute("name", results[i]);
	    writer.writeEndElement();
        }
	writer.writeEndElement();
	writer.writeEndDocument();
        writer.flush();
        sw.flush();
	StringBuffer sb = sw.getBuffer();
	String strResult = sb.toString();
	return strResult;
    }
    /**
     * Render errors as XML c:errors
     */
    public String createXmlError(String message) throws XMLStreamException {
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
	System.out.println(nsuri);
	writer.writeStartDocument("UTF-8", "1.0");
	writer.writeStartElement(nsprefix, "errors", nsuri);
	writer.writeNamespace(nsprefix, nsuri);
	writer.writeAttribute("code", "svn-error");
	writer.writeStartElement(nsprefix, "error", nsuri);
	writer.writeAttribute("code", "error");
	writer.writeCharacters(message);
	writer.writeEndElement();
	writer.writeEndElement();
	writer.writeEndDocument();
        writer.flush();
        sw.flush();
	StringBuffer sb = sw.getBuffer();
	String strResult = sb.toString();
	return strResult;
    }
}
