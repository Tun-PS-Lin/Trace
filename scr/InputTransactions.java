/**
 * InputTransactions.java
 *  This is the class that reference previous transaction outputs
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

public class InputTransactions {
	public String outputTransactionID;
	public OutputTransactions UTXO; //unspent transaction
	
	//Default Constructor
	public InputTransactions() {
		
	}
	
	//InputTransaction Constructor
	public InputTransactions(String outputTransactionID){
		this.outputTransactionID = outputTransactionID; //takes in previous output 
	}
	
	/**
	 * ACCESSORS
	 */
	public String getOutputTransactionID() {
		return this.outputTransactionID;
	}
	
	public OutputTransactions getUTXO() {
		return this.UTXO;
	}
}
