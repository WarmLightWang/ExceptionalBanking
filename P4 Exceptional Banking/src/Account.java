import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * An Account type object represents a bank user. The user will have a unique ID
 * that can not be changed and an arrayList of TransactionGroups and its size
 * stored in transactionGroupsCount This class contains 6 private field, 2
 * initializer and 6 methods, each of them in turn has following functions:
 * 
 * @Initializer(name): load a name and set it as unique ID. Set an
 * transactionGroups with nulls @Initializer(file): load a file, set the name
 * and add transaction groups, throw an exception if there is any.
 * 
 * @method getId(): return the unique ID
 * @method addTransactionGroup(command): add a new transaction group, throw an
 *         exception if there is any
 * @method getTransactionCount(): calculate the total number of transactions in
 *         all groups
 * @method getTransactionAmount(index): calculate the amount of a certain
 *         transaction with an index
 * @method getCurrentBalance(): return the balance of all transaction groups
 * @method getNumberOfOverdrafts(): calculate the number of overdrafts of all
 *         transaction groups
 */
public class Account {

	private static final int MAX_GROUPS = 1;
	private static int nextUniqueId = 1000;
	private String name;
	private final int UNIQUE_ID;
	private TransactionGroup[] transactionGroups;
	private int transactionGroupsCount;

	/**
	 * This initializer will load a name and set it as unique ID. Set an
	 * transactionGroups with nulls
	 */
	public Account(String name) {
		this.setName(name);
		this.UNIQUE_ID = Account.nextUniqueId;
		Account.nextUniqueId++;
		this.transactionGroups = new TransactionGroup[MAX_GROUPS];
		this.transactionGroupsCount = 0;
	}

	/**
	 * This initializer will load a file, set the name and add transaction groups.
	 * There is an error that may occur so we have to handle it: the file we want
	 * does not exist, we should throw an FileNotFoundException
	 */
	public Account(File file) throws FileNotFoundException {
		// NOTE: THIS METHOD SHOULD NOT BE CALLED MORE THAN ONCE, BECAUSE THE
		// RESULTING BEHAVIOR IS NOT DEFINED WITHIN THE JAVA SPECIFICATION ...

		Scanner in = new Scanner(file);
		// ... THIS WILL BE UPDATED TO READ FROM A FILE INSTEAD OF SYSTEM.IN.

		this.setName(in.nextLine());
		this.UNIQUE_ID = Integer.parseInt(in.nextLine());
		Account.nextUniqueId = this.UNIQUE_ID + 1;
		this.transactionGroups = new TransactionGroup[MAX_GROUPS];
		this.transactionGroupsCount = 0;
		while (in.hasNextLine()) {
			try {
				this.addTransactionGroup(in.nextLine());
			} catch (DataFormatException e) {
				// We catch this exception and do nothing, because we want remaining transaction
				// groups to be processed as normal, one exception does not influence the other
				// groups
			}
		}
		in.close();
	}

	public int getId() {
		return this.UNIQUE_ID;
	}

	/**
	 * This method add a transaction group to the array. There are several errors
	 * that may occur so we have to handle them in sequence. 1. String command
	 * contains letters other than numbers, we should throw a DataFormatException.
	 * 2. transactionGroups exceed the limit, we should throw a OutOfMemoryError.
	 * 
	 * @throws DataFormatException
	 */
	public void addTransactionGroup(String command) throws DataFormatException {
		String[] parts = command.split(" ");
		int[] newTransactions = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			try {
				newTransactions[i] = Integer.parseInt(parts[i]);
			} catch (NumberFormatException e) {
				// Exception: command contains characters other than numbers
				// Note that we have to catch the NumberFormatException and throw a
				// DataFormatException
				throw new DataFormatException(
						"addTransactionGroup requires string commands that contain only space separated integer values");
			}
		}
		try {
			TransactionGroup t = new TransactionGroup(newTransactions);
			this.transactionGroups[this.transactionGroupsCount] = t;
			this.transactionGroupsCount++;
		} catch (IndexOutOfBoundsException e) {
			// Exception: array out of bound
			// Note that we have to catch the IndexOutOfBoundsException and throw a
			// OutOfMemoryError
			throw new OutOfMemoryError(
					"the capacity of this Account object’s internal array storage is" + " MAX_GROUPS");
		}

	}

	/**
	 * This method calculate the total number of transactions in all groups
	 */
	public int getTransactionCount() {
		int transactionCount = 0;
		for (int i = 0; i < this.transactionGroupsCount; i++)
			transactionCount += this.transactionGroups[i].getTransactionCount();
		return transactionCount;
	}

	/**
	 * This method calculate the amount of a certain transaction with an index There
	 * are several errors that may occur so we have to handle them in sequence. If
	 * index >= size, we should throw IndexOutOfBoundsException.
	 * 
	 * @return the amount of No.index transaction
	 */
	public int getTransactionAmount(int index) throws IndexOutOfBoundsException {
		int size = this.getTransactionCount();
		// Exception: index >= size, that means the index we want is beyong limit
		if (index >= size)
			throw new IndexOutOfBoundsException(
					"ArrayIndexOutOfBoundsException: " + index + ". Exceed the limit: " + size);
		int transactionCount = 0;
		for (int i = 0; i < this.transactionGroupsCount; i++) {
			int prevTransactionCount = transactionCount;
			transactionCount += this.transactionGroups[i].getTransactionCount();
			if (transactionCount > index) {
				index -= prevTransactionCount;
				// Note that this index could be 0, it is legal because index start from 0
				// in the TransactionGroup class
				return this.transactionGroups[i].getTransactionAmount(index);
			}
		}
		return -1;
	}

	/**
	 * This method calculate the amount of a certain transaction with an index
	 * 
	 * @return return the balance of all transaction groups
	 */
	public int getCurrentBalance() {
		int balance = 0;
		int size = this.getTransactionCount();
		for (int i = 0; i < size; i++)
			balance += this.getTransactionAmount(i);
		return balance;
	}

	/**
	 * This method calculate the number of overdrafts of all transaction groups
	 * 
	 * @return return the number of overdrafts of all transaction groups
	 */
	public int getNumberOfOverdrafts() {
		int balance = 0;
		int overdraftCount = 0;
		int size = this.getTransactionCount();
		// Note that when calculating overdrafts, we have to satisfy both balance and
		// amount < 0
		for (int i = 0; i < size; i++) {
			int amount = this.getTransactionAmount(i);
			balance += amount;
			if (balance < 0 && amount < 0)
				overdraftCount++;
		}
		return overdraftCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
