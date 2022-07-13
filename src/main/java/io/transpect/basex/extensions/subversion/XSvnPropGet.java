package io.transpect.basex.extensions.subversion;

import java.io.File;

import java.util.HashMap;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNPropertyData;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn propget command. The class 
 * connects to a Subversion repository and provides 
 * the results as XML document.
 *
 * @see XSvnConnect
 * @see XSvnXmlReport
 */
public class XSvnPropGet {
	
  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnPropGet (String url, String username, String password, String property, String revision) {
    XSvnXmlReport report = new XSvnXmlReport();
    SVNRevision svnRevision, svnPegRevision;
    SVNPropertyData propData;
    try{
      if(revision.trim().isEmpty()){
        svnRevision = svnPegRevision = SVNRevision.HEAD;
      } else {
        svnRevision = svnPegRevision = SVNRevision.parse(revision);
      }
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNWCClient client = connection.getClientManager().getWCClient();
      if(connection.isRemote()){
        propData = client.doGetProperty(connection.getSVNURL(), property, svnPegRevision, svnRevision);
      } else {
        propData = client.doGetProperty(new File(url), property, svnPegRevision, svnRevision);
      }
      if(propData != null){
        HashMap<String, String> results = new HashMap<String, String>();
        results.put("property", propData.getName());
        results.put("value", propData.getValue().toString());
        results.put("repo", url);
        results.put("revision", svnRevision.toString());
        FElem xmlResult = report.createXmlResult(results);
        return xmlResult;
      } else {
        String msg = "SVN property cannot be found at this node (" + url + ")!";
        //System.out.println("[ERROR] " + msg);
        FElem propError = report.createXmlError(msg);
        return propError;
      }
    } catch(SVNException svne) {
      //System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
	public FElem XSvnPropGet (String url, XQMap auth, String property, String revision) {
    XSvnXmlReport report = new XSvnXmlReport();
    SVNRevision svnRevision, svnPegRevision;
    SVNPropertyData propData;
    try{
      if(revision.trim().isEmpty()){
        svnRevision = svnPegRevision = SVNRevision.HEAD;
      } else {
        svnRevision = svnPegRevision = SVNRevision.parse(revision);
      }
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNWCClient client = connection.getClientManager().getWCClient();
      if(connection.isRemote()){
        propData = client.doGetProperty(connection.getSVNURL(), property, svnPegRevision, svnRevision);
      } else {
        propData = client.doGetProperty(new File(url), property, svnPegRevision, svnRevision);
      }
      if(propData != null){
        HashMap<String, String> results = new HashMap<String, String>();
        results.put("property", propData.getName());
        results.put("value", propData.getValue().toString());
        results.put("repo", url);
        results.put("revision", svnRevision.toString());
        FElem xmlResult = report.createXmlResult(results);
        return xmlResult;
      } else {
        String msg = "SVN property cannot be found at this node (" + url + ")!";
        //System.out.println("[ERROR] " + msg);
        FElem propError = report.createXmlError(msg);
        return propError;
      }
    } catch(QueryException | SVNException svne) {
      //System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
}
