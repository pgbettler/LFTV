

public class Operation {

    public OperationType operationType;
    public int value;
    public int index;
    public int returnValue = 0;

    public Operation(OperationType opType, int value, int index) {
        this.operationType = opType;
        this.value = value;
        this.index = index;
    }


    @Override
    public String toString() {

        String str = operationType.toString();

        if (operationType == OperationType.pushBack || operationType == OperationType.write)
            str += " value " + (Integer.toString(value));

        if (operationType == OperationType.popBack || operationType == OperationType.read)
            str += " returned " + Integer.toString(returnValue);

        if (operationType == OperationType.write || operationType == OperationType.read)
           str += " at index " + Integer.toString(index);

        str += "\n";

        return str;
    }
}