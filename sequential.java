/* Group 5
 * COP 4520
 * Project Assignment 1 - Part 1
 * 3/11/2020
 */

import java.util.*; 


public class sequential {

    public static final int UNSET = Integer.MAX_VALUE;
    //public static CompactElement[] CompactVector = new CompactElement[10];
    public static OperationType[] opTypeValues = OperationType.values();
    public static int totalOpTypes = 6;
    public static CompactVector v = new CompactVector();

    public static Random random = new Random();

    public static void main(String[] args) {
                
        Populate();

        for(int i = 0; i < v.array.length; i++) {
            System.out.println(v.array[i]);
        }

        int txnCount = 0;

        while(txnCount < 1) {

            // Create Transaction
            Transaction t = BuildTransaction();
            System.out.println(t);


            // Preprocess transaction - turn into RWSet
            Preprocess(t);


            // Perform Transaction
            Boolean result = CompleteTransaction(t, false);

            if(result)
                System.out.println("Completed\n" + t);


            txnCount++;
        }


        for(int i = 0; i < v.array.length; i++) {
            System.out.println(v.array[i]);
        }
    }

    private static void Preprocess(Transaction t) {

        RWOperation rwop = null;
        int largestReserve = 0;


        for (Operation op : t.operations) {   

            if(t.set.containsKey(op.index))
                rwop = t.set.get(op.index);
            
            else    
                rwop = new RWOperation();

            if(op.operationType == OperationType.read) {
                rwop.readList.add(op);
            }

            else if (op.operationType == OperationType.write) {
                rwop.lastWriteOp = op;
            }

            // same for pushback and popback - keep track of furthest index accessed by pushback

            // do something with size calls and t.size

            // Keep track of largest reserve call
            if(op.operationType == OperationType.reserve) {
               
                if(op.index > largestReserve)
                    largestReserve = op.index;
            }

           //if(rwop.readList.size() != 0 && rwop.lastWriteOp != null)
                t.set.put(op.index, rwop);
        }
        
        Set<Integer> keys = t.set.keySet();

        // System.out.println("\n\nTHE SET  " + keys.size() );
        // for(Integer k : keys) {
        //     System.out.print("Index " + k + " has ");
        //     System.out.println(t.set.get(k));
        // }

        // do something with the largestReserve value

    }


    private static void Populate() {

        int layer = 0;
        

        while(layer < 1) {

            Operation[] operations = new Operation[5];

            for(int i = 0; i < 5; i++) {

                int value =  random.nextInt(100);

                Operation operation = new Operation(OperationType.write, value, i);
                operations[i] = operation;
            }
            Transaction t = new Transaction(operations);

            // System.out.println("pushing ");
            // System.out.println(t);

           // t.status.set(TxnStatus.committed)
            Preprocess(t);
            CompleteTransaction(t, true);

            for(int i = 5; i < 10; i++) {

                int value =  random.nextInt(100);

                Operation operation = new Operation(OperationType.write, value, i);
                operations[i-5] = operation;
            }
            

            Transaction d = new Transaction(operations);
            // System.out.println("pushing ");
            // System.out.println(d);

            Preprocess(d);
            CompleteTransaction(d, true);
            
            layer++;
        }


    } 

 


    private static Transaction BuildTransaction() {
        
        int operationCount = 0;
        Operation[] operations = new Operation[5];
        int value;

        while(operationCount < 5) {

            // get random operation type
            int opIndex = random.nextInt(totalOpTypes);
            OperationType opType = opTypeValues[opIndex];

            // get random value to write or push
            if(opType == OperationType.popBack)
                value = Integer.MAX_VALUE;
                
            else
                value = random.nextInt(100);

            // choose random index to perform operation on in vector
            // Change the random bounds
            int vectorIndex = random.nextInt(10);

            Operation operation = new Operation(opType, value, vectorIndex);
            operations[operationCount] = operation;

            operationCount++;
        }
        
        Transaction t = new Transaction(operations);
        t.size = 5;

        return t;
    }


    private static Boolean CompleteTransaction(Transaction desc, boolean prepopulating) {

        boolean ret = true;

      

            // reserve first 

            Set<Integer> indexes = desc.set.keySet();

            Iterator<Integer> it = indexes.iterator();        

            while(it.hasNext()) {

                int index = it.next();

                RWOperation rwop = desc.set.get(index);

                Boolean result = UpdateElement(desc, index, rwop, prepopulating);

                if(!result) {
                    // do something

                    System.out.println("update element failed\n");
                    return false;
                }

               
            }

            desc.status.set(TxnStatus.committed);
            return true;
        
    }

    private static Boolean UpdateElement(Transaction desc, int index, RWOperation rwop, boolean prepopulating) {
        
        Set<Integer> keys = desc.set.keySet();

       // System.out.println("\n\nRWOp");
      //  System.out.println(rwop);


        // should check if the index is greater than vector's capacity, will check array length for now
        if(index > v.array.length) {
            desc.status.set(TxnStatus.aborted);
            return false;
        }

        CompactElement newElem = new CompactElement();
        CompactElement oldElem = v.array[index];

        if(prepopulating) {
             // should probably get rid of these atomics for the sequential version
            if(oldElem.desc.status.get() == TxnStatus.committed && oldElem.desc.set != null && oldElem.desc.set.get(index).lastWriteOp != null) {

                newElem.oldValue = oldElem.newValue;
            }
            else newElem.oldValue = oldElem.oldValue;
        }
        

        // should probably get rid of these atomics for the sequential version
        if(oldElem.desc.status.get() == TxnStatus.committed && oldElem.desc.set != null && rwop.lastWriteOp != null ) {

                newElem.oldValue = oldElem.newValue;
        }
        else newElem.oldValue = oldElem.oldValue;

        if(rwop == null)
            System.out.println("it don't");

        // accessing out of bounds
        if(rwop.checkBounds && newElem.oldValue == UNSET) {
            desc.status.set(TxnStatus.aborted);
            System.out.println("yeah you guessed it");
            return false;
        }

        // perform the write operation or maybe not
        if(rwop.lastWriteOp != null) {
            newElem.newValue = rwop.lastWriteOp.value;
        }
        

        newElem.desc = desc;

        v.array[index] = newElem;

        for(int i = 0; i < rwop.readList.size(); i++) {
            Operation op = rwop.readList.get(i);
            op.returnValue = newElem.oldValue;
        }


        return true;
    }
}
