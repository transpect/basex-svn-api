package io.transpect.basex.extensions.subversion;

import java.io.File;

import java.util.HashMap;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler;
import org.tmatesoft.svn.core.SVNProperties;

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
public class XSvnPropSet {

  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnPropSet (String url, String username, String password, String propName, String propValue, String revision) {
    XSvnXmlReport report = new XSvnXmlReport();
    SVNRevision baseRevision;
    baseRevision = SVNRevision.HEAD;
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNWCClient client = connection.getClientManager().getWCClient();
      SVNProperties svnprops = new SVNProperties();
      if(connection.isRemote()){
        SVNURL svnurl = connection.getSVNURL();
        client.doSetProperty(svnurl, propName, SVNPropertyValue.create(propValue), baseRevision, "added prop: " + propName, svnprops, false, getISVNPropertyHandler());        
      } else {
		 File path = new File(url);
		 System.out.println("propName: " + propName);
		 client.doSetProperty(path, propName, SVNPropertyValue.create(propValue), false, SVNDepth.EMPTY, getISVNPropertyHandler(), null);
      }
      String[] results = {propName, propValue};
      FElem xmlResult = report.createXmlResult(url, "property", results);
      return xmlResult;
    } catch(SVNException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
	public FElem XSvnPropSet (String url, XQMap auth, String propName, String propValue, String revision) {
    XSvnXmlReport report = new XSvnXmlReport();
    SVNRevision svnRevision, svnPegRevision;
    if(revision == null || revision.trim().isEmpty()){
      svnRevision = svnPegRevision = SVNRevision.HEAD;
    } else {
      svnRevision = svnPegRevision = SVNRevision.parse(revision);
    }
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNWCClient client = connection.getClientManager().getWCClient();
      SVNProperties svnprops = new SVNProperties();
      if(connection.isRemote()){
        SVNURL svnurl = connection.getSVNURL();
        client.doSetProperty(svnurl, propName, SVNPropertyValue.create(propValue), svnRevision, "added prop: " + propName, svnprops, false, null);   
        //client.doSetRevisionProperty(svnurl, svnRevision, propName, SVNPropertyValue.create(propValue), false, null);        
      } else {
		 File path = new File(url);
		 System.out.println("propName: " + propName);
		 client.doSetProperty(path, propName, SVNPropertyValue.create(propValue), false, SVNDepth.EMPTY, getISVNPropertyHandler(), null);
      }
      String[] results = {propName, propValue};
      FElem xmlResult = report.createXmlResult(url, "property", results);
      return xmlResult;
    } catch(QueryException | SVNException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
  private ISVNPropertyHandler getISVNPropertyHandler(){
    return ISVNPropertyHandler.NULL;
  }
}
