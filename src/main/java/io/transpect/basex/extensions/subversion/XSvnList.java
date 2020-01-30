package io.transpect.basex.extensions.subversion;

import java.util.Collection;
import java.util.Iterator;

import java.io.File;
import java.io.StringWriter;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Performs svn ls as XML Calabash extension step for
 * XProc. The class connects to a Subversion repository and
 * provides the results as XML document.
 *
 * @see XSvnConnect
 */
public class XSvnList {

    final static String nsprefix = "c";
    final static String nsuri = "http://www.w3.org/ns/xproc-step";
    
    public String XSvnList (String url, String username, String password, Boolean recursive) {
        XSvnXmlReport report = new XSvnXmlReport();
	try{
	    XSvnConnect connection = new XSvnConnect(url, username, password);
	    SVNRepository repository = connection.getRepository();
	    try {
		String xmlResult = createXmlDirTree(repository, "", recursive);
		return xmlResult;
	    } catch (XMLStreamException xse) {
		return xse.getMessage();
	    }
	} catch(SVNException svne) {
	    System.out.println(svne.getMessage());
	    try {
		String xmlError = report.createXmlError(svne.getMessage());
		return xmlError;
            } catch (XMLStreamException xse) {
                return xse.getMessage();
            }
	}
    }    
    public static String createXmlDirTree(SVNRepository repository, String path, Boolean recursive) throws SVNException, XMLStreamException {
	XMLOutputFactory factory = XMLOutputFactory.newInstance();
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
	writer.writeStartDocument("UTF-8", "1.0");
	writer.writeStartElement(nsprefix, "files", nsuri);
	writer.writeNamespace(nsprefix, nsuri);
	listEntries(repository, "", recursive, writer);
	writer.writeEndElement();
	writer.writeEndDocument();
	writer.flush();
	sw.flush();
	StringBuffer sb = sw.getBuffer();
	String strResult = sb.toString();
	return strResult;
    }
    public static XMLStreamWriter listEntries(SVNRepository repository, String path, Boolean recursive, XMLStreamWriter writer) throws SVNException, XMLStreamException {
	Collection entries = repository.getDir( path, -1 , null , (Collection) null );
	Iterator iterator = entries.iterator( );
	while ( iterator.hasNext( ) ) {
	    SVNDirEntry entry = (SVNDirEntry) iterator.next( );
	    String elementName = entry.getKind() == SVNNodeKind.DIR ? "directory" : "file";
	    writer.writeStartElement(nsprefix, elementName, nsuri);
	    writer.writeAttribute("name", entry.getName());
	    writer.writeAttribute("author", entry.getAuthor());
	    writer.writeAttribute("date", entry.getDate().toString());
	    writer.writeAttribute("revision", String.valueOf(entry.getRevision()));
	    writer.writeAttribute("size", String.valueOf(entry.getSize()));
	    if ( entry.getKind() == SVNNodeKind.DIR && recursive == true ) {
		listEntries(repository, (path.equals( "" )) ? entry.getName( ) : path + "/" + entry.getName( ), recursive, writer);
	    }
	    writer.writeEndElement();
	}
	return writer;
    }
}
