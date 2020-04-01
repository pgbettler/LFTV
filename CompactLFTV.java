import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

//import org.graalvm.compiler.nodes.calc.IntegerDivRemNode.Op;

public class CompactLFTV {
    private class BucketAndIndex {
        int bucket;
        int indexInBucket;
        BucketAndIndex(int bucket, int indexInBucket) {
            this.bucket = bucket;
            this.indexInBucket = indexInBucket;
        }
    }

    private static final int FIRST_BUCKET_CAPACITY = 8;
    private static final int BUCKETS_LENGTH = 30;
    private static final int UNSET = Integer.MAX_VALUE;
    public AtomicReference<Integer> size;
    private final AtomicReferenceArray<AtomicReferenceArray<CompactElement>> buckets;

    public CompactLFTV() {
        size = new AtomicReference<Integer>(0);
        buckets = new AtomicReferenceArray<AtomicReferenceArray<CompactElement>>(BUCKETS_LENGTH);
        buckets.set(0, new AtomicReferenceArray<CompactElement>(FIRST_BUCKET_CAPACITY));
    }

    public void Populate() {
     
        int s = size.get();

        BucketAndIndex bai = calculateWhichBucketAndIndex(s);

        if(buckets.get(bai.bucket).get(bai.indexInBucket) == null) {
           

            Transaction t = new Transaction(TxnStatus.committed);


            t.operations = new Operation[1];
            int v = ThreadLocalRandom.current().nextInt(50);
            Operation op = new Operation(OperationType.pushBack, v, s);
            t.operations[0] = op;

            RWOperation rwop = new RWOperation();
            rwop.lastWriteOp = op;
            t.set.put(s, rwop);

            CompactElement c = new CompactElement();
            c.oldValue = Integer.MAX_VALUE;
            c.newValue = v;
            c.desc = t;
            
            size.compareAndExchange(s, s+1);

            buckets.get(bai.bucket).compareAndSet(s, null, c);
            
        }
        
    }

    private BucketAndIndex calculateWhichBucketAndIndex(int index) {
        // Account for initial capacity being 8 instead of 0
        int x = index + FIRST_BUCKET_CAPACITY;

        // Each leading bit difference between x and the first bucket's capacity increases the 
        // bucket index by 1 since each bucket capacity is 2 times the previous buckets capacity
        int bucket = Integer.numberOfLeadingZeros(FIRST_BUCKET_CAPACITY) - Integer.numberOfLeadingZeros(x);

        // xor to trim x to be within the capacity of the correct bucket
        int indexInBucket = Integer.highestOneBit(x) ^ x;
        return new BucketAndIndex(bucket, indexInBucket);
    }

    public boolean UpdateElem(int index, CompactElement newElem) {
        BucketAndIndex bucketAndIndex = calculateWhichBucketAndIndex(index);
        CompactElement oldElem;
        RWOperation op;

        //System.out.println("Grabbing index " + index);
        do {
            int s = size.get();

            if (index >= s) {
                newElem.desc.status.set(TxnStatus.aborted);
                return false;
            } else {
                oldElem = buckets.get(bucketAndIndex.bucket).get(bucketAndIndex.indexInBucket);
                if(oldElem == null)
                    System.out.println("we got a null ");
            }

            if (newElem.desc.status.get() != TxnStatus.active) {
                return true;
            }

            // Should i be storing the desc or accessing it every time?
            if(newElem.desc == null)
                System.out.println("new desc is null");
            if(oldElem.desc == null)
                System.out.println("old desc is null");
            if (oldElem.desc == newElem.desc) {
                return true;
            }

            // Should i be storing the desc.status or accessing it every time?
            while (oldElem.desc.status.get() == TxnStatus.active) {
                //completeTransaction(oldElem.desc, index)
            }

            if (oldElem.desc.status.get() == TxnStatus.committed && oldElem.desc.set.get(index).lastWriteOp != null) {
                newElem.oldValue = oldElem.newValue;
            } else {
                newElem.oldValue = oldElem.oldValue;
            }

            op = newElem.desc.set.get(index);
            if (op.checkBounds == true && newElem.oldValue == UNSET) {
                newElem.desc.status.set(TxnStatus.aborted);
                return false;
            }

        } while (!buckets.get(bucketAndIndex.bucket).compareAndSet(bucketAndIndex.indexInBucket, oldElem, newElem));

        op = newElem.desc.set.get(index);
        for (int i = 0; i < op.readList.size(); i++) {
            op.readList.get(i).returnValue = newElem.oldValue;
        }

        return true;
    }


    // Read an element from shared memory - For reads not followed by a write
    public int ReadElement(int index) {

        BucketAndIndex bai = calculateWhichBucketAndIndex(index);
        CompactElement c = buckets.get(bai.bucket).get(bai.indexInBucket);

        if(c == null) {
            System.err.println("Theh element is null in readElement()");
        }
        if(c.desc.status.get() == TxnStatus.committed)
            return c.newValue;
        else
            return c.oldValue;
    }

    public void PrintVector() {

        for(int i = 0; i < size.get(); i++) {
            CompactElement c = buckets.get(0).get(i);
            System.out.println(c);
        }
    }

    public void Reserve(int newCapacity) {
        int currentSize = size.get();
        int x = currentSize + FIRST_BUCKET_CAPACITY - 1;
        int index = Integer.numberOfLeadingZeros(FIRST_BUCKET_CAPACITY) - Integer.numberOfLeadingZeros(x);
        if (index < 1) index = 1;

        int capacity = buckets.get(index - 1).length();
        while (index < Integer.numberOfLeadingZeros(FIRST_BUCKET_CAPACITY) - Integer.numberOfLeadingZeros(newCapacity + FIRST_BUCKET_CAPACITY -1)) {
            index++;
            capacity *= 2;
            buckets.compareAndSet(index, null, new AtomicReferenceArray<CompactElement>(capacity));
        }
    }

}