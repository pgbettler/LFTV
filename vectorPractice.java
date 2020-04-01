


public class vectorPractice {

    public static CompactLFTV v;


    public static void main(String[] args) {

        v = new CompactLFTV();

        // Prepopulate vector
        
        for(int i = 0; i < 6; i++) {
            v.Populate();
            System.out.println();
        }
        

        System.out.println("\n\n" + "The size is " + v.size.get());

        


    }
}