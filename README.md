# basex-svn-api
SVN commands as XQuery functions for BaseX

## Requirements

* Java 1.8 or later
* BaseX 9.0 or later (older versions might work too, but didn't tested)

## How to use

1. copy jar and libs to your BaseX install directory
```bash
cp jar/basex-svn-api.jar $(BASEX)/lib/custom
cp lib/* $(BASEX)/lib/custom
```
2. start BaseX
3. create XQuery file
```xquery
import module namespace svn = 'java:io.transpect.basex.extensions.subversion.XSvnApi';
svn:info("repo-url", "username", "password")
```
4. run and you should get a similar output
```xml
<?xml version="1.0" encoding="UTF-8"?>
<c:param-set xmlns:c="http://www.w3.org/ns/xproc-step">
  <c:param name="date" value="Sun Jan 12 01:10:06 CET 2020"/>
  <c:param name="path" value="path-to-repo"/>
  <c:param name="rev" value="1"/>
  <c:param name="author" value="user"/>
  <c:param name="root-url" value="https://subversion.le-tex.de/common"/>
  <c:param name="uuid" value="f0c14ec2-1229-41a2-ad03-15e4e2b1ca9a"/>
  <c:param name="nodekind" value="dir"/>
  <c:param name="url" value="https://subversion.le-tex.de/common/repo"/>
</c:param-set>
```

## Available XQuery functions
the map auth need the following structure:

```xquery
declare variable $auth := map{'username':'user', 'cert-path': 'path/to/cert', 'password': 'pass'};
```
if cert-path is empty, username and password are used as is.

### info
```xquery
svn:info(String url, String username, String password)
svn:info(String url, map auth)
```
### list
```xquery
svn:list(String url, String username, String password, Boolean recursive)
svn:list(String url, map auth, Boolean recursive)
```
### checkout
```xquery
svn:checkout(String url, String username, String password, String path, String revision)
svn:checkout(String url, map auth, String path, String revision)
```
### mkdir
```xquery
svn:mkdir(String url, String username, String password, String dir, Boolean parents, String commitMessage)
svn:mkdir(String url, map auth, String dir, Boolean parents, String commitMessage)
```
### add
```xquery
svn:add(String url, String username, String password, String path, Boolean parents)
svn:add(String url, map auth, String path, Boolean parents)
```
### delete
```xquery
svn:delete(String url, String username, String password, String path, Boolean force, String commitMessage)
svn:delete(String url, map auth, String path, Boolean force, String commitMessage)
```
### copy
```xquery
svn:copy(String url, String username, String password, String path, String target, String commitMessage)
svn:copy(String url, map auth, String path, String target, String commitMessage)
```
### move
```xquery
svn:move(String url, String username, String password, String path, String target, String commitMessage)
svn:move(String url, map auth, String path, String target, String commitMessage)
```
### update
```xquery
svn:update(String username, String password, String path, String revision)
svn:update(map auth, String path, String revision)
```
### commit
```xquery
svn:commit(String username, String password, String path, String commitMessage)
svn:commit(map auth, String path, String commitMessage)
```
### lock 
```xquery
svn:lock(String url, String username, String password, String+ paths, String message)
svn:lock(String url, map auth, String+ paths, String message)
```
### unlock 
```xquery
svn:unlock(String url, String username, String password, String+ paths, String message)
svn:unlock(String url, map auth, String+ paths, String message)
```
### propget
```xquery
svn:propget(String url, String username, String password, String property, String revision)
svn:propget(String url, map auth, String property, String revision)
```
### propset
```xquery
svn:propset(String url, String username, String password, String propertyName, String propertyValue)
svn:propset(String url, map auth, String propertyName, String propertyValue)
```
