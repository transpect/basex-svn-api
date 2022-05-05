package io.transpect.calabash.extensions.subversion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;


import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import com.xmlcalabash.core.XMLCalabash;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcConstants;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.library.DefaultStep;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.runtime.XAtomicStep;
import com.xmlcalabash.util.TreeWriter;
import com.xmlcalabash.io.ReadablePipe;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNCommitInfo;

import io.transpect.calabash.extensions.subversion.XSvnConnect;
import io.transpect.calabash.extensions.subversion.XSvnXmlReport;
/**
 * Getting a Property from an SVN repository
 *
 */
public class XSvnPropGetSet extends DefaultStep {
  private WritablePipe result = null;
  private ReadablePipe source = null;
  
  private SVNRevision svnPegRevision = null;
  private SVNRevision svnRevision = null;
    
  private HashMap<String, String> PropGet(SVNWCClient propClient, SVNURL url, String[] aProperties) throws SVNException{
	    print("PropGet");
	    List<SVNPropertyData> propData = new ArrayList<SVNPropertyData>();
	    for (String property: aProperties){
	      propData.add(propClient.doGetProperty(url,property,svnPegRevision,svnRevision));
	    }
	      HashMap<String, String> results = new HashMap<String, String>();
	    for (SVNPropertyData data: propData){
	      if (data != null) {
	        results.put(data.getName(), data.getValue().toString());
	      }
	    }
	    return results;
	  }
  
  private HashMap<String, String> PropGet(SVNWCClient propClient, File svnPath, String[] aProperties) throws SVNException{
	    print("PropGet");
	    List<SVNPropertyData> propData = new ArrayList<SVNPropertyData>();
	    for (String property: aProperties){
	      propData.add(propClient.doGetProperty(svnPath,property,svnPegRevision,svnRevision));
	    }
	      HashMap<String, String> results = new HashMap<String, String>();
	    for (SVNPropertyData data: propData){
	      if (data != null) {
	        results.put(data.getName(), data.getValue().toString());
	      }
	    }
	    return results;
	  }
  
  private ISVNPropertyHandler getCommitHandler(){
  return ISVNPropertyHandler.NULL;
  }
  
  private HashMap<String, String> PropSet(SVNWCClient propClient, File svnPath) throws SVNException{
	  print("Local PropSet");
	    try {
	      HashMap<String, String> props = new HashMap<String, String>();
	      XdmNode sourceNode = null;
	      
	      sourceNode = source.read();
	      while (sourceNode != null){
	        try {
	          XdmNode inputNode = null;
	          for (XdmNode tempchild: sourceNode.children()){ //get correct node
	         	if (tempchild != null){
	              String tempName = tempchild.attribute("name");
	              if (tempName != null){
	                inputNode = tempchild;
	              }
	            }
	          }
	          if (inputNode != null){
	            // print(inputNode.attribute("name"));
	            // print(inputNode.toString());
	          } else {
	            throw new Exception("No name found. Property will be discarded.");
	          }
	          String propName = inputNode.attribute("name");
	          
	          Iterator i = inputNode.children().iterator();
	          int c = 0; //number of children
	          for (XdmNode x: inputNode.children()){c++;}
	          // print(propName + " children: " + c); 
	          if (c == 1) {
	            for (XdmNode soleChild: inputNode.children()){
	              props.put(propName,soleChild.toString()); //Add to Props
	                print(propName + ": " + soleChild.toString());
	            }
	          }
	        }
	        catch (Exception e){
	          print(e.getMessage());
	        }
	        finally{
	            sourceNode = source.read();
	        }
	      }
	      HashMap<String, String> results = new HashMap<String, String>();
	      for (Map.Entry<String, String> entry: props.entrySet()){
	        results.put(entry.getKey(), entry.getValue());
	        SVNPropertyValue value = SVNPropertyValue.create(entry.getValue());
	        propClient.doSetProperty(svnPath, entry.getKey(),value, false,SVNDepth.EMPTY,getCommitHandler(),null);
	      }
	      return results;
	    }	
	    catch (Exception e){
	      print(e.getMessage());
	      throw new SVNException(SVNErrorMessage.create(null,e.getMessage()));
	    }
  }
  
  private HashMap<String, String> PropSet(SVNWCClient propClient, SVNURL url) throws SVNException{
    print("Remote PropSet");
    try {
      HashMap<String, String> props = new HashMap<String, String>();
      XdmNode sourceNode = null;
      
      sourceNode = source.read();
      while (sourceNode != null){
        try {
          XdmNode inputNode = null;
          for (XdmNode tempchild: sourceNode.children()){ //get correct node
         	if (tempchild != null){
              String tempName = tempchild.attribute("name");
              if (tempName != null){
                inputNode = tempchild;
              }
            }
          }
          if (inputNode != null){
            // print(inputNode.attribute("name"));
            // print(inputNode.toString());
          } else {
            throw new Exception("No name found. Property will be discarded.");
          }
          String propName = inputNode.attribute("name");
          
          Iterator i = inputNode.children().iterator();
          int c = 0; //number of children
          for (XdmNode x: inputNode.children()){c++;}
          // print(propName + " children: " + c); 
          if (c == 1) {
            for (XdmNode soleChild: inputNode.children()){
              props.put(propName,soleChild.toString()); //Add to Props
                print(propName + ": " + soleChild.toString());
            }
          }
        }
        catch (Exception e){
          print(e.getMessage());
        }
        finally{
            sourceNode = source.read();
        }
      }
      HashMap<String, String> results = new HashMap<String, String>();
      for (Map.Entry<String, String> entry: props.entrySet()){
        results.put(entry.getKey(), entry.getValue());
        SVNPropertyValue value = SVNPropertyValue.create(entry.getValue());
        SVNCommitInfo ci = propClient.doSetProperty(url, entry.getKey(),value, SVNRevision.HEAD,"Property " + entry.getKey() + " set to value " + entry.getValue(),null,false,getCommitHandler());
        print(ci.toString());
      }
      return results;
    }	
    catch (Exception e){
      print(e.getMessage());
      throw new SVNException(SVNErrorMessage.create(null,e.getMessage()));
    }
  }
  
  private void print(String text){
    System.out.println("XSvnPropGetSet: " + text);
  }
  
  public XSvnPropGetSet(XProcRuntime runtime, XAtomicStep step) {
    super(runtime,step);
  }
  @Override 
  public void setInput(String port, ReadablePipe pipe){
    print("input set: " + port);
	source = pipe;
  }
  @Override
  public void setOutput(String port, WritablePipe pipe) {
	  print("output set: " + port);
    result = pipe;
  }
  @Override
  public void reset() {
    result.resetWriter();
  }
  @Override
  public void run() throws SaxonApiException {
    super.run();
    String username = getOption(new QName("username")).getString();
    String password = getOption(new QName("password")).getString();
    String url = getOption(new QName("path")).getString();
	
	  RuntimeValue temp;	  
	  temp = getOption(new QName("properties"));
	  String sProperties;
	  if (temp != null) {
	  	sProperties = temp.getString();
	  } else {
	  	sProperties = null;
	  }
	  	
	  String[] aProperties = new String[]{};
	  if (sProperties != null) {
	    aProperties = sProperties.split(" ");
	  }
	  for(String param : aProperties)
      print("property-name: " + param);
    XSvnXmlReport report = new XSvnXmlReport();
	
    try{
      XSvnConnect connection = new XSvnConnect(url, auth);
      SVNClientManager clientmngr = connection.getClientManager();
      SVNWCClient propClient = clientmngr.getWCClient();
			print(url);
      if (url.startsWith("http"))
      {
        SVNURL svnurl = SVNURL.parseURIEncoded( url );
        
        boolean allowUnversionedObstructions = false;
        //revision
        String revision = "";
        if(revision.trim().isEmpty()){
          svnRevision = svnPegRevision = SVNRevision.HEAD;
        } else {
          svnRevision = svnPegRevision = SVNRevision.parse(revision);
        }
        
        HashMap<String, String> results = new HashMap<String, String>();
        //PropGet or PropSet
        if (aProperties.length == 0){ 
          results = PropSet(propClient, svnurl);
        } else {
          results = PropGet(propClient, svnurl, aProperties);
        }
       
        XdmNode xmlResult = report.createXmlResult(results, runtime, step);
        result.write(xmlResult);
      } else {
    	  File svnPath = new File(url);
    	  boolean allowUnversionedObstructions = false;
          //revision
          String revision = "";
          if(revision.trim().isEmpty()){
            svnRevision = svnPegRevision = SVNRevision.HEAD;
          } else {
            svnRevision = svnPegRevision = SVNRevision.parse(revision);
          }
          
          HashMap<String, String> results = new HashMap<String, String>();
          //PropGet or PropSet
          if (aProperties.length == 0){ 
            results = PropSet(propClient, svnPath);
          } else {
            results = PropGet(propClient, svnPath, aProperties);
          }
         
          XdmNode xmlResult = report.createXmlResult(results, runtime, step);
          result.write(xmlResult);
      }
    }
    catch(SVNException svne) {
      System.out.println(svne.getMessage());
      XdmNode xmlError = report.createXmlError(svne.getMessage(), runtime, step);
      result.write(xmlError);
    }
  }
};