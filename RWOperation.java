import java.util.*;
import java.util.List;

public class RWOperation {
    
    Boolean checkBounds;
    Operation lastWriteOp;
    List<Operation> readList;

    public RWOperation() {
        checkBounds = false;
        lastWriteOp = null;
        readList = new ArrayList<Operation>();
    }

    @Override
    public String toString() {

        return  readList.size() + " bounds " + checkBounds + lastWriteOp;
    }
}