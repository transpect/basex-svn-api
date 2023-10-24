package io.transpect.basex.extensions.subversion;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * This class provides the svn status handler. 
 *
 * @see XSvnStatus
 */

public class XSvnStatusHandler implements ISVNStatusHandler{
  
  public ArrayList<SVNStatus> statusList = new ArrayList<SVNStatus>();
  
  public void handleStatus(SVNStatus status){
    statusList.add(status);
  }
}
