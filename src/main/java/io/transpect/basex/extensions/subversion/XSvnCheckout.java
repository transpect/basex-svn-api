package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Checkout a working copy of a SVN repository	
 *
 */
public class XSvnCheckout {
  
  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnCheckout(String url, String username, String password, String path, String revision, String depth) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(path, username, password);
      SVNClientManager clientmngr = connection.getClientManager();
      SVNUpdateClient updateClient = clientmngr.getUpdateClient();
      String baseURI = connection.isRemote() ? path : connection.getPath();
      SVNURL svnurl = SVNURL.parseURIEncoded( url );
      File checkoutPath = new File(path);
      SVNRevision svnRevision, svnPegRevision;
      boolean allowUnversionedObstructions = false;
      if(revision.trim().isEmpty()){
        svnRevision = svnPegRevision = SVNRevision.HEAD;
      } else {
        svnRevision = svnPegRevision = SVNRevision.parse(revision);
      }
      long checkoutRevision = updateClient.doCheckout(svnurl, checkoutPath, svnPegRevision, svnRevision, getSVNDepth(depth), allowUnversionedObstructions);
      HashMap<String, String> results = new HashMap<String, String>();
      results.put("repo", svnurl.toString());
      results.put("revision", String.valueOf(checkoutRevision));
      results.put("path", checkoutPath.getCanonicalFile().toString());
      FElem xmlResult = report.createXmlResult(results);
      return xmlResult;
    } catch(SVNException|IOException svne) {
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
	public FElem XSvnCheckout(String url, XQMap auth, String path, String revision, String depth) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(path, auth);
      SVNClientManager clientmngr = connection.getClientManager();
      SVNUpdateClient updateClient = clientmngr.getUpdateClient();
      String baseURI = connection.isRemote() ? path : connection.getPath();
      SVNURL svnurl = SVNURL.parseURIEncoded( url );
      File checkoutPath = new File(path);
      SVNRevision svnRevision, svnPegRevision;
      boolean allowUnversionedObstructions = false;
      if(revision.trim().isEmpty()){
        svnRevision = svnPegRevision = SVNRevision.HEAD;
      } else {
        svnRevision = svnPegRevision = SVNRevision.parse(revision);
      }
      long checkoutRevision = updateClient.doCheckout(svnurl, checkoutPath, svnPegRevision, svnRevision, getSVNDepth(depth), allowUnversionedObstructions);
      HashMap<String, String> results = new HashMap<String, String>();
      results.put("repo", svnurl.toString());
      results.put("revision", String.valueOf(checkoutRevision));
      results.put("path", checkoutPath.getCanonicalFile().toString());
      FElem xmlResult = report.createXmlResult(results);
      return xmlResult;
    } catch(QueryException | SVNException | IOException svne) {
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
  private SVNDepth getSVNDepth(String depth) {
    switch (depth) {
    case "empty":
      return SVNDepth.EMPTY;
    case "exclude":
      return SVNDepth.EXCLUDE;
    case "files":
      return SVNDepth.FILES;
    case "immediates":
      return SVNDepth.IMMEDIATES;
    case "unknown":
      return SVNDepth.UNKNOWN;
    default:
      return SVNDepth.INFINITY;
    }
  }
}
