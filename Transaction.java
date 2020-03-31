import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {

    public int size;
    public Operation[] operations;
    public AtomicReference<TxnStatus> status;
    public ConcurrentMap<Integer,RWOperation> set = null;

    public Transaction (Operation[] operations) {
        this.operations = operations;
        if (operations == null) size = 0;
        else this.size = operations.length;
        this.status = new AtomicReference<TxnStatus>(TxnStatus.active);
        this.set = new ConcurrentHashMap<>();
    }

    // this constructor is for populating
    public Transaction(TxnStatus status) {
        this.status = new AtomicReference<TxnStatus>(TxnStatus.committed);
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

        return "Transaction Status: " + status + "\nOperations:\n" + str;
    }

}
