package io.transpect.basex.extensions.subversion;

import io.transpect.basex.extensions.subversion.XSvnAdd;
import io.transpect.basex.extensions.subversion.XSvnCheckout;
import io.transpect.basex.extensions.subversion.XSvnCommit;
import io.transpect.basex.extensions.subversion.XSvnConnect;
import io.transpect.basex.extensions.subversion.XSvnDelete;
import io.transpect.basex.extensions.subversion.XSvnInfo;
import io.transpect.basex.extensions.subversion.XSvnMkDir;
import io.transpect.basex.extensions.subversion.XSvnUpdate;
import io.transpect.basex.extensions.subversion.XSvnXmlReport;
/**
 * 
 * Public Interface for XSvn BaseX extension
 *
 */
public class XSvnApi  {

    public String info (String url, String username, String password) {
        XSvnInfo info = new XSvnInfo();
	return info.XSvnInfo(url, username, password);
    }
    public String checkout (String url, String username, String password, String path, String revision) {
        XSvnCheckout checkout = new XSvnCheckout();
	return checkout.XSvnCheckout(url, username, password, path, revision);
    }
    public String mkdir (String url, String username, String password, String dir, Boolean parents, String commitMessage) {
	XSvnMkDir mkdir = new XSvnMkDir();
	return mkdir.XSvnMkDir(url, username, password, dir, parents, commitMessage);
    }
    public String add (String url, String username, String password, String path, Boolean parents) {
	XSvnAdd add = new XSvnAdd();
	return add.XSvnAdd(url, username, password, path, parents);
    }
    public String delete (String url, String username, String password, String path, Boolean force, String commitMessage) {
	XSvnDelete delete = new XSvnDelete();
	return delete.XSvnDelete(url, username, password, path, force, commitMessage);
    }
    public String update (String username, String password, String path, String revision) {
	XSvnUpdate update = new XSvnUpdate();
	return update.XSvnUpdate(username, password, path, revision);
    }
    public String commit (String username, String password, String path, String commitMessage) {
	XSvnCommit commit = new XSvnCommit();
	return commit.XSvnCommit(username, password, path, commitMessage);
    }

}
