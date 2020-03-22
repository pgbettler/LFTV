import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

import sun.tools.tree.BooleanExpression;

class MRLOCK {
    class Cell {
        AtomicReference<Integer> seq;
        BitSet bits;
    }

    Cell[] buffer;
    Integer mask;
    AtomicReference<Integer> head;
    AtomicReference<Integer> tail;

    MRLOCK(Integer size) {
        buffer = new Cell[size];
        mask = size - 1;
        
        // memory_order_relaxed ?
        head = new AtomicReference<Integer>(0);
        tail = new AtomicReference<Integer>(0);

        for (int i = 0; i < size; i++) {
            buffer[i].bits = new BitSet();
            buffer[i].bits.set(0, 64);
            buffer[i].seq = new AtomicReference<Integer>(0);
        }
    }

    Integer lock(BitSet r) {
        Cell c;
        Integer pos;

        while (true) {
            pos = tail.get();
            c = buffer[pos & mask];
            Integer seq = c.seq.get();
            Integer dif = seq - pos;
            if (dif == 0) {
                if (tail.compareAndExchange(pos, pos+1) == pos) 
                    break;
            }
        }
        c.bits = r;
        c.seq.set(pos + 1);
        Integer spin = head.get();

        
       
        while (spin != pos) {

            // the cell is free and recycled OR the request in the cell has no conflict, then advance down the line
            if (pos - buffer[spin & mask].seq.get() > mask || !isConflict(r, buffer[spin & mask].bits)  )
                spin++;
        }

        return pos;
    }


    Boolean isConflict(BitSet set1, BitSet set2 ) {

        BitSet b = (BitSet) set2.clone();

        b.and(set1);

        if(b.isEmpty())
            return false;

        else return true;
    }

    void unlock(Integer h) {

        // clear the cell request at index h 
        buffer[h & mask].bits.clear();

        Integer pos = head.get();

        // while the head's cell request is clear
        while(buffer[pos & mask].bits.isEmpty()) {

            Cell c = buffer[pos & mask];

            Integer seq = c.seq.get();
            Integer dif = seq - (pos + 1);

            if(dif == 0) {
                if(head.compareAndSet(pos, pos + 1)) {

                    // reset and recycle the cell
                    c.bits.set(0, 64);
                    c.seq.set(0);
                }
            }

            pos = head.get();
        }


    }
    
}