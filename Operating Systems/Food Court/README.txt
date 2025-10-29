To run the program simply use the run.bash file and change the parameters
    -Usage:java FoodCourtSimulator <Duration in Seconds> <numCustomers>

Description:
    Initilization: Desired number of customers is initalized(default is 250) as runnables. Each customer has an id(its number) 
    and decides randomly on which counter it will choose.

    Capactity Check: The waiting area is a Semaphore with 100 permits. I use the tryAquire() method to make any customers who
    arrive when there is already 100 people in the waiting area.

    Food Court Operation: To operate the food court, there is an additional 4 Semaphores and 2 mutexs. 1 Semaphore of size 40 
    for each of the three counters, 1 semaphore of size 10 for the cashiers, 1 mutex to protect the updating and accessing of 
    the counter of the number of people in each counter, and 1 mutex to protect the updating and accessing of the counter of
    the number of people at the registers. Each customer first aquires a permit for the waiting area then aquires a permit for
    the counter they chose randomly in initalization. Next, the customer aquires the mutex, updates and prints out the count, 
    releases the mutex, then waits randomly between 1 and 10 seconds. Then the customer must aquire the cashier semaphore then uses
    the counter mutex to update the counter to reflect the customer leaving. Next the customer must aquire the cashier mutex and repeat 
    the same steps as aquiring the counter mutex and instead does so for the register counter. Then the customer waits and aquires the same
    mutex to update the count to reflect leaving the register. The customer then releases the cashier semaphore and releases the waiting area
    semaphore.