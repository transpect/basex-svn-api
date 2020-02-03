package io.transpect.basex.extensions.subversion;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import org.basex.query.value.node.FElem;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Performs svn list. The class connects to a Subversion repository
 * and provides the results as XML directory tree.
 *
 * @see XSvnConnect
 */
public class XSvnList {
    
  public FElem XSvnList (String url, String username, String password, Boolean recursive) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNRepository repository = connection.getRepository();
      FElem xmlResult = report.createXmlDirTree(url, repository, recursive);
      return xmlResult;
    } catch(SVNException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }    
}
