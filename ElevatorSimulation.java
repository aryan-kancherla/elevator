import java.util.Scanner;

public class ElevatorSimulation {
    private static final int MIN_FLOOR = 1;
    private static final int MAX_FLOOR = 10;
    private static final int SPEED_MS = 1000;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Elevator elevator = new Elevator(MIN_FLOOR, MAX_FLOOR, SPEED_MS);
        
        System.out.println("=== ELEVATOR SIMULATION ===");
        System.out.println("Building: Floors " + MIN_FLOOR + " to " + MAX_FLOOR);
        System.out.println();
        
        Thread elevatorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                elevator.step();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        elevatorThread.start();
        
        boolean running = true;
        while (running) {
            printMenu();
            
            String input = scanner.nextLine().trim();
            
            switch (input.toLowerCase()) {
                case "1":
                    handleDestinationRequest(scanner, elevator);
                    break;
                case "2":
                    handlePickupRequest(scanner, elevator);
                    break;
                case "3":
                    elevator.displayStatus();
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        elevatorThread.interrupt();
        scanner.close();
        System.out.println("Simulation ended.");
    }
    
    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Press floor button inside elevator");
        System.out.println("2. Call elevator from a floor");
        System.out.println("3. Display elevator status");
        System.out.println("4. Exit simulation");
        System.out.print("Select option: ");
    }
    
    private static void handleDestinationRequest(Scanner scanner, Elevator elevator) {
        System.out.print("Enter destination floor (" + MIN_FLOOR + "-" + MAX_FLOOR + "): ");
        try {
            int floor = Integer.parseInt(scanner.nextLine().trim());
            elevator.addDestination(floor);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void handlePickupRequest(Scanner scanner, Elevator elevator) {
        System.out.print("Enter floor to call from (" + MIN_FLOOR + "-" + MAX_FLOOR + "): ");
        try {
            int floor = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Direction (U for up, D for down): ");
            String dirInput = scanner.nextLine().trim().toUpperCase();
            
            Direction direction;
            if (dirInput.equals("U")) {
                direction = Direction.UP;
            } else if (dirInput.equals("D")) {
                direction = Direction.DOWN;
            } else {
                System.out.println("Invalid direction. Use U or D.");
                return;
            }
            
            elevator.addPickupRequest(floor, direction);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}