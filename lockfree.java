
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;




public class lockfree {

    public static int totalOpTypes = 6;
    public static final int UNSET = Integer.MAX_VALUE;
    public static OperationType[] opTypeVals = OperationType.values();

    public static void main(String[] args) {


        // Prepopulate compact LFTV with pushback operations;



        // Initialize threads array
        Thread[] threads = new Thread[3];           // ******** VARY NUMBER OF THREADS *********

        // Create Threads
        for(int i = 0; i < threads.length; i++)
        {
            Transaction[] transactions = BuildTransactions();
            Thread t = new Thread(new Perform(transactions));       // ******** PASS IN LFTV REFERENCE TOO *********

            t.setName(String.valueOf(i+1));
            threads[i] = t;
        }

        // Start timer
        long startTime = System.currentTimeMillis();

        // Start threads
        for(int i = 0; i < threads.length; i++)
            threads[i].start();

        // Join threads
        try {
            for (Thread thread : threads) 
                thread.join();
        } catch(InterruptedException e){
            System.out.println("Threads interrupted");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time = " + (endTime - startTime) + "ms\n");
    }



    // Creates an array of transactions for a thread to pull from
    public static Transaction[] BuildTransactions() {

        Transaction[] transactions = new Transaction[3];        // ******** CHANGE THIS NUMBER LATER *********

        // Build each transaction and insert it into transactions array
        for(int x = 0; x < transactions.length; x++) {

            int value;
            int operationCount = 0;
            Operation[] operations = new Operation[5];
    
            // Insert 5 random operations per transaction
            while(operationCount < 5) {

                // get random operation type
                double ratio = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
                OperationType opType = GetOperationType(ratio);
    
                // Popback's value field should always be max integer
                if(opType == OperationType.popBack)
                    value = Integer.MAX_VALUE;
                    
                // get random value to write or push
                else
                    value = ThreadLocalRandom.current().nextInt(100);
    
                // choose random index to perform operation on in vector
                int vectorIndex = ThreadLocalRandom.current().nextInt(100);         // ******** CHANGE THE BOUND *********
    
                Operation operation = new Operation(opType, value, vectorIndex);
                operations[operationCount] = operation;
    
                operationCount++;
            }

            Transaction t = new Transaction(operations);

            transactions[x] = t;
        }

        return transactions;
    }



    // Returns an operation based off the ratio
    public static OperationType GetOperationType(double ratio) {

        // Read operation - 20%                                         // ******** VARY THE RATIOS *********
        if(ratio < 0.2)
            return opTypeVals[0];
    
        // Write operation - 20%
        else if (ratio >= 0.2 && ratio < 0.4)
            return opTypeVals[1];
        
        // Pushback - 20%
        else if (ratio >= 0.4 && ratio < 0.6)
            return opTypeVals[2];

        // Popback - 20%
        else if (ratio >= 0.6 && ratio < 0.8)
            return opTypeVals[3];

        // Size - 10%
        else if (ratio >= 0.8 && ratio < 0.9)
            return opTypeVals[4];

        // Reserve - 10%
        else
            return opTypeVals[5];
    }
}




class Perform implements Runnable {

    public Transaction[] transactions;


    public Perform (Transaction[] t) {              // ******** PASS IN LFTV REFERENCE  *********
        this.transactions = t;
    }



    public void run() {

        for(int x = 0; x < transactions.length; x++) 
            System.out.println("Thread " + Thread.currentThread().getName() + "\n" + transactions[x] + "\n");

        for(int i = 0; i < transactions.length; i++) {

            Transaction t = transactions[i];
            Preprocess(t);
            CompleteTransaction(t);
        }
    }



    private void Preprocess(Transaction t) {

    }

    

    private static Boolean CompleteTransaction(Transaction desc) {

        // call updateElement

        return true;
    }
}