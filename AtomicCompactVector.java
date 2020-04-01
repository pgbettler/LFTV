import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicCompactVector {

    AtomicReferenceArray<CompactElement> array;
    public AtomicCompactVector() {

        this.array = new AtomicReferenceArray<>(new CompactElement[10]);
    }


    public void Size() {

        array.length();
    	
    	//return this.array.length;

    }

    // reserve new space in the array
    public void Reserve(int index) {
    	
    	//increase array size up to index
    	//"used in reserve calls to indicate desired capacity" so +1 or nah?
    	//this.array =  Arrays.copyOf(this.array, index);
    	
    	
    }
}
