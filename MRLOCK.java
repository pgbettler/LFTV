import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

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
        head.set(0);
        tail.set(0);

        for (int i = 0; i < size; i++) {
            buffer[i].bits.set(1);
            buffer[i].seq.set(i);
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
            //if (pos - buffer[spin & mask].seq.get() > mask || !(buffer[spin & mask].bits & r) )
                spin++;
        }

        return pos;
    }

    void unlock(Integer h) {


    }
    
}