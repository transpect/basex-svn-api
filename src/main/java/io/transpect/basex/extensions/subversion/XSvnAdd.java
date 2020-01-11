package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn add command as 
 * XQuery function for BaseX. The class  
 * adds one or multiple files in a SVN working copy
 *
 * @see XSvnAdd
 */
public class XSvnAdd {
    public String XSvnAdd(String url, String username, String password, String path, Boolean parents) {
        XSvnXmlReport report = new XSvnXmlReport();
        Boolean force = false;
        Boolean addAndMkdir = false;
        Boolean climbUnversionedParents = false;
        Boolean includeIgnored = false;
	try{
	    XSvnConnect connection = new XSvnConnect(url, username, password);
            SVNClientManager clientmngr = connection.getClientManager();
            String baseURI = connection.isRemote() ? url : connection.getPath();
            SVNWCClient client = clientmngr.getWCClient();
            String[] paths = path.split(" ");
            for(int i = 0; i < paths.length; i++) {
                File currentPath = new File( url + "/" + paths[i]);
                client.doAdd(currentPath, force, addAndMkdir, climbUnversionedParents, SVNDepth.IMMEDIATES, includeIgnored, parents);
            }
	    try {
		String xmlResult = report.createXmlResult(baseURI, "add", paths);
		return xmlResult;
	    } catch (XMLStreamException xse) {
		return xse.getMessage();
	    }
	} catch(SVNException|IOException svne) {
	    System.out.println(svne.getMessage());
	    try{
		String xmlError = report.createXmlError(svne.getMessage());
		return xmlError;
	    } catch (XMLStreamException xse) {
		return xse.getMessage();
	    }
	}
    }
}
