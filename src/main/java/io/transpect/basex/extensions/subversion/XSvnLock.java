package io.transpect.basex.extensions.subversion;

import java.util.Collection;
import java.util.Iterator;

import java.io.File;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import org.basex.query.value.node.FElem;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Performs svn lock and unlock as XML Calabash extension step for
 * XProc. The class connects to a Subversion repository and
 * provides the results as XML document.
 *
 * @see XSvnLock
 */

public class XSvnLock {
  public FElem XSvnLock ( String url, String username, String password, String paths, Boolean unlock, Boolean breakLock, String message ) {

    String[] pathsArr = paths.split(" ");
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(url, username, password);
      SVNClientManager clientmngr = connection.getClientManager();
      SVNWCClient client = clientmngr.getWCClient();
      for (int i = 0; i < pathsArr.length; i++){
        pathsArr[i] = url + "/" + pathsArr[i];
      }
      if(connection.isRemote()){
        SVNURL[] svnUrlArr = new SVNURL[pathsArr.length];
        for (int i = 0; i < pathsArr.length; i++){
          svnUrlArr[i] = SVNURL.parseURIEncoded(pathsArr[i]);
        }
        if(unlock){
          client.doUnlock(svnUrlArr, breakLock);
        } else {
          client.doLock(svnUrlArr, breakLock, message);
        }
      } else {
        File[] filesArr = null;
        for (int i = 0; i < pathsArr.length; i++){
          filesArr[i] = new File(pathsArr[i]);
        }
        if(unlock){
          client.doUnlock(filesArr, breakLock);
        } else {
          client.doLock(filesArr, breakLock, message);
        }
      }
      FElem xmlResult = report.createXmlResult(url, ( unlock ? "unlock" : "lock" ), pathsArr);
      return xmlResult;
    } catch (SVNException svne){
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
}
