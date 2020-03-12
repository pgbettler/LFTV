/* Group 5
 * COP 4520
 * Project Assignment 1 - Part 1
 * 3/11/2020
 */

import java.util.*; 
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.atomic.AtomicMarkableReference;
// import java.util.concurrent.atomic.AtomicReference;
// import java.util.concurrent.ThreadLocalRandom;


public class sequential {

    public static final int UNSET = Integer.MAX_VALUE;
    public static CompactElement[] CompactVector = new CompactElement[10];
    public static OperationType[] opTypeValues = OperationType.values();
    public static int totalOpTypes = 6;
    public static CompactVector v = new CompactVector();

    public static Random random = new Random();

    public static void main(String[] args) {

        int txnCount = 0;

        while(txnCount < 5) {

            // Create Transaction
            Transaction t = BuildTransaction();
            System.out.println(t);


            // Preprocess transaction - turn into RWSet



            // Perform Transaction



            txnCount++;
        }
    }

    private static Transaction BuildTransaction() {
        
        int operationCount = 0;
        Operation[] operations = new Operation[5];

        while(operationCount < 5) {

            // get random operation type
            int opIndex = random.nextInt(totalOpTypes);
            OperationType opType = opTypeValues[opIndex];

            // get random value to write or push
            int value = random.nextInt(100);

            // choose random index to perform operation on in vector
            // Change the random bounds
            int vectorIndex = random.nextInt(100);

            Operation operation = new Operation(opType, value, vectorIndex);
            operations[operationCount] = operation;

            operationCount++;
        }
        
        
        Transaction t = new Transaction(operations);

        return t;
    }
}
