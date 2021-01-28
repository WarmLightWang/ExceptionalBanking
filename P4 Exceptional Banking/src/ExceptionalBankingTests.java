import java.io.File;
import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

/**
 * The class ExceptionalBankingTest is used to test the functions of Account
 * class and TransactionGroup class. This class has multiple test methods, each
 * test focuses on a specific method, check whether the error message is exactly
 * what we want. We do this by catching the exception thrown by the method
 * calling. If we successfully catch an exception and the type and exception
 * message conform to what we have expected, we return true, if not, return
 * false
 */
public class ExceptionalBankingTests {

	/**
	 * We create an Account object and try to add a transaction group. Then
	 * calculate the balance. This test compares the balance with what we expected.
	 * Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testAccountBalance() {
		Account ac = new Account("test");
		String cmd = "0 1 1 1 1 0 0 1";
		try {
			ac.addTransactionGroup(cmd);
		} catch (DataFormatException e) {

			return false;
		}
		if (ac.getCurrentBalance() == 3)
			return true;
		return false;
	}

	/**
	 * We create an Account object and try to add a transaction group. Then
	 * calculate the overdrafts. This test compares the number of overdrafts with
	 * what we expected. Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testOverdraftCount() {
		Account ac = new Account("test");
		String cmd = "0 0 0 0 1 1 1 1 0 0 0 0";
		try {
			ac.addTransactionGroup(cmd);
		} catch (DataFormatException e) {

			return false;
		}
		if (ac.getNumberOfOverdrafts() == 2)
			return true;
		return false;
	}

	/**
	 * We try to initialize a TransactionGroup object with a null parameter This
	 * test compares the exception message caught with what we expected. Should
	 * return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testTransactionGroupEmpty() {
		int[] group = new int[0];
		String errMsg = "transaction group encoding cannot be null or empty";
		try {
			new TransactionGroup(group);
		} catch (DataFormatException e) {
			if (e.getMessage().equals(errMsg))
				return true;
		}
		return false;
	}

	/**
	 * We try to initialize a TransactionGroup object with a group having 3 at the
	 * encoding digit This test compares the exception message caught with what we
	 * expected. Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testTransactionGroupInvalidEncoding() {
		int[] group = { 3, 2, 2, 2, 1, 2 };
		String errMsg = "the first element within a transaction group must be 0, 1, or 2";
		try {
			new TransactionGroup(group);
		} catch (DataFormatException e) {
			if (e.getMessage().equals(errMsg))
				return true;
		}
		return false;
	}

	/**
	 * We create an Account object and try to add a quick type transaction group
	 * with negative number This test compares the exception message caught with
	 * what we expected. Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testAccountAddNegativeQuickWithdraw() {
		String errMsg = "quick withdraw transaction groups may not contain negative numbers";
		Account ac = new Account("test");
		String cmd = "2 1 1 1 -1";
		try {
			ac.addTransactionGroup(cmd);
		} catch (DataFormatException e) {
			if (e.getMessage().equals(errMsg))
				return true;
		}
		return false;
	}

	/**
	 * We create an Account object and try to add a transaction group with letters
	 * in it. This test compares the exception message caught with what we expected.
	 * Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testAccountBadTransactionGroup() {
		String errMsg = "addTransactionGroup requires string commands that contain only space separated integer values";
		Account ac = new Account("test");
		String cmd = "2 1 1 hello -1";
		try {
			ac.addTransactionGroup(cmd);
		} catch (DataFormatException e) {
			if (e.getMessage().equals(errMsg))
				return true;
		}
		return false;
	}

	/**
	 * We create an Account object and try to add a transaction group and get the
	 * amount with a bad index This test compares the exception message caught with
	 * what we expected. Should return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testAccountIndexOutOfBounds() {
		String errMsg = "ArrayIndexOutOfBoundsException: 2. Exceed the limit: 1";
		Account ac = new Account("test");
		String cmd = "0 1 1 1 1";
		try {
			ac.addTransactionGroup(cmd);
			ac.getTransactionAmount(2);
		} catch (IndexOutOfBoundsException e) {
			if (e.getMessage().equals(errMsg))
				return true;
		} catch (DataFormatException e) {
			return false;
		}
		return false;
	}

	/**
	 * This test compares the exception message caught with what we expected. Should
	 * return true.
	 * 
	 * @return true when they are the same, false otherwise
	 */
	public static boolean testAccountMissingFile() {

		File file = new File("1.txt");
		try {
			new Account(file);
		} catch (FileNotFoundException e) {

			return true;
		}

		return false;
	}

	/**
	 * Testing main. Runs each test and prints which (if any) failed. If no problem
	 * occurs, print a single line showing "All tests passed!".
	 */
	public static void main(String[] args) {
		int fails = 0;
		if (!testAccountBalance()) {
			System.out.println("testAccountBalance failed");
			fails++;
		}
		if (!testOverdraftCount()) {
			System.out.println("testOverdraftCount failed");
			fails++;
		}
		if (!testTransactionGroupEmpty()) {
			System.out.println("testTransactionGroupEmpty failed");
			fails++;
		}
		if (!testTransactionGroupInvalidEncoding()) {
			System.out.println("testTransactionGroupInvalidEncoding failed");
			fails++;
		}
		if (!testAccountAddNegativeQuickWithdraw()) {
			System.out.println("testAccountAddNegativeQuickWithdraw failed");
			fails++;
		}
		if (!testAccountBadTransactionGroup()) {
			System.out.println("testAccountBadTransactionGroup failed");
			fails++;
		}
		if (!testAccountIndexOutOfBounds()) {
			System.out.println("testAccountIndexOutOfBounds failed");
			fails++;
		}
		if (!testAccountMissingFile()) {
			System.out.println("testAccountMissingFile");
			fails++;
		}

		// If no problem occurs, print a single line showing "All tests passed!".
		if (fails == 0)
			System.out.println("All tests passed!");
	}
}
