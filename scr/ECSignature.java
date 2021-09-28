/**
 * ECSignature.java 
 * This is the class for creating and verifying digital signature for each transaction
 * 
 * @author Tun Pyay Sone LIN
 */

import java.security.*;

public class ECSignature {

	//Default Constructor
	ECSignature(){
		
	}
	
	/**
	 * This function create a digital signature for a transaction
	 * by using Elliptical Curvature Digital Signature Algorithm (ECDSA)
	 * 
	 * @param privateKey
	 *            - privateKey of the sender
	 * @param data
	 *            - senderKey + receiverKey + amount
	 * @return - the digital signature output
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public byte[] getECSignature(PrivateKey privateKey, String data) throws InvalidKeyException, SignatureException {
		Signature dsa;
		byte[] output = new byte[0];

		try {
			dsa = Signature.getInstance("SHA256withECDSA");
			dsa.initSign(privateKey);
			byte[] strByte = data.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * This function verifies the digital signature created
	 * 
	 * @param publicKey
	 *            - publicKey of the sender
	 * @param data
	 *            - senderKey + receiverKey + data
	 * @param ECSignature
	 *            - the digital signature created using ECSignature() function
	 * @return - true if the signature is verified
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public boolean VerifyECSignature(PublicKey publicKey, String data, byte[] ECSignature)
			throws InvalidKeyException, SignatureException {
		boolean isVerified = false;
		
		try {
			Signature VerifyEC = Signature.getInstance("SHA256withECDSA");
			VerifyEC.initVerify(publicKey);
			VerifyEC.update(data.getBytes());
			isVerified = VerifyEC.verify(ECSignature);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isVerified;
	}
}
