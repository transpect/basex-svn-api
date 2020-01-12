# basex-svn-api
SVN integration for BaseX

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
