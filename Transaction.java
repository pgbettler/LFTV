import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {

    public int size;
    public Operation[] operations;
    public AtomicReference<TxnStatus> txnStatus;
    // This map is atomic but uses locks in its implementation so we might need to look for alternatives
    public ConcurrentMap<Integer,RWOperation> set;

    public Transaction (Operation[] operations) {
        this.size = 5;
        this.operations = operations;
        this.txnStatus = new AtomicReference<TxnStatus>(TxnStatus.active);
        this.set = new ConcurrentHashMap<>();
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
