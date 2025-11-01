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
    
    public void addDestination(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("Invalid floor: " + floor);
            return;
        }
        if (floor == currentFloor) {
            System.out.println("Already at floor " + floor);
            return;
        }
        if (!destinationQueue.contains(floor)) {
            destinationQueue.add(floor);
            System.out.println("Added destination: Floor " + floor);
        } else {
            System.out.println("Floor " + floor + " already in queue");
        }
    }
    
    public void addPickupRequest(int floor, Direction direction) {
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("Invalid floor: " + floor);
            return;
        }
        
        for (ElevatorRequest req : pickupQueue) {
            if (req.floor == floor && req.direction == direction) {
                System.out.println("Request already exists for " + req);
                return;
            }
        }
        
        ElevatorRequest request = new ElevatorRequest(floor, direction);
        pickupQueue.add(request);
        System.out.println("Added pickup request: " + request);
    }
    
    public void step() {
        if (destinationQueue.isEmpty() && pickupQueue.isEmpty()) {
            currentDirection = Direction.IDLE;
            return;
        }
        
        Integer nextFloor = getNextDestination();
        
        if (nextFloor == null) {
            currentDirection = Direction.IDLE;
            return;
        }
        
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
        checkArrival();
        
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private Integer getNextDestination() {
        if (currentDirection == Direction.UP || currentDirection == Direction.IDLE) {
            Integer upDestination = getNextFloorInDirection(Direction.UP);
            if (upDestination != null) return upDestination;
        }
        
        if (currentDirection == Direction.DOWN || currentDirection == Direction.IDLE) {
            Integer downDestination = getNextFloorInDirection(Direction.DOWN);
            if (downDestination != null) return downDestination;
        }
        
        if (!destinationQueue.isEmpty()) {
            return destinationQueue.get(0);
        }
        
        if (!pickupQueue.isEmpty()) {
            return pickupQueue.get(0).floor;
        }
        
        return null;
    }
    
    private Integer getNextFloorInDirection(Direction dir) {
        Integer closest = null;
        
        for (Integer floor : destinationQueue) {
            if (dir == Direction.UP && floor >= currentFloor) {
                if (closest == null || floor < closest) {
                    closest = floor;
                }
            } else if (dir == Direction.DOWN && floor <= currentFloor) {
                if (closest == null || floor > closest) {
                    closest = floor;
                }
            }
        }
        
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
    
    private void checkArrival() {
        if (destinationQueue.contains(currentFloor)) {
            destinationQueue.remove(Integer.valueOf(currentFloor));
            System.out.println(">>> ARRIVED at floor " + currentFloor + " (destination) <<<");
        }
        
        Iterator<ElevatorRequest> iter = pickupQueue.iterator();
        while (iter.hasNext()) {
            ElevatorRequest req = iter.next();
            if (req.floor == currentFloor) {
                // Pick up if: directions match, elevator is idle, or we're at a terminal floor
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
        System.out.println("\n--- ELEVATOR STATUS ---");
        System.out.println("Current Floor: " + currentFloor);
        System.out.println("Direction: " + currentDirection);
        System.out.println("Destinations: " + (destinationQueue.isEmpty() ? "None" : destinationQueue));
        System.out.println("Pickup Requests: " + (pickupQueue.isEmpty() ? "None" : pickupQueue));
        System.out.println("----------------------\n");
    }
    
    public int getCurrentFloor() { return currentFloor; }
    public Direction getCurrentDirection() { return currentDirection; }
    public boolean hasRequests() { return !destinationQueue.isEmpty() || !pickupQueue.isEmpty(); }
}