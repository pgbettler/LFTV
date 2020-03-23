import java.util.Arrays;

public class CompactVector {

    public CompactElement[] array;

    public CompactVector() {

        this.array = new CompactElement[10];

        for(int i = 0; i < this.array.length; i++) {

            CompactElement elem = new CompactElement();
            elem.oldValue = Integer.MAX_VALUE;
            elem.newValue = -1;
            elem.desc = new Transaction(TxnStatus.committed);
            this.array[i] = elem;
        }
    }



    public int Read(int index, int oldElem) {
    	
    	// check bounds
    	if(index > this.array.length) 
    	{
    		// return value indicating read failed? 
    		return -1;
    	}
    	// check transaction status
    	if(this.array[index].desc.status.get().equals(TxnStatus.active)) 
    	{
    		// helping must occur for this transaction, so abort?
    		// reference page 6 section 6 paragraph 2 
    		return -1;
    	}
    	if(this.array[index].desc.status.get().equals(TxnStatus.committed)) 
    	{
    		//committed is the only status to return new value
    		return this.array[index].newValue;
    	}
    		
    	return this.array[index].oldValue;
    }


    public void Write(int index, int newval) {
    	
    	if(index < this.array.length) 
    	{
    		this.array[index].oldValue = this.array[index].newValue;
    		this.array[index].newValue = newval;
    	}
    	// Do we need to put anything in the return value to signify success?
    		
    	
    }


    public void PushBack( int newval) {
    	
    	int len = array.length-1;
    	this.array[len].oldValue = this.array[len].newValue;
    	this.array[len].newValue = newval;
    	
    }


    public int PopBack() {
    	int len = array.length-1;
    	int oldVal = this.array[len].oldValue;
    	int newVal = this.array[len].newValue;
    	// check transaction status
    	if(this.array[len].desc.status.get().equals(TxnStatus.active)) 
    	{
    		// helping must occur for this transaction, so abort?
    		// reference page 6 section 6 paragraph 2 
    		return -1;
    	}
    	if(this.array[len].desc.status.get().equals(TxnStatus.committed)) 
    	{
    		//remove last value in array
    		this.array =  Arrays.copyOf(this.array, this.array.length-1);
    		//committed is the only status to return new value
    		return newVal;
    	}
    	this.array =  Arrays.copyOf(this.array, this.array.length-1);
    	return oldVal;

    }


    public int Size() {
    	
    	return this.array.length;

    }

    // reserve new space in the array
    public void Reserve(int index) {
    	
    	//increase array size up to index
    	//"used in reserve calls to indicate desired capacity" so +1 or nah?
    	this.array =  Arrays.copyOf(this.array, index);
    	
    	
    }
}
