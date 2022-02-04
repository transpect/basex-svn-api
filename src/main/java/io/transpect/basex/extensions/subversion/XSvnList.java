package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Performs svn list. The class connects to a Subversion repository
 * and provides the results as XML directory tree.
 *
 * @see XSvnConnect
 */
public class XSvnList {
  
  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnList (String url, String username, String password, Boolean recursive) {
    XSvnXmlReport report = new XSvnXmlReport();
    FElem xmlResult;
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      if(connection.isRemote()){
        SVNRepository repository = connection.getRepository();
        xmlResult = report.createXmlDirTree(url, repository, recursive);
      } else {
        File path = new File(new File(url).getCanonicalPath());
        xmlResult = report.createXmlDirTree(path, recursive);
        return xmlResult; 
      }
      return xmlResult;
    } catch(SVNException | IOException e) {
      System.out.println(e.getMessage());
      FElem xmlError = report.createXmlError(e.getMessage());
      return xmlError;
    }
  }
  public FElem XSvnList (String url, XQMap auth, Boolean recursive) {
    XSvnXmlReport report = new XSvnXmlReport();
    FElem xmlResult;
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      if(connection.isRemote()){
        SVNRepository repository = connection.getRepository();
        xmlResult = report.createXmlDirTree(url, repository, recursive);
      } else {
        File path = new File(new File(url).getCanonicalPath());
        xmlResult = report.createXmlDirTree(path, recursive);
        return xmlResult; 
      }
      return xmlResult;
    } catch(QueryException | SVNException | IOException e) {
      System.out.println(e.getMessage());
      FElem xmlError = report.createXmlError(e.getMessage());
      return xmlError;
    }
  }	
}
