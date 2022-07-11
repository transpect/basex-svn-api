package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;
import org.tmatesoft.svn.core.wc.admin.SVNAdminPath;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.QueryException;
import org.basex.query.value.item.*;
import org.basex.query.value.Value;

import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * Performs svn list. The class connects to a Subversion repository
 * and provides the results as XML directory tree.
 *
 * @see XSvnConnect
 */
public class XSvnLogEntryHandler implements ISVNLogEntryHandler {
  
  final static String nsprefix = "c";
  final static String nsuri = "http://www.w3.org/ns/xproc-step";
  
  public String Result;
  public FElem XmlResult;
  private String author;
  private String changedPaths;
  private String date;
  private String message;
  private String revision;
  private String revisionProperties;
  private String hash;
  
  public XSvnLogEntryHandler()
  {
    this.XmlResult = new FElem("log");
  }
	
	public static FElem createXmlFromPaths(Map<String, SVNLogEntryPath> results) {
    FElem xmlResult = new FElem(nsprefix, "changedPaths", nsuri);
		xmlResult.add("cahnges", "" + results.size());
    for(String key:results.keySet()) {
			FElem pathElement = new FElem(nsprefix, "changedPath", nsuri);
			SVNLogEntryPath path = results.get(key);
			
      pathElement.add("name", key);
      pathElement.add("path", path.getPath());
      pathElement.add("type", "" + path.getType());
      xmlResult.add(pathElement);
    }
    return xmlResult;
  }
  
  public void handleLogEntry (SVNLogEntry entry){
    
    Result = Result + '\n' + entry.toString();
    
    String elementName = "logEntry";
    FElem element = new FElem(nsprefix, elementName, nsuri);
    element.add("author",entry.getAuthor());
    element.add("date",String.valueOf(entry.getDate()));
    element.add("message",entry.getMessage());
    element.add("revision",Long.toString(entry.getRevision()));
		FElem changedPaths = createXmlFromPaths(entry.getChangedPaths());
		element.add(changedPaths);
		
    XmlResult.add(element);
  }
}
