package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn mkdir command as 
 * XML Calabash extension step for XProc. The class 
 * connects to a Subversion repository and creates 
 * one or multiple directories
 *
 * @see XSvnMkDir
 */
public class XSvnMkDir {

    public String XSvnMkDir (String url, String username, String password, String dir, Boolean parents, String commitMessage) {
        XSvnXmlReport report = new XSvnXmlReport();
        Boolean force = false;
        Boolean addAndMkdir = true;
        Boolean climbUnversionedParents = false;
        Boolean includeIgnored = false;
	try{
	    XSvnConnect connection = new XSvnConnect(url, username, password);
            SVNClientManager clientmngr = connection.getClientManager();
            String baseURI = connection.isRemote() ? url : connection.getPath();
            SVNCommitClient commitClient = clientmngr.getCommitClient();
            SVNWCClient client = clientmngr.getWCClient();
            String[] dirs = dir.split(" ");
            for(int i = 0; i < dirs.length; i++) {
                String currentDir = dirs[i];
                if( connection.isRemote() ){
                    SVNURL[] svnurl = { SVNURL.parseURIEncoded( url + "/" + currentDir )};
                    commitClient.doMkDir(svnurl, commitMessage);
                } else {
                    File path = new File( url + "/" + currentDir );
                    client.doAdd(path, force, addAndMkdir, climbUnversionedParents, SVNDepth.IMMEDIATES, includeIgnored, parents);
                }
            }
	    try {
		String xmlResult = report.createXmlResult(baseURI, "mkdir", dirs);
		return xmlResult;
	    } catch (XMLStreamException xse) {
		return xse.getMessage();
	    }
	} catch(SVNException|IOException svne) {
	    System.out.println(svne.getMessage());
	    try {
		String xmlError = report.createXmlError(svne.getMessage());
		return xmlError;
	    } catch (XMLStreamException xse) {
		return xse.getMessage();
	    }
	}
    }
}
