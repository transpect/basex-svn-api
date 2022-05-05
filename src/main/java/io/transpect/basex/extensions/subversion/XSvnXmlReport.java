package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;

/**
 * Returns reports or errors as XML (currently only
 * string) that needs to be parsed with parse-xml()
 */
public class XSvnXmlReport {

  final static String nsprefix = "c";
  final static String nsuri = "http://www.w3.org/ns/xproc-step";
    
  /**
   * Render a HashMap as XML c:param-set
   */
  public static FElem createXmlResult(HashMap<String, String> results) {
    FElem xmlResult = new FElem(nsprefix, "param-set", nsuri);
    for(String key:results.keySet()) {
      FElem paramElement = new FElem(nsprefix, "param", nsuri);
      paramElement.add("name", key);
      paramElement.add("value", results.get(key));
      xmlResult.add(paramElement);
    }
    return xmlResult;
  }
  public static FElem createXmlResult(String baseURI, String type, String[] results) {
    FElem xmlResult = new FElem(nsprefix, "param-set", nsuri);
    xmlResult.add("xml:base", baseURI);
    for(int i = 0; i < results.length; i++){
      FElem paramElement = new FElem(nsprefix, "param", nsuri);
      paramElement.add("name", type);
      paramElement.add("value", results[i]);
      xmlResult.add(paramElement);
    }
    return xmlResult;
  }
  public static FElem createXmlDirTree(File path, Boolean recursive) throws SVNException {
    FElem element = new FElem(nsprefix, "files", nsuri);
    element.add("xml:base", path.toURI().toString());
    listEntries(path, recursive, element); 
    return element;
  }
  public static FElem listEntries(File path, Boolean recursive, FElem dirElement) throws SVNException {
    File[] dirList = path.listFiles();
    if( path.isDirectory() && dirList != null ) {
      for (File child : dirList ) {
        String elementName = child.isDirectory() ? "directory" : "file";
        FElem element = new FElem(nsprefix, elementName, nsuri);
        element.add("name", child.getName());
        if(!child.isDirectory()) {
          element.add("size", String.valueOf(child.length() / 1024));
        }
        if(child.isDirectory() && recursive) {
          listEntries(child, recursive, element);
        }
        dirElement.add(element);
      }
    }
    return dirElement;
  }
  public static FElem createXmlDirTree(String url, SVNRepository repository, Boolean recursive) throws SVNException {
    FElem xmlResult = new FElem(nsprefix, "files", nsuri);
    xmlResult.add("xml:base", url);
    listEntries(repository, "", recursive, xmlResult);
    return xmlResult;
  }
  public static FElem listEntries(SVNRepository repository, String path, Boolean recursive, FElem dirElement) throws SVNException {
    Collection entries = repository.getDir(path, -1 , null , (Collection) null);
    Iterator iterator = entries.iterator();
    String repositoryRootURL = repository.getRepositoryRoot(true).toString();
    while (iterator.hasNext()) {
      SVNDirEntry entry = (SVNDirEntry) iterator.next( );
      String elementName = entry.getKind() == SVNNodeKind.DIR ? "directory" : "file";
      String entryURL = entry.getURL().toString();
      String entryRelPath = entryURL.replace(repositoryRootURL, "");
      FElem element = new FElem(nsprefix, elementName, nsuri);
      element.add("name", entry.getName());
      element.add("author", entry.getAuthor());
      element.add("date", entry.getDate().toString());
      element.add("revision", String.valueOf(entry.getRevision()));
      if ( entry.getKind() == SVNNodeKind.FILE ) {
          SVNLock lock = repository.getLock( entryRelPath );
          element.add("size", String.valueOf( entry.getSize() ));
          if( lock != null ) {
            element.add("lock-id", lock.getID());
            element.add("lock-path", lock.getPath());
            element.add("lock-owner", lock.getOwner());
            element.add("lock-created", lock.getCreationDate().toString());
            if( lock.getExpirationDate() != null ) {
              element.add("lock-expires", lock.getExpirationDate().toString());
            }
            if( lock.getComment() != null ) {
              element.add("lock-comment", lock.getComment());
            }
          }
      }
      if (entry.getKind() == SVNNodeKind.DIR && recursive == true) {
        listEntries(repository, (path.equals( "" )) ? entry.getName( ) : path + "/" + entry.getName( ), recursive, element);
      }
      dirElement.add(element);
    }
    return dirElement;
  }
  /**
   * Render errors as XML c:errors
   */
  public static FElem createXmlError(String message) {
    FElem xmlResult = new FElem(nsprefix, "errors", nsuri);
    xmlResult.add("code", "svn-error");
    FElem errorElement = new FElem(nsprefix, "error", nsuri);
    errorElement.add("code", "error");
    errorElement.add(message);
    xmlResult.add(errorElement);
    return xmlResult;
  }
}
