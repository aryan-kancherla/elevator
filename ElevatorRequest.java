public class ElevatorRequest {
    int floor;
    Direction direction;
    long timestamp;
    
    public ElevatorRequest(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "Floor " + floor + " (" + direction + ")";
    }
}