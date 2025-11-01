import java.util.*;

public class Elevator {
    private int currentFloor;
    private Direction currentDirection;
    private List<Integer> destinationQueue;
    private List<ElevatorRequest> pickupQueue;
    private final int minFloor;
    private final int maxFloor;
    private final int speed;
    
    public Elevator(int minFloor, int maxFloor, int speed) {
        this.currentFloor = 1;
        this.currentDirection = Direction.IDLE;
        this.destinationQueue = new ArrayList<>();
        this.pickupQueue = new ArrayList<>();
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.speed = speed;
    }


    // This method simulates a person inside the elevator that is "pressing a button" for the floor they want to go to
    public void addDestination(int floor) {


        // It first makes sure the floor is valid (within the range of floors 1-10 and not the floor the person is already on)
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("Invalid floor: " + floor);
            return;
        }
        if (floor == currentFloor) {
            System.out.println("Already at floor " + floor);
            return;
        }
        if (!destinationQueue.contains(floor)) {
        // Then, it adds the new destination to the "destinationQueue" list
            destinationQueue.add(floor);
            System.out.println("Added destination: Floor " + floor);
        } else {
            System.out.println("Floor " + floor + " already in queue");
        }
    }
    
    // This method simuates a person outside the elevator that wants to go on the elevator
    // takes in the floor as a parameter (same as addDestination()) but also has direction (either Up or Down)
    public void addPickupRequest(int floor, Direction direction) {
        // First, the function checks if the floor is valid
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("Invalid floor: " + floor);
            return;
        }
        // Makes sure the request hasn't been requested already
        for (ElevatorRequest req : pickupQueue) {
            if (req.floor == floor && req.direction == direction) {
                System.out.println("Request already exists for " + req);
                return;
            }
        }
        
        // After checking validity, an ElevatorRequest object is created and added to the pickupQueue list
        ElevatorRequest request = new ElevatorRequest(floor, direction);
        pickupQueue.add(request);
        System.out.println("Added pickup request: " + request);
    }
    
    // This function moves the elevator
    public void step() {
        // If there are no people inside or outside the elevator making requests, then the state of the elevator is "IDLE"
        if (destinationQueue.isEmpty() && pickupQueue.isEmpty()) {
            currentDirection = Direction.IDLE;
            return;
        }
        
        Integer nextFloor = getNextDestination();
        
        if (nextFloor == null) {
            currentDirection = Direction.IDLE;
            return;
        }
        
        // Current floor increases or decreases depending on the next floor and the current floor
        if (nextFloor > currentFloor) {
            currentDirection = Direction.UP;
            currentFloor++;
        } else if (nextFloor < currentFloor) {
            currentDirection = Direction.DOWN;
            currentFloor--;
        } else {
            currentDirection = Direction.IDLE;
        }
        
        if (currentDirection != Direction.IDLE) {
            System.out.println("Elevator moving " + currentDirection + " to floor " + currentFloor);
        }        
        checkArrival(); // Checks to see if the elevator has reached the correct floor 
        
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // This method gets the next destination the elevator should go to
    // The priority is:
    // 1: The same direction the elevator is moving in
    // 2: The opposite direction the elevator is moving in
    // 3: Any destination a person inside the elevator wants to go to
    // 4: Any pickup triggered by a person outside the elevator
    private Integer getNextDestination() {
        if (currentDirection == Direction.UP || currentDirection == Direction.IDLE) {
            Integer upDestination = getNextFloorInDirection(Direction.UP);
            if (upDestination != null) {
                return upDestination;
            }
        
        }
        
        if (currentDirection == Direction.DOWN || currentDirection == Direction.IDLE) {
            Integer downDestination = getNextFloorInDirection(Direction.DOWN);
            if (downDestination != null) { 
                return downDestination;
            }
        }
        
        if (!destinationQueue.isEmpty()) {
            return destinationQueue.get(0);
        }
        
        if (!pickupQueue.isEmpty()) {
            return pickupQueue.get(0).floor;
        }
        
        return null;
    }
    
    // This methods finds the closest floor to go to based on the requests from people inside and outside the elevator
    private Integer getNextFloorInDirection(Direction dir) {
        Integer closest = null;
        
        // If the elevator is currently going up, it will check to make sure the floor requested is greater than the current floor
        for (Integer floor : destinationQueue) {
            if (dir == Direction.UP && floor >= currentFloor) {
                if (closest == null || floor < closest) {
                    closest = floor;
                }

        // If the elevator is currently going down, it will check to make sure the floor requested is less than the current floor
            } else if (dir == Direction.DOWN && floor <= currentFloor) {
                if (closest == null || floor > closest) {
                    closest = floor;
                }
            }
        }
        
        // To avoid the elevator going up and down randomly, I made it so that it only picks up people from the outside if they want to go in the 
        // same direction the elevator is currently going 
        for (ElevatorRequest req : pickupQueue) {
            if (dir == Direction.UP && req.floor >= currentFloor && req.direction == Direction.UP) {
                if (closest == null || req.floor < closest) {
                    closest = req.floor;
                }
            } else if (dir == Direction.DOWN && req.floor <= currentFloor && req.direction == Direction.DOWN) {
                if (closest == null || req.floor > closest) {
                    closest = req.floor;
                }
            }
        }
        
        return closest;
    }
    
    // Checks to see if the elevator has reached its destination
    private void checkArrival() {
        if (destinationQueue.contains(currentFloor)) {
            destinationQueue.remove(Integer.valueOf(currentFloor));
            System.out.println(">>> ARRIVED at floor " + currentFloor + " (destination) <<<");
        }
        
        Iterator<ElevatorRequest> iter = pickupQueue.iterator();
        while (iter.hasNext()) {
            ElevatorRequest req = iter.next();
            if (req.floor == currentFloor) {
                boolean shouldPickup = req.direction == currentDirection || 
                                      currentDirection == Direction.IDLE ||
                                      (currentFloor == minFloor && req.direction == Direction.UP) ||
                                      (currentFloor == maxFloor && req.direction == Direction.DOWN);
                
                if (shouldPickup) {
                    iter.remove();
                    System.out.println(">>> PICKED UP passengers at floor " + currentFloor + " <<<");
                }
            }
        }
    }
    
    public void displayStatus() {
        System.out.println("\n" + "-------------- ELEVATOR STATUS --------------");
        System.out.println("Current Floor: " + currentFloor);
        System.out.println("Direction the elevator is moving in: " + currentDirection);
        System.out.println("Destinations: " + (destinationQueue.isEmpty() ? "None" : destinationQueue));
        System.out.println("Pickup Requests: " + (pickupQueue.isEmpty() ? "None" : pickupQueue));
        System.out.println("----------------------\n");
    }
    
    public int getCurrentFloor() { return currentFloor; }
    public Direction getCurrentDirection() { return currentDirection; }
    public boolean hasRequests() { return !destinationQueue.isEmpty() || !pickupQueue.isEmpty(); }
}