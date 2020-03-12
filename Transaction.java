

public class Transaction {
    public int size;
    public Operation[] operations;
    public TxnStatus txnStatus;

    public Transaction (Operation[] operations) {

        this.size = 5;
        this.operations = operations;
        this.txnStatus = TxnStatus.active;
    }

    @Override
    public String toString() {

        String str = "";
        //System.out.println(operations.length);

        for(Operation o : operations) {
          // System.out.println(o);
            str +=  o;
        }

       // System.out.println(str);

        return "Transaction Status: " + txnStatus + "\nOperations:\n" + str;
    }

}
