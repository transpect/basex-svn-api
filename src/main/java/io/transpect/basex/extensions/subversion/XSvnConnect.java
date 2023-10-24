package io.transpect.basex.extensions.subversion;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.value.item.*;
import org.basex.query.value.Value;
import org.basex.query.value.type.AtomType;
import org.basex.query.QueryError;
import org.basex.query.QueryException;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSLAuthentication;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;

/**
 * This class implements SVNKit and provides methods to connect to a 
 * Subversion repository. You need to instantiate the class with the 
 * corresponding Subversion URL, username and password.
 */

public class XSvnConnect {

  private enum AuthType{PASSWORD, PRIVKEY};
  private AuthType authType;
  
  private String url;
  private String username;
  private String password;
  private File keyFile;
  
  private SVNClientManager clientManager;
  private SVNRepository repository;
  
  /**
   * Creates an exception given an error message. 
   * 
   * @param url an URL which identifies a Subversion repository
   * @param username username (or a path to a private key) for the given repository
   * @param password password for the given repository or for the corresponding private key
   *
   */
  public XSvnConnect(String url, String username, String password) throws SVNException{
    this.url = url;
    this.password = password;
    
    /* if username contains a dot, it is considered to be a private key file path.*/
    if (username.matches("(.*)\\.(.*)")){
      this.authType = AuthType.PRIVKEY;
      print("using private key at " + username);
      this.username = getUsernameFromFile(username);
      this.keyFile = new File(username);
    } else {
      this.authType = AuthType.PASSWORD;
      print("using password authentication");
      this.username = username;
    }
    
    clientManager = init();
    
  }
  
  /**
  * Uses a map as below as authentication data. If cert-path is defined it is used as
  *
  * a private key file, otherwise username and password is used as is.
  * auth := map{'username':'heinz', 'cert-path': '/data/svn/cert/heinz.p12', 'password': '****'}
  *
  */  
  public XSvnConnect(String url, XQMap auth) throws SVNException, QueryException{    
    this.url = url;
    this.password = getStringFromMap(auth,"password");
    this.username = getStringFromMap(auth,"username");

    /* if cert-path is not empty, it is used as a path to a private key file.*/
    String certpath = getStringFromMap(auth,"cert-path");

    if (certpath != null && !certpath.isEmpty()){
      this.authType = AuthType.PRIVKEY;
      this.keyFile = new File(certpath);

    } else {
      this.authType = AuthType.PASSWORD;

    }
    clientManager = init();
  }
  
  /**
   * Returns a JavaKit SVNClientManager object.
   * 
   * @return a SVNClientManager object 
   *
   * @see org.tmatesoft.svn.core.wc.SVNClientManager
   */
  public SVNClientManager getClientManager() throws SVNException {
    return clientManager;
  }
  
  /**
   * Returns a JavaKit SVNRepository object. 
   *
   * @return a SVNRepository object
   *
   * @see org.tmatesoft.svn.core.io.SVNRepository
   */
  public SVNRepository getRepository() throws SVNException {
    repository = clientManager.createRepository(getSVNURL(), false);
    return repository;
  }
  
  /**
   * Returns true if the submitted href is a URL. 
   *
   * @return boolean
   *
   */
  public boolean isRemote(){
    return isURLBool(url);
  }
  
  private void print(String text){
    System.out.println("XSvnConnect: " + text);
  }
  
  /**
   * Returns a JavaKit SVNURL object. 
   * 
   * @return a SVNURL object
   * 
   * @see org.tmatesoft.svn.core.SVNURL
   */
  public SVNURL getSVNURL() throws SVNException {
    SVNURL svnurl = null;
    if(isURLBool(url)){
      svnurl = SVNURL.parseURIEncoded(url);
    } else {
      try {
        File path = new File(url);
        svnurl = SVNURL.fromFile(new File(path.getCanonicalPath()));
      } catch ( IOException e) {
        print(e.getMessage());
      }
    }
    return svnurl;
  }
  
  public String getPath() throws IOException {
    File path = new File(url);
    return path.getCanonicalPath();
  }
  
  private SVNClientManager init() throws SVNException{
    //Set up connection protocols support:
    DAVRepositoryFactory.setup();             // http
    SVNRepositoryFactoryImpl.setup();         // svn, svn+xxx (svn+ssh in particular)
    FSRepositoryFactory.setup();              // file
    
    SVNClientManager clientManager;
    
    File kitConf = new File(SVNWCUtil.getDefaultConfigurationDirectory().getParentFile(),"/.svnkit/");
    DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(kitConf, true);
    if(this.username == null || this.username.isEmpty()){
      print("INFO: username is empty; use svn auth");
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
      clientManager = SVNClientManager.newInstance(options, authManager);
      return clientManager;
      
    } else {
      if(isURLBool(url)){
        switch (this.authType){
          case PASSWORD:
            BasicAuthenticationManager authManagerPW = new BasicAuthenticationManager(this.username, this.password);  
				    clientManager = SVNClientManager.newInstance(options, authManagerPW);
            return clientManager;
            
          case PRIVKEY:
            SVNSSLAuthentication auth = new SVNSSLAuthentication(this.keyFile, this.password, false);
            SVNAuthentication auths[] = new SVNAuthentication[]{auth};
            BasicAuthenticationManager authManagerPK = new BasicAuthenticationManager(auths);  
            clientManager = SVNClientManager.newInstance(options, authManagerPK);
            return clientManager;
        }
      }
      else
      {
        BasicAuthenticationManager authManager = new BasicAuthenticationManager(this.username, this.password);  
        clientManager = SVNClientManager.newInstance(options, authManager);
        SVNWCClient client = clientManager.getWCClient();
        return clientManager;
      }
    }
    return null;
  }
  
  private String getUsernameFromFile(String input){
    String regex = "^(.{8})";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);
    if (matcher.matches()){
      return matcher.group(1);
    }
    return null;
  }
  
  private String getStringFromMap(XQMap map, String key) throws QueryException{
      Str strKey = Str.get(key);
    if (map.contains(strKey, null)){
      Value val = map.get(strKey, null);
      String result = new String(val.toString());
      return result.replace("\"","");
    } else {
      return null;
    }
  }
  
  private boolean isURLBool(String href){
    return href.startsWith("http://") || href.startsWith("https://");
  }
}
