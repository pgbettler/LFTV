import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

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
    private AtomicReference<Integer> size;
    private final AtomicReferenceArray<AtomicReferenceArray<CompactElement>> buckets;

    public CompactLFTV() {
        size = new AtomicReference<Integer>(0);
        buckets = new AtomicReferenceArray<AtomicReferenceArray<CompactElement>>(BUCKETS_LENGTH);
        buckets.set(0, new AtomicReferenceArray<CompactElement>(FIRST_BUCKET_CAPACITY));
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

    private boolean updateElem(int index, CompactElement newElem) {
        BucketAndIndex bucketAndIndex = calculateWhichBucketAndIndex(index);
        CompactElement oldElem;
        RWOperation op;
        do {
            if (index >= size.get()) {
                newElem.desc.status.set(TxnStatus.aborted);
                return false;
            } else {
                oldElem = buckets.get(bucketAndIndex.bucket).get(bucketAndIndex.indexInBucket);
            }

            if (newElem.desc.status.get() != TxnStatus.active) {
                return true;
            }

            // Should i be storing the desc or accessing it every time?
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
                newElem.oldValue = oldElem.newValue;
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
}