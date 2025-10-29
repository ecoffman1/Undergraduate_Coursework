To run the program simply use the run.bash file and change the parameters
    -Usage:java BridgeSimulator <Duration in Seconds> <numEmergencyVehicles>

Description:
    Initilization: 25 cars and trucks are Initilized for a total of 50.
    they are randomly chosen to arrive on the north or south side. 
    Once initialized the thread sleeps for a random value from between 0 to 1000ms 
    to accomplish arriving randomly within the first second.
    After the vehicles a number of emergency vehicles is initialized based on parameter.
    To prevent all emergency vehicles from immediately starting the sleep time for 
    emergency vehicles is randomized using a value from 0 to simulationTime*1000ms.

    Capactity Check: North and South Queues are handled using seperate Semaphores with a capacity of 10. 
    If a thread attempts to aquire from a full Semaphore the thread prints that the
    car was frustrated and terminates. Otherwise the thread moves to wait for the Bridge.
    This is not the case for emergency vehicles which will move to wait regardless of 
    the Semaphore capacity.

    Bridge Operation: To prevent multiple vehicles from crossing the bridge a reentry lock is
    used. This lock works similiarly to a monitor. We have two signals, emergency and regular. 
    each waking one of the specified type of thread. To start each thread grabs a lock which only 
    one can hold at a time. The thread then checks a boolean to determine if the bridge is full. If 
    the thread is an emergency vehicle it increments a counter for how many emergency vehicles are waiting.
    then waits for a signal to wake it. If it is not an emergency vehicle it instead checks the same varaible 
    and if another thread is using it, it waits. If the bridge is empty the thread sets the bridge to full and 
    releases the lock. It then does the critical section and sleeps for the specified time according to vehicle.
    The thread then aquires the lock again and checks for the emergency vehicle count to determine if 
    one is waiting. If it is, then it sends a signal to wake an emergency vehicle. If there is none 
    waiting it wakes another regular vehicle then tells the Semaphore for its direction 
    that it has completed and to free up a spot.