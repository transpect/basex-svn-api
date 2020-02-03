package io.transpect.basex.extensions.subversion;

import java.util.HashMap;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNInfo;

import org.basex.query.value.node.FElem;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn info command. The class 
 * connects to a Subversion repository and provides 
 * the results as XML document.
 *
 * @see XSvnConnect
 */
public class XSvnInfo {

  public FElem XSvnInfo (String url, String username, String password) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNWCClient client = connection.getClientManager().getWCClient();
      SVNInfo info;
      if(connection.isRemote()){
        info = client.doInfo(connection.getSVNURL(), SVNRevision.HEAD, SVNRevision.HEAD);
      } else {
        info = client.doInfo(new File(url), SVNRevision.HEAD);
      }
      HashMap<String, String> results = getSVNInfo(info);
      FElem xmlResult = report.createXmlResult(results);
      return xmlResult;
    } catch(SVNException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
  /**
   * Create a HashMap from a SVNInfo object
   */
  private HashMap<String, String> getSVNInfo(SVNInfo info){
    HashMap<String, String> results = new HashMap<String, String>();
    results.put("url", info.getURL().toString());
    results.put("author", info.getAuthor());
    results.put("date", info.getCommittedDate().toString());
    results.put("uuid", info.getRepositoryUUID());
    results.put("rev", String.valueOf(info.getCommittedRevision().getNumber()));
    results.put("path", info.getPath());
    results.put("root-url", info.getRepositoryRootURL().toString());
    results.put("nodekind", info.getKind().toString());
    return results;
  }
}
