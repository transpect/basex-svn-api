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
 * This class provides the svn delete command as 
 * XML Calabash extension step for XProc. The class 
 * connects to a Subversion remote repository or a
 * working copy and attempts to delete the selected paths.
 *
 * @see XSvnDelete
 */
public class XSvnDelete {
    public String XSvnDelete ( String url, String username, String password, String path, Boolean force, String commitMessage ) {
        Boolean dryRun = false;
        XSvnXmlReport report = new XSvnXmlReport(); 
	try{
	    XSvnConnect connection = new XSvnConnect(url, username, password);
            SVNClientManager clientmngr = connection.getClientManager();
            String baseURI = connection.isRemote() ? url : connection.getPath();
            SVNCommitClient commitClient = clientmngr.getCommitClient();
            SVNWCClient client = clientmngr.getWCClient();
            String[] paths = path.split(" ");
            for(int i = 0; i < paths.length; i++) {
                String currentPath = paths[i];
                if( connection.isRemote() ){
                    SVNURL[] svnurl = { SVNURL.parseURIEncoded( url + "/" + currentPath )};
                    commitClient.doDelete(svnurl, commitMessage);
                } else {
                    File fullPath = new File( url + "/" + currentPath );
                    client.doDelete(fullPath, force, dryRun);
                }
            }
	    try {
		String xmlResult = report.createXmlResult(baseURI, "delete", paths);
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
