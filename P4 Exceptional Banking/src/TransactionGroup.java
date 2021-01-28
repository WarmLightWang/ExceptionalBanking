import java.util.zip.DataFormatException;

/**
 * Each TransactionGroup instance represents a group of transactions with the
 * same encoding type. This class contains two private field: type(EncodingType
 * object) and values(int[] type object). This class provides a initializer and
 * 2 methods, each of them in turn has following functions:
 * 
 * @Initializer: load a int[] type groupEncoding, process it, try to get the
 *               type and values, throw exception if there is any, store the
 *               type and values.
 * @method getTransactionCount(): calculate the number of transactions in this
 *         transaction group.
 * @method getTransactionAmount(index): calculate the amount of No.index
 *         transaction.
 */
public class TransactionGroup {

	private enum EncodingType {
		BINARY_AMOUNT, INTEGER_AMOUNT, QUICK_WITHDRAW
	};

	private EncodingType type;
	private int[] values;

	/**
	 * This initializer will load a integers list(groupEncoding). This list contains
	 * a encoding number at index 0, followed by several transaction values that
	 * conform to this encoding type. There are several errors that may occur so we
	 * have to handle them in sequence. 1. Null or empty groupEncoding, we should
	 * throw a DataFormatException. 2. Encoding number is not 0, 1 or 2, we should
	 * throw a DataFormatException. 3. For binary type, if any value is not 0 or 1,
	 * we should throw a DataFormatException. 4. For integer type, if any value is
	 * 0, we should throw a DataFormatException. 5. For quick type, if the size of
	 * value is not 4, we should throw a DataFormatException. 6. For quick type, if
	 * any value is less than 0, we should throw a DataFormatException. Note that
	 * these are unchecked exceptions, we should add throw declaration at the
	 * beginning.
	 */
	public TransactionGroup(int[] groupEncoding) throws DataFormatException {

		// Exception: Null or empty groupEncoding
		if (groupEncoding == null || groupEncoding.length == 0)
			throw new DataFormatException("transaction group encoding cannot be null or empty");
		// Exception: Encoding number is not 0, 1 or 2
		if (groupEncoding[0] != 0 && groupEncoding[0] != 1 && groupEncoding[0] != 2)
			throw new DataFormatException("the first element within a transaction group must be 0, 1, or 2");

		this.type = EncodingType.values()[groupEncoding[0]];
		// Note that values does not contain encoding number in groupEncoding, thus size
		// is length - 1.
		this.values = new int[groupEncoding.length - 1];
		for (int i = 0; i < values.length; i++) {
			// Exception: In binary type, a value is not 0 or 1
			if (groupEncoding[0] == 0 && groupEncoding[i + 1] != 0 && groupEncoding[i + 1] != 1)
				throw new DataFormatException("binary amount transaction groups may only contain 0s and 1s");
			// Exception: In integer type, a value is 0
			if (groupEncoding[0] == 1 && groupEncoding[i + 1] == 0)
				throw new DataFormatException("integer amount transaction groups may not contain 0s");
			// Exception: In quick type, the size of values is not 4(or size of
			// groupEncoding is no 5)
			if (groupEncoding[0] == 2 && groupEncoding.length != 5)
				throw new DataFormatException("quick withdraw transaction groups must contain 5 elements");
			// Exception: In quick type, a value is less than 0
			if (groupEncoding[0] == 2 && groupEncoding[i + 1] < 0)
				throw new DataFormatException("quick withdraw transaction groups may not contain negative numbers");

			this.values[i] = groupEncoding[i + 1];
		}
	}

	/**
	 * This method calculate the number of transactions in this transaction group.
	 * For binary type, successive same number will be combined together and
	 * regarded as one transaction. For example, binary type: 0 0 0 1 1 1 has 2
	 * transactions. For integer type, number of transactions is the same as size of
	 * values. For quick type, number of transactions is adding each 4 types of
	 * quick withdraw number. For example, quick type: 2 2 2 2 has 8 transactions.
	 * 
	 * @return transactionCount: the number of transactions in this transaction
	 *         group.
	 */

	public int getTransactionCount() {
		int transactionCount = 0;
		switch (this.type) {
		// For binary type, successive same number will be combined together and
		// regarded as one transaction. For example, binary type: 0 0 0 1 1 1 has 2
		// transactions.
		case BINARY_AMOUNT:
			int lastAmount = -1;
			for (int i = 0; i < this.values.length; i++) {
				if (this.values[i] != lastAmount) {
					transactionCount++;
					lastAmount = this.values[i];
				}
			}
			break;
		// For integer type, number of transactions is the same as size of values.
		case INTEGER_AMOUNT:
			transactionCount = values.length;
			break;
		// For quick type, number of transactions is adding each 4 types of quick
		// withdraw number. For example, quick type: 2 2 2 2 has 8 transactions.
		case QUICK_WITHDRAW:
			for (int i = 0; i < this.values.length; i++)
				transactionCount += this.values[i];
		}
		return transactionCount;
	}

	/**
	 * This method calculate the amount of No.index transaction. Note that the index
	 * start from 0, and it represents the amount AFTER the combination in binary
	 * type and split in quick type. There is an error that may occur so we have to
	 * handle it: transactionIndex exceed the limit of count - 1, we should throw an
	 * IndexOutOfBoundsException
	 * 
	 * @return the amount of No.index transaction.
	 */
	public int getTransactionAmount(int transactionIndex) throws IndexOutOfBoundsException {
		// Exception: trasactionIndex is more or equal to total transaction count,
		// because index starts from 0, it should at most count - 1
		if (transactionIndex >= this.getTransactionCount())
			throw new IndexOutOfBoundsException("ArrayIndexOutOfBoundsException: " + transactionIndex
					+ ". Exceed the limit: " + (this.getTransactionCount()));

		int transactionCount = 0;
		switch (this.type) {
		// For binary type, successive same number will be combined together and
		// regarded as one transaction. For example, binary type: 1 0 0 1 1 1. Index 2
		// amount = 3
		case BINARY_AMOUNT:
			int lastAmount = -1;
			int amountCount = 0;
			for (int i = 0; i <= this.values.length; i++) {
				// if there is a shifting point break the successive sequence, count++
				if (i == this.values.length || this.values[i] != lastAmount) {
					// if we find the index we want, we begin to calculate the amount
					if (transactionCount - 1 == transactionIndex) {
						if (lastAmount == 0)
							return -1 * amountCount;
						else
							return +1 * amountCount;
					}
					transactionCount++;
					lastAmount = this.values[i];
					amountCount = 1;
				} else
					amountCount++;
				lastAmount = this.values[i];
			}
			break;
		// For integer type, just simply return the No.index value
		case INTEGER_AMOUNT:
			return this.values[transactionIndex];
		// For quick type, we should split each kind of withdraw, process them one by
		// one,and find the index we want
		case QUICK_WITHDRAW:
			final int[] QW_AMOUNTS = new int[] { -20, -40, -80, -100 };
			for (int i = 0; i < this.values.length; i++)
				for (int j = 0; j < this.values[i]; j++)
					if (transactionCount == transactionIndex)
						return QW_AMOUNTS[i];
					else
						transactionCount++;
		}
		return -1;
	}
}
