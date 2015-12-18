package gdp18.waterlock;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import blackboard.platform.plugin.PlugInUtil;

public class Settings {
	
	private String providerID = "blackboard";
    private String sharedKey = "this is shared with blackboard";
    private int jwtExpirySeconds = 10;
    
    public Settings(){
    	try {
            // Get path to plugin config directory.
            File configDir = PlugInUtil.getConfigDirectory(Utils.vendorID,
                    Utils.pluginHandle);
            File settingsFile = new File(configDir, "settings.xml");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            if(settingsFile.exists())
            {
                // Parse XML
                Document settingsDocument = db.parse(settingsFile);

                // Walk document tree and set corresponding setting values.
                readSettings(settingsDocument);
            }
        }
        catch(Exception e)
        {
            // Utils has static reference to instance of Settings, so can't use Utils.log() here
            e.printStackTrace();
        }
    }
    
    public String getProviderID()
    {
        return providerID;
    }
    
    public void setProviderID(String instanceName){
    	this.providerID = instanceName;
    	save();
    }
    
    public String getSharedKey(){
    	return sharedKey;
    }
    
    public void setSharedKey(String sharedKey){
    	this.sharedKey = sharedKey;
    	save();
    }
    
    public int getJWTExpirySeconds()
    {
    	return jwtExpirySeconds;
    }
    
    public void setJWTExpirySeconds(int expire){
    	this.jwtExpirySeconds = expire;
    	save();
    }
    
 // Serialize current settings to XML settings file in config directory.
    private void save()
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document settingsDocument = db.newDocument();

            Element docElem = settingsDocument.createElement("config");
            settingsDocument.appendChild(docElem);

            Element providerIDElem = settingsDocument.createElement("providerID");
            providerIDElem.setAttribute("name", providerID);
            docElem.appendChild(providerIDElem);

            Element sharedKeyElem = settingsDocument.createElement("sharedKey");
            sharedKeyElem.setAttribute("name", sharedKey);
            docElem.appendChild(sharedKeyElem);
            
            File configDir = PlugInUtil.getConfigDirectory(Utils.vendorID, Utils.pluginHandle);
            File settingsFile = new File(configDir, "settings.xml");

            FileOutputStream outStream = new FileOutputStream(settingsFile);
            
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();    
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
            LSParser parser = impl.createLSParser(
                    DOMImplementationLS.MODE_SYNCHRONOUS,
                    "http://www.w3.org/TR/REC-xml");
            LSSerializer serializer = impl.createLSSerializer();
            LSOutput output = impl.createLSOutput();
            output.setEncoding("UTF-8");
            output.setByteStream(outStream);
            serializer.write(settingsDocument, output);
        }
        catch(Exception e)
        {
            Utils.log(e, "Error saving settings.");
        }
    }
    
 // Walk document tree and set corresponding setting values.
    private void readSettings(Document settingsDocument)
    {
        Element docElem = settingsDocument.getDocumentElement();

        NodeList providerIDNodes = docElem.getElementsByTagName("providerID");
        if(providerIDNodes.getLength() != 0)
        {
            Element providerIDElem = (Element)providerIDNodes.item(0);
            this.providerID = providerIDElem.getAttribute("name");
        }

        NodeList sharedKeyNodes = docElem.getElementsByTagName("sharedKey");
        if(sharedKeyNodes.getLength() != 0)
        {
            Element sharedKeyElem = (Element)sharedKeyNodes.item(0);
            this.sharedKey = sharedKeyElem.getAttribute("name");
        }
    }
}
