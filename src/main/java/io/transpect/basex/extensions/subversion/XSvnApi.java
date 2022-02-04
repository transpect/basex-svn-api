package io.transpect.basex.extensions.subversion;

import org.basex.query.value.node.FElem;
import org.basex.query.value.map.XQMap;
import org.basex.query.value.item.*;
import org.basex.query.value.type.AtomType;

import io.transpect.basex.extensions.subversion.XSvnAdd;
import io.transpect.basex.extensions.subversion.XSvnCopy;
import io.transpect.basex.extensions.subversion.XSvnCheckout;
import io.transpect.basex.extensions.subversion.XSvnCommit;
import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnCopy;
import io.transpect.basex.extensions.subversion.XSvnDelete;
import io.transpect.basex.extensions.subversion.XSvnInfo;
import io.transpect.basex.extensions.subversion.XSvnList;
import io.transpect.basex.extensions.subversion.XSvnLock;
import io.transpect.basex.extensions.subversion.XSvnMkDir;
import io.transpect.basex.extensions.subversion.XSvnPropGet;
import io.transpect.basex.extensions.subversion.XSvnPropSet;
import io.transpect.basex.extensions.subversion.XSvnUpdate;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * 
 * XSvn BaseX extension
 *
 */
public class XSvnApi  {
	
	private XQMap createauth(String username, String password){
		Str usernamevalue = Str.get(username);
		Str usernamekey = Str.get("username");
		Str passwordvalue = Str.get(password);
		Str passwordkey = Str.get("password");
		try {
			XQMap result = XQMap.entry(usernamekey, usernamevalue, null);
			result.put(passwordkey,passwordvalue, null);
			return result;
		}
		catch (Exception e){
			System.out.println(e.toString());
			return null;
		}
	}
	
  public FElem info (String url, String username, String password) {
    XSvnInfo info = new XSvnInfo();
		XQMap auth = createauth(username, password);
    return info.XSvnInfo(url, auth);
  }
  public FElem info (String url, XQMap auth) {
    XSvnInfo info = new XSvnInfo();
    return info.XSvnInfo(url, auth);
  }
	
  public FElem list (String url, String username, String password, Boolean recursive) {
    XSvnList list = new XSvnList();
		XQMap auth = createauth(username, password);
    return list.XSvnList(url, auth, recursive);
  }
	public FElem list (String url, XQMap auth, Boolean recursive) {
    XSvnList list = new XSvnList();
    return list.XSvnList(url, auth, recursive);
  }
	
  public FElem checkout (String url, String username, String password, String path, String revision, String depth) {
    XSvnCheckout checkout = new XSvnCheckout();
		XQMap auth = createauth(username, password);
    return checkout.XSvnCheckout(url, auth, path, revision, depth);
  }
	public FElem checkout (String url, XQMap auth, String path, String revision, String depth) {
    XSvnCheckout checkout = new XSvnCheckout();
		return checkout.XSvnCheckout(url, auth, path, revision, depth);
  }
	
  public FElem copy (String url, String username, String password, String path, String target, String commitMessage) {
    XSvnCopy copy = new XSvnCopy();
		XQMap auth = createauth(username, password);
    return copy.XSvnCopy(url, auth, path, target, false, commitMessage);
  }
	public FElem copy (String url, XQMap auth, String path, String target, String commitMessage) {
    XSvnCopy copy = new XSvnCopy();
		return copy.XSvnCopy(url, auth, path, target, false, commitMessage);
  }
	
  public FElem move (String url, String username, String password, String path, String target, String commitMessage) {
    XSvnCopy move = new XSvnCopy();
		XQMap auth = createauth(username, password);
    return move.XSvnCopy(url, auth, path, target, true, commitMessage);
  }
	public FElem move (String url, XQMap auth, String path, String target, String commitMessage) {
    XSvnCopy move = new XSvnCopy();
		return move.XSvnCopy(url, auth, path, target, true, commitMessage);
  }
	
  public FElem mkdir (String url, String username, String password, String dir, Boolean parents, String commitMessage) {
    XSvnMkDir mkdir = new XSvnMkDir();
		XQMap auth = createauth(username, password);
    return mkdir.XSvnMkDir(url, auth, dir, parents, commitMessage);
  }
	public FElem mkdir (String url, XQMap auth, String dir, Boolean parents, String commitMessage) {
    XSvnMkDir mkdir = new XSvnMkDir();
		return mkdir.XSvnMkDir(url, auth, dir, parents, commitMessage);
  }
	
  public FElem add (String url, String username, String password, String path, Boolean parents) {
    XSvnAdd add = new XSvnAdd();
		XQMap auth = createauth(username, password);
    return add.XSvnAdd(url, auth, path, parents);
  }
	public FElem add (String url, XQMap auth, String path, Boolean parents) {
    XSvnAdd add = new XSvnAdd();
		return add.XSvnAdd(url, auth, path, parents);
  }
	
  public FElem delete (String url, String username, String password, String path, Boolean force, String commitMessage) {
    XSvnDelete delete = new XSvnDelete();
		XQMap auth = createauth(username, password);
    return delete.XSvnDelete(url, auth, path, force, commitMessage);
  }
	public FElem delete (String url, XQMap auth, String path, Boolean force, String commitMessage) {
    XSvnDelete delete = new XSvnDelete();
		return delete.XSvnDelete(url, auth, path, force, commitMessage);
  }
	
  public FElem update (String username, String password, String path, String revision) {
    XSvnUpdate update = new XSvnUpdate();
		XQMap auth = createauth(username, password);
    return update.XSvnUpdate(auth, path, revision);
  }
	public FElem update (XQMap auth, String path, String revision) {
    XSvnUpdate update = new XSvnUpdate();
		return update.XSvnUpdate(auth, path, revision);
  }
	
  public FElem commit (String username, String password, String path, String commitMessage) {
    XSvnCommit commit = new XSvnCommit();
		XQMap auth = createauth(username, password);
    return commit.XSvnCommit(auth, path, commitMessage);
  }
	public FElem commit (XQMap auth, String path, String commitMessage) {
    XSvnCommit commit = new XSvnCommit();
		return commit.XSvnCommit(auth, path, commitMessage);
  }
	
  public FElem propget (String url, String username, String password, String property, String revision) {
    XSvnPropGet propget = new XSvnPropGet();
		XQMap auth = createauth(username, password);
    return propget.XSvnPropGet(url, auth, property, revision);
  }
	public FElem propget (String url, XQMap auth, String property, String revision) {
    XSvnPropGet propget = new XSvnPropGet();
		return propget.XSvnPropGet(url, auth, property, revision);
  }
	
  public FElem propset (String url, String username, String password, String propName, String propValue) {
    XSvnPropSet propset = new XSvnPropSet();
		XQMap auth = createauth(username, password);
    return propset.XSvnPropSet(url, auth, propName, propValue);
  }
	public FElem propset (String url, XQMap auth, String propName, String propValue) {
    XSvnPropSet propset = new XSvnPropSet();
		return propset.XSvnPropSet(url, auth, propName, propValue);
  }
	
  public FElem lock (String url, String username, String password, String paths, String message) {
    XSvnLock lock = new XSvnLock();
		XQMap auth = createauth(username, password);
    return lock.XSvnLock(url, auth, paths, false, false, message);
  }
	public FElem lock (String url, XQMap auth, String paths, String message) {
    XSvnLock lock = new XSvnLock();
		return lock.XSvnLock(url, auth, paths, false, false, message);
  }
	
  public FElem unlock (String url, String username, String password, String paths, String message) {
    XSvnLock unlock = new XSvnLock();
		XQMap auth = createauth(username, password);
    return unlock.XSvnLock(url, auth, paths, true, false, message);
  }
	public FElem unlock (String url, XQMap auth, String paths, String message) {
    XSvnLock unlock = new XSvnLock();
		return unlock.XSvnLock(url, auth, paths, true, false, message);
  }
}
