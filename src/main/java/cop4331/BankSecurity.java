package cop4331;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class BankSecurity {

    private static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
    }
    
    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        
        return bytesToHex(encodedHash);
	}
	
	public static Account findAccount(List<Account> l, String account) {
		for (Account a : l) {
			if (a.getAName().equals(account)) {
				return a;
			}
		}
		// shouldn't (fingers crossed) get here
		return null;
	}
}
