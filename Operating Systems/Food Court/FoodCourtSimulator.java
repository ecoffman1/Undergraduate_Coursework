//Ethan Coffman 011024098
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

class Customer implements Runnable {
    // Instance Variables
    private int id;
    private int choice; //which counter 
    private final Random generator = new Random();

    //shared statistics 
    private static int counter1Count = 0;
    private static int counter2Count = 0;
    private static int counter3Count = 0;
    private static int cashierCount = 0;
    private static int turnedaway = 0;

    //shared semaphores
    private static final Semaphore waitingArea = new Semaphore(100); // Max 100 customers in waiting area

    private static final Semaphore counter1 = new Semaphore(40); // Max 40 customers in each counter
    private static final Semaphore counter2 = new Semaphore(40); 
    private static final Semaphore counter3 = new Semaphore(40); 
    
    private static final Semaphore counter1Mutex = new Semaphore(1); //ensures only one person at each counter
    private static final Semaphore counter2Mutex = new Semaphore(1);
    private static final Semaphore counter3Mutex = new Semaphore(1);
    private static final Semaphore counterNum = new Semaphore(1); //ensures security of counter counts
    private static final Semaphore cashierMutex = new Semaphore(1); //ensures security of cashier counts

    private static final Semaphore cashier = new Semaphore(10); // Max 10 customers at cashier



//constructor customer
Customer(int id) {
    this.id = id;
    this.choice = generator.nextInt(3) + 1;
}

 private void enterWaitingArea() {
        System.out.printf("Customer %d enters the food court\n", id);
        if(waitingArea.tryAcquire()){
            System.out.printf("Customer %d enters the waiting area\n", id); 
        } else {
            System.out.printf("The waiting area is full, customer %d left in frustration\n", id);
            turnedaway++;
        }
        waitForCounter();
    }

    private void waitForCounter() {
        
        try {
            if(choice == 1){
                counter1.acquire(); //enter counter waiting area
                waitingArea.release(); //leave waiting area

                counterNum.acquire(); //acquire semaphore to update and print count
                counter1Count++;
                System.out.printf("Customer %d leaves the wait area and joins counter %d(Counter count:%d)\n",id,choice,counter1Count);
                counterNum.release();

                counter1Mutex.acquire(); //acquire semaphore to get food from counter 
                Thread.sleep(this.generator.nextInt(10000)); //Sleep while getting food
                counterNum.acquire(); //acquire semaphore to update count with status of leaving
                counter1Count--;
                counter1.release();
                counterNum.release();
                counter1Mutex.release();

            } else if (choice == 2){
                counter2.acquire(); //enter counter waiting area
                waitingArea.release(); 
                
                counterNum.acquire(); //acquire semaphore to update and print count
                counter2Count++;
                System.out.printf("Customer %d leaves the wait area and joins counter %d(Counter count:%d)\n",id,choice,counter2Count);
                counterNum.release();

                counter2Mutex.acquire(); //acquire semaphore to get food from counter 
                Thread.sleep(this.generator.nextInt(10000)); //Sleep while getting food
                counterNum.acquire(); //acquire semaphore to update count with status of leaving
                counter2.release(); 
                counter2Count--;
                counterNum.release();
                counter2Mutex.release();

            } else {
                counter3.acquire(); //enter counter waiting area
                waitingArea.release();

                counterNum.acquire(); //acquire semaphore to update and print count
                counter3Count++;
                System.out.printf("Customer %d leaves the wait area and joins counter %d(Counter count:%d)\n",id,choice,counter3Count);
                counterNum.release();

                counter3Mutex.acquire(); //acquire semaphore to get food from counter 
                Thread.sleep(this.generator.nextInt(10000)); //Sleep while getting food
                counterNum.acquire(); //acquire semaphore to update count with status of leaving
                counter3.release();
                counter3Count--;
                counterNum.release();
                counter3Mutex.release();
            }


            cashierMutex.acquire(); //acquire the semaphore to update and print out register number
            cashierCount++;
            System.out.printf("Customer %d leaves counter %d and moves to the register(Register Count: %d)\n",id,choice,cashierCount);
            cashierMutex.release();

            cashier.acquire(); //enter one of 10 registers
            Thread.sleep(this.generator.nextInt(10000)); //Sleep after getting to register
            cashierMutex.acquire(); //update count to reflect leaving
            cashierCount--;
            cashier.release();
            System.out.printf("Customer %d leaves the food court\n",id);
            cashierMutex.release();

        } catch (InterruptedException e) {
            // Thread was interrupted
        }
    }

    @Override
    public void run() {
        enterWaitingArea();
    }
}

public class FoodCourtSimulator {
    public static int simulationTime = 200;
    public static void main(String[] args) {
        // Default values
        int numCustomers = 250;

        if (args.length == 2) {
            simulationTime = Integer.parseInt(args[0]);
            numCustomers= Integer.parseInt(args[1]);
            System.out.printf("Simulation time = %d seconds\t", simulationTime);
            System.out.printf("Number of customers = %d\n", numCustomers);
        } else {
            System.out.println("Usage: java FoodCourtSimulator <simulation time> <number of customers>");
            System.out.println("Using default values.");
            System.out.printf("Simulation time = %d seconds\t", simulationTime);
            System.out.printf("Number of customers = %d\n", numCustomers);
        }



        // Time info
        long startTime = System.currentTimeMillis();
        List<Thread> CustomerThreads = new ArrayList<>();

        // Generate customers
        for (int i = 0; i < numCustomers; i++) {
            int elapsedTime = (int) ((System.currentTimeMillis() - startTime));

            //calculate elapsedTime
            if (elapsedTime <= simulationTime){
                Customer v = new Customer(i);
                Thread t = new Thread(v);
                CustomerThreads.add(t);
                t.start();
            } else {
                System.out.println("Simulation time has expired. No new customers allowed.");
                break;
            }
        }
 

        // Wait for all customer threads to finish
        for (Thread customerThread : CustomerThreads) {
            try {
                customerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("");
        System.out.println("Simulation concludes after " + simulationTime + " seconds.");
    }
}