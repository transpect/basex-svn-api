package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Commits one or more paths and their children 
 * in a SVN working directory to the assigned repository.
 *
 */
public class XSvnCommit {
    public String XSvnCommit (String username, String password, String path, String commitMessage) {
        XSvnXmlReport report = new XSvnXmlReport();
	try{
            String[] paths = path.split(" ");
            File[] filePaths = new File[paths.length];
            for(int i = 0; i < paths.length; i++) {
                filePaths[i] = new File(paths[i]);
            }
	    XSvnConnect connection = new XSvnConnect(paths[0], username, password);
            SVNClientManager clientmngr = connection.getClientManager();
            String baseURI = connection.isRemote() ? paths[0] : connection.getPath();
            SVNCommitClient commitClient = clientmngr.getCommitClient();
            Boolean keepLocks = false;
            SVNProperties svnProps = new SVNProperties();
            String[] changelists = null;
            Boolean keepChangelist = false;
            Boolean force = false;
	    commitClient.doCommit(filePaths, keepLocks, commitMessage, svnProps, changelists, keepChangelist, force, SVNDepth.IMMEDIATES);
	    try {
		String xmlResult = report.createXmlResult(baseURI, "commit", paths);
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
