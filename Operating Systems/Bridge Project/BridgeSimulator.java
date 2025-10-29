//Ethan Coffman 011024098
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.concurrent.locks.*;
import java.util.List;
import java.util.ArrayList;

class Vehicle implements Runnable {
    // Instance Variables
    private int id;
    private String type;
    private String direction;
    private int crossingTime; // in milliseconds
    private final Random generator = new Random();

    private static int turned_away = 0;
    private static int vehiclesCrossed = 0;

    // Shared Variables (Static)
    private static final ReentrantLock bridgeLock = new ReentrantLock(true); // Fair lock
    private static final Condition emergencyCondition = bridgeLock.newCondition();
    private static final Condition regularCondition = bridgeLock.newCondition();
    private static int emergencyWaiting = 0;
    private static boolean bridgeInUse = false;

    private static final Semaphore northQueueSem = new Semaphore(10); // Max 10 vehicles in North queue
    private static final Semaphore southQueueSem = new Semaphore(10); // Max 10 vehicles in South queue


public static void getSummary(){
    System.out.println();
    System.out.printf("Report: Total Crossed: %d, Vehicles Turned Away: %d", vehiclesCrossed, turned_away);
}

//constructor Vehicle
Vehicle(int id, String type, String direction) {
    this.id = id;
    this.type = type;
    this.direction = direction;
    if(this.type.equals("Car")){
        this.crossingTime = 2;
    } else if(this.type.equals("Truck")){
        this.crossingTime = 3;
    } else {
        this.crossingTime = 1;
    }
}

 private void approachBridge() {
        try {
            if(this.type.equals("Emergency")){
                Thread.sleep(this.generator.nextInt(BridgeSimulator.simulationTime*1000));
            } else {
                Thread.sleep(this.generator.nextInt(1000));
            }
            System.out.printf("%s %d from %s approaches the bridge.\n", type, id, direction);

            if (type.equals("Emergency")) {
                // Emergency vehicles proceed without queue limitations
                System.out.printf("\t\t%s %d from %s is waiting to cross (Emergency vehicle has priority).\n", type, id, direction);
                waitAndCrossBridge();
            } else {
                // Regular vehicles try to enter their respective direction queues
                boolean addedToQueue = false;
                if (direction.equals("North")) {
                    if(northQueueSem.tryAcquire()){
                        addedToQueue = true;
                        System.out.printf("\t\t%s %d from %s is waiting to cross. Waiting (North): %d\n", type, id, direction,10-northQueueSem.availablePermits()); //How to display total in North Queue?);
                    }
                } else {
                    if(southQueueSem.tryAcquire()) {
                        addedToQueue = true;
                        System.out.printf("\t\t%s %d from %s is waiting to cross. Waiting (South): %d\n", type, id, direction,10-southQueueSem.availablePermits());//How to display total in South Queue?));
                    }
                }   
                if(!addedToQueue){
                    turned_away++;
                    System.out.printf("\t%s %d from %s leaves in frustration due to excessive wait (%s Queue full).\n", type, id, direction, direction);
                    return;
                }
                waitAndCrossBridge();
            }
        } catch (InterruptedException e) {
            // Thread was interrupted
        }
    }


    private void waitAndCrossBridge() {
        
        try {
            bridgeLock.lock();
            try {
                if(this.type.equals("Emergency")){
                    emergencyWaiting++;
                    if(bridgeInUse) {
                        emergencyCondition.await(); //  the thread to wait and release bridgeLock
                    }
                    bridgeInUse = true;
                    emergencyWaiting--;
                } else {
                    if(bridgeInUse) {
                        regularCondition.await(); //  the thread to wait and release bridgeLock
                    } 
                    bridgeInUse = true;
                }
            } finally {
                bridgeLock.unlock();
            }

            // Cross the bridge
            crossBridge();


            //post crossing the bridge
            //After crossing, the vehicle needs to update the bridge's state and notify other waiting vehicles.

            bridgeLock.lock();
            try {
                bridgeInUse = false;
                if(emergencyWaiting > 0){
                    emergencyCondition.signal();
                } else {
                    regularCondition.signal();
                }
            } finally {
                bridgeLock.unlock();
            }

            
            if(!this.type.equals("Emergency")){
                if(this.direction.equals("North")){
                    northQueueSem.release();
                } else {
                    southQueueSem.release();
                }
            }
        } catch (InterruptedException e) {
            // Thread was interrupted
        }
    }


private void crossBridge() {
        try {
            System.out.printf("\t\t\t\t%s %d from %s starts crossing the bridge.\n", type, id, direction);

            Thread.sleep(this.crossingTime * 1000);

            System.out.printf("\t\t\t\t\t%s %d from %s has crossed the bridge.\n", type, id, direction);
            vehiclesCrossed++;

        } catch (InterruptedException e) {
            // Thread was interrupted
        }
    }

    @Override
    public void run() {
        approachBridge();
    }
}

public class BridgeSimulator {
    public static int simulationTime = 30;
    public static void main(String[] args) {
        // Default values
        int numEmergencyVehicles = 2;

        if (args.length == 2) {
            simulationTime = Integer.parseInt(args[0]);
            numEmergencyVehicles = Integer.parseInt(args[1]);
            System.out.printf("Simulation time = %d seconds\t", simulationTime);
            System.out.printf("Number of Emergency Vehicles = %d\n", numEmergencyVehicles);
        } else {
            System.out.println("Usage: java BridgeSimulator <simulation time> <number of emergency vehicles>");
            System.out.println("Using default values.");
            System.out.printf("Simulation time = %d seconds\t", simulationTime);
            System.out.printf("Number of Emergency Vehicles = %d\n", numEmergencyVehicles);
        }


        //set number of car, truck and calculater total number of vehicle

        // Time info
        long startTime = System.currentTimeMillis();

        List<Thread> vehicleThreads = new ArrayList<>();
        Random random = new Random();

        // Generate Cars and Trucks
        for (int i = 0; i < 50; i++) {
            int elapsedTime = (int) ((System.currentTimeMillis() - startTime));

            //calculate elapsedTime
            if (elapsedTime <= simulationTime){
                // set type as car or Truck
                String type;
                String direction;
    
                if(i%2 == 0){
                    type = "Car";
                } else {
                    type = "Truck";
                }
                if(random.nextBoolean()){
                    direction = "North";
                } else {
                    direction = "South";
                }
                Vehicle v = new Vehicle(i, type, direction);
                Thread t = new Thread(v);
                vehicleThreads.add(t);
                t.start();
            } else {
                System.out.println("Simulation time has expired. No new vehicles allowed.");
                break;
            }
        }

        // Generate Emergency Vehicles
        for(int i = 0; i < numEmergencyVehicles;i++){
            int elapsedTime = (int) ((System.currentTimeMillis() - startTime));
            if (elapsedTime <= simulationTime){
                String direction;
                String type = "Emergency";
                if(random.nextBoolean()){
                    direction = "North";
                } else {
                    direction = "South";
                }
                Vehicle v = new Vehicle(i + 49, type, direction);
                Thread t = new Thread(v);
                vehicleThreads.add(t);
                t.start();
            } else {
                System.out.println("Simulation time has expired. No new vehicles allowed.");
                break;
            }
        }    

        // Wait for all vehicle threads to finish
        for (Thread vehicleThread : vehicleThreads) {
            try {
                vehicleThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int total = 50 + numEmergencyVehicles;
        System.out.println("");
        System.out.println("Simulation concludes after " + simulationTime + " seconds.");
        System.out.printf("Total Vehicles: %d, Cars: 25, Trucks: 25, Emergency Vehicles: %d", total, numEmergencyVehicles);
        Vehicle.getSummary();
        
    }
}