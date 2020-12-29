package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.SVNPropertyValue;

import org.basex.query.value.node.FElem;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Getting a Property from an SVN repository
 *
 */
public class XSvnPropget {

  public FElem XSvnPropget(String url, String username, String password, String path, String revision, String propname) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(path, username, password);
      SVNClientManager clientmngr = connection.getClientManager();
      SVNWCClient propClient = clientmngr.getWCClient();
      String baseURI = connection.isRemote() ? path : connection.getPath();
      SVNURL svnurl = SVNURL.parseURIEncoded( url );
      File propPath = new File(path);
      SVNRevision svnRevision, svnPegRevision;
      boolean allowUnversionedObstructions = false;
      if(revision.trim().isEmpty()){
        svnRevision = svnPegRevision = SVNRevision.HEAD;
      } else {
        svnRevision = svnPegRevision = SVNRevision.parse(revision);
      }            
      SVNPropertyData propdata = propClient.doGetProperty(propPath,
                                                 propname,
                                                 svnPegRevision,
                                                 svnRevision
												 );
      HashMap<String, String> results = new HashMap<String, String>();
	  results.put("propName", propdata.getName());
	  results.put("propValue", propdata.getValue().toString());
      results.put("repo", svnurl.toString());
      results.put("revision", String.valueOf(revision));
      FElem xmlResult = report.createXmlResult(results);
      return xmlResult;
    } catch(SVNException|IOException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
}
