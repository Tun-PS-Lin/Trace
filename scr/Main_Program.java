import java.io.IOException;
import java.util.Scanner;

/**
 * Main_Program.java This is the Main_Program for FakeCoin blockchain Users will
 * be able to view balances, send funds to other users on the blockchain Miners
 * will be able mine blocks and get rewarded if a block is successfully mined
 * 
 * 01.April.2021
 * 
 * @author Tun Pyay Sone LIN
 */

public class Main_Program {
	 //AList BlockChain
	AList<Block> blockchain = new AList<Block>();
	
	//UTXOs Ledger
	public static LinkedDictionary<String, OutputTransactions> UTXOs = new LinkedDictionary<String, OutputTransactions>(); //UTXOs Ledger
	
	//User Wallet Database
	private LinkedDictionary<String, Wallet> User_Wallet_Database = new LinkedDictionary<String, Wallet>();
	
	//Miner Wallet Database
	private LinkedDictionary<String, Wallet> Miner_Wallet_Database = new LinkedDictionary<String, Wallet>();
	
	public static void main(String args[]) throws IOException {
		mainProgram();
	}
	
	public static void mainProgram() throws IOException {
		
//		populateBlockChain();
//		populateUTXOsLedger();
//		populateUserWallet();
//		populateMinerWallet();
		
		System.out.println("\nLaunching FAKECOIN V2.0...\n");
		
//		System.out.println("===================================================================");
//		System.out.println(" ______    ____             _____    _____   _____   _____  ");
//		System.out.println("|         |    |    |   /  |        |       |     |    |    |\\   |");
//		System.out.println("|____    |______|   |_ /   |-----   |       |     |    |    | \\  |");
//		System.out.println("|        |      |   |  \\   |        |       |     |    |    |  \\ |");
//		System.out.println("|        |      |   |   \\  |_____   |_____  |_____|  __|__  |   \\|");
//		System.out.println("===================================================================");	
		
		System.out.println("===========================================================================");
		System.out.println("FFFFFFF   FFFF     F    F   FFFFFFF   FFFFFFF   FFFFFF   FFFFFFF   FF    F");
		System.out.println("F        F    F    F   F    F         F        F      F     F      F F   F");
		System.out.println("FFFFF   FFFFFFFF   FFFF     FFFFFF    F        F      F     F      F  F  F");
		System.out.println("F       F      F   F   F    F         F        F      F     F      F   F F");
		System.out.println("F       F      F   F    F   FFFFFFF   FFFFFFF   FFFFFF   FFFFFFF   F    FF");
		System.out.println("===========================================================================");		
		
		
		
		
	}
	
	public String mainMenu(Scanner input) {
		String choice;
		System.out.println("\n-----------------------------------------------------------------------------------");
		System.out.println("Already have a Profile?   Don't have Profile?    Close Program");
		System.out.println("	A. Log in 	     B. Register            X. Exit");
		System.out.print("\nYour choice: ");
		choice = input.nextLine();
		while (!choice.equalsIgnoreCase("A") && !choice.equalsIgnoreCase("B") && !choice.equalsIgnoreCase("X")) {
			System.out.print("\nInvalid input!\nPlease choose again: ");
			choice = input.nextLine();
		}
		return choice.toUpperCase();

	}
	
	
	/**
	 * This function read masterBlockChain.txt and populate to BlockChain AList
	 */
	public void populateBlockChain(){
		
	}
	
	/**
	 * This function read masterUTXOsLedger.txt and populate to UTXOs dictionary
	 */
	public void populateUTXOsLedger() {
		
	}
	
	/**
	 * This function read masterUserWallet.txt and populate to User_Wallet_Database dictionary
	 */
	public void populateUserWallet() {
		
	}
	
	/**
	 * This function read masterMinerWallet.txt and populate to Miner_Wallet_Database dictionary
	 */
	public void populateMinerWallet() {
		
	}
	

}
