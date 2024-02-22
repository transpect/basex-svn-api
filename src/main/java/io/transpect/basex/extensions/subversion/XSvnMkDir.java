package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import java.util.Date;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.SVNCommitInfo;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn mkdir command. The class 
 * connects to a Subversion repository and creates 
 * one or multiple directories
 *
 * @see XSvnMkDir
 */
public class XSvnMkDir {
	
  /**
  * @deprecated  username/password login replaced with XQMap auth
  */
  public FElem XSvnMkDir (String url, String username, String password, String dir, Boolean parents, String commitMessage) {
    XSvnXmlReport report = new XSvnXmlReport();
    Boolean force = false;
    Boolean addAndMkdir = true;
    Boolean climbUnversionedParents = false;
    Boolean includeIgnored = false;
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNClientManager clientmngr = connection.getClientManager();
      String baseURI = connection.isRemote() ? url : connection.getPath();
      SVNCommitClient commitClient = clientmngr.getCommitClient();
      SVNWCClient client = clientmngr.getWCClient();
      String[] dirs = dir.split(" ");
      for(int i = 0; i < dirs.length; i++) {
        String currentDir = dirs[i];
        if( connection.isRemote() ){
          SVNURL[] svnurl = { SVNURL.parseURIEncoded( url + "/" + currentDir )};
          commitClient.doMkDir(svnurl, commitMessage);
        } else {
          File path = new File( url + "/" + currentDir );
          client.doAdd(path, force, addAndMkdir, climbUnversionedParents, SVNDepth.IMMEDIATES, includeIgnored, parents);
        }
      }
      FElem xmlResult = report.createXmlResult(baseURI, "mkdir", dirs);
      return xmlResult;
    } catch(SVNException|IOException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
	public FElem XSvnMkDir (String url, XQMap auth, String dir, Boolean parents, String commitMessage) {
    XSvnXmlReport report = new XSvnXmlReport();
    Boolean force = false;
    Boolean addAndMkdir = true;
    Boolean climbUnversionedParents = false;
    Boolean includeIgnored = false;
    SVNCommitInfo info = new SVNCommitInfo(666, "author", new Date());
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNClientManager clientmngr = connection.getClientManager();
      String baseURI = connection.isRemote() ? url : connection.getPath();
      String[] dirs = dir.split(" ");
      SVNCommitClient commitClient = clientmngr.getCommitClient();
      SVNWCClient client = clientmngr.getWCClient();
      for(int i = 0; i < dirs.length; i++) {
        String currentDir = dirs[i];
        if( connection.isRemote() ){
          SVNURL[] svnurl = { SVNURL.parseURIEncoded( url + "/" + currentDir )};
          info = commitClient.doMkDir(svnurl, commitMessage, null, parents);
        } else {
          File path = new File( url + "/" + currentDir );
          client.doAdd(path, force, addAndMkdir, climbUnversionedParents, SVNDepth.IMMEDIATES, includeIgnored, parents);
          File[] paths = {path};
          if (commitMessage != ""){
            commitClient.doCommit(paths, false, commitMessage, true, false);
          }
        }
      }
      FElem xmlResult = report.createXmlResult(baseURI, "mkdir v1.6" + info.toString(), dirs);
      return xmlResult;
    } catch(QueryException | SVNException|IOException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
}
