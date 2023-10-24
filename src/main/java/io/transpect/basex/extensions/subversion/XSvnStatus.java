package io.transpect.basex.extensions.subversion;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
import io.transpect.basex.extensions.subversion.XSvnStatusHandler;
/**
 * This class provides the svn status command. Works
 * only with a working copy.
 *
 * @see XSvnConnect
 */
public class XSvnStatus {
  
	public FElem XSvnStatus (String url, XQMap auth) {
    XSvnXmlReport report = new XSvnXmlReport();
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNStatusClient client = connection.getClientManager().getStatusClient();
      XSvnStatusHandler handler = new XSvnStatusHandler();
      client.doStatus(new File(url),SVNRevision.HEAD,SVNDepth.INFINITY,false,false,false,false,handler,null);
      HashMap<String, String> results = getSVNStatus(handler.statusList);
      FElem xmlResult = report.createXmlResult(results);
      return xmlResult;
    } catch(QueryException | SVNException svne) {
      System.out.println(svne.getMessage());
      FElem xmlError = report.createXmlError(svne.getMessage());
      return xmlError;
    }
  }
  
  private HashMap<String, String> getSVNStatus(ArrayList<SVNStatus> statuses){
    HashMap<String, String> results = new HashMap<String, String>();
    
    HashMap<SVNStatusType, String> typeToString = new HashMap<SVNStatusType, String>();
    typeToString.put(SVNStatusType.CHANGED,"CHANGED");
    typeToString.put(SVNStatusType.CONFLICTED,"CONFLICTED");
    typeToString.put(SVNStatusType.CONFLICTED_UNRESOLVED,"CONFLICTED_UNRESOLVED");
    typeToString.put(SVNStatusType.INAPPLICABLE,"INAPPLICABLE");
    typeToString.put(SVNStatusType.LOCK_INAPPLICABLE,"LOCK_INAPPLICABLE");
    typeToString.put(SVNStatusType.LOCK_UNCHANGED,"LOCK_UNCHANGED");
    typeToString.put(SVNStatusType.LOCK_UNKNOWN,"LOCK_UNKNOWN");
    typeToString.put(SVNStatusType.LOCK_UNLOCKED,"LOCK_UNLOCKED");
    typeToString.put(SVNStatusType.MERGED,"MERGED");
    typeToString.put(SVNStatusType.MISSING,"MISSING");
    typeToString.put(SVNStatusType.NO_MERGE,"NO_MERGE");
    typeToString.put(SVNStatusType.OBSTRUCTED,"OBSTRUCTED");
    typeToString.put(SVNStatusType.STATUS_ADDED,"STATUS_ADDED");
    typeToString.put(SVNStatusType.STATUS_CONFLICTED,"STATUS_CONFLICTED");
    typeToString.put(SVNStatusType.STATUS_DELETED,"STATUS_DELETED");
    typeToString.put(SVNStatusType.STATUS_EXTERNAL,"STATUS_EXTERNAL");
    typeToString.put(SVNStatusType.STATUS_IGNORED,"STATUS_IGNORED");
    typeToString.put(SVNStatusType.STATUS_INCOMPLETE,"STATUS_INCOMPLETE");
    typeToString.put(SVNStatusType.STATUS_MERGED,"STATUS_MERGED");
    typeToString.put(SVNStatusType.STATUS_MISSING,"STATUS_MISSING");
    typeToString.put(SVNStatusType.STATUS_MODIFIED,"STATUS_MODIFIED");
    typeToString.put(SVNStatusType.STATUS_NAME_CONFLICT,"STATUS_NAME_CONFLICT");
    typeToString.put(SVNStatusType.STATUS_NONE,"STATUS_NONE");
    typeToString.put(SVNStatusType.STATUS_NORMAL,"STATUS_NORMAL");
    typeToString.put(SVNStatusType.STATUS_OBSTRUCTED,"STATUS_OBSTRUCTED");
    typeToString.put(SVNStatusType.STATUS_REPLACED,"STATUS_REPLACED");
    typeToString.put(SVNStatusType.STATUS_UNVERSIONED,"STATUS_UNVERSIONED");
    typeToString.put(SVNStatusType.UNCHANGED,"UNCHANGED");
    typeToString.put(SVNStatusType.UNKNOWN,"UNKNOWN");
    
    
    results.put("count","" + statuses.size());
    for(SVNStatus s : statuses){
			results.put(s.getFile().toString(), typeToString.get(s.getNodeStatus()));
		}
    return results;
  }
}