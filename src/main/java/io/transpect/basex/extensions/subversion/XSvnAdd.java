package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

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
	
  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnAdd(String url, String username, String password, String path, Boolean parents) {
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
      FElem xmlResult = report.createXmlResult(baseURI, "add", paths);
      return xmlResult;
    } catch(SVNException|IOException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
	public FElem XSvnAdd(String url, XQMap auth, String path, Boolean parents) {
    XSvnXmlReport report = new XSvnXmlReport();
    Boolean force = false;
    Boolean addAndMkdir = false;
    Boolean climbUnversionedParents = false;
    Boolean includeIgnored = false;
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNClientManager clientmngr = connection.getClientManager();
      String baseURI = connection.isRemote() ? url : connection.getPath();
      SVNWCClient client = clientmngr.getWCClient();
      String[] paths = path.split(" ");
      for(int i = 0; i < paths.length; i++) {
        File currentPath = new File( url + "/" + paths[i]);
        client.doAdd(currentPath, force, addAndMkdir, climbUnversionedParents, SVNDepth.IMMEDIATES, includeIgnored, parents);
      }
      FElem xmlResult = report.createXmlResult(baseURI, "add", paths);
      return xmlResult;
    } catch(QueryException | SVNException|IOException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
}
