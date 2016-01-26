package gdp18.waterlock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedHashMap;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.JWTSigner.Options;

import blackboard.platform.plugin.PlugInUtil;
import gdp18.waterlock.Settings;
import gdp18.waterlock.jwt.EditedJWTVerifier;

public class Utils {
	public static final Settings pluginSettings = new Settings();
    public static final String vendorID = "syn";
    public static final String pluginHandle = "waterlock-blackboard-auth";

    public static final String logFilename = "log.txt";

    public static void log(Exception e, String logMessage)
    {
        try
        {
        	File configDir = PlugInUtil.getConfigDirectory(vendorID, pluginHandle);
        	File logFile = new File(configDir, logFilename);

            FileWriter fileStream = new FileWriter(logFile, true);
            BufferedWriter bufferedStream = new BufferedWriter(fileStream);
            PrintWriter output = new PrintWriter(bufferedStream);

            output.write(new Date().toString() + ": " + logMessage);

            if(!logMessage.endsWith(System.getProperty("line.separator")))
            {
                output.println();
            }

            if(e != null)
            {
                e.printStackTrace(output);
            }

            output.println("===================================================================================");

            output.close();
        }
        catch(Exception ex)
        {
        }
    }
    public static void log(String logMessage){
        log(null, logMessage);
    }
    
    /**
     * Returns the String of a new JWT for Waterlock authentication. The signature and expiry
     * seconds of the JWT are defined in Settings.
     * @param username
     * @param firstname
     * @param surname
     * @param email
     * @return
     */
    public static String generateResponseJWT(String username, String firstname, 
			String surname, String email){

		JWTSigner signer = new JWTSigner(pluginSettings.getSharedKey());
		
		LinkedHashMap<String, Object> responseClaims = new LinkedHashMap<String, Object>();
		responseClaims.put("provider", pluginSettings.getProviderID());
		responseClaims.put("blackboard_id", username);
		responseClaims.put("username", pluginSettings.getProviderID() + "\\" + username);
		responseClaims.put("firstname", firstname);
		responseClaims.put("lastname", surname);
		responseClaims.put("email", email);
		
		Options options = new Options();
		options.setExpirySeconds(pluginSettings.getJWTExpirySeconds());
		
		return signer.sign(responseClaims, options);
	}

    /**
     * Validates an incoming JWT for this system and returns the payload of the JWT as a LinkedHashMap. 
     * @param jsonWebTokenString
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IllegalStateException
     * @throws SignatureException
     * @throws IOException
     * @throws JWTVerifyException
     */
    public static LinkedHashMap<String, Object> validateIncomingWebToken(String jsonWebTokenString) 
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, 
			SignatureException, IOException, JWTVerifyException{
		EditedJWTVerifier verifier = new EditedJWTVerifier(pluginSettings.getSharedKey());
		return (LinkedHashMap<String, Object>) verifier.verify(jsonWebTokenString);
	}

	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		e.printStackTrace(pw);
		return sw.toString();
	}

    public static boolean isValidXML(String str){
    	return !(str.contains("<") | str.contains(">") | str.contains("&"));
    }
    
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
}
