Hi, my name is Aryan Kancherla, an undergraduate student at the University of Illinois Urbana-Champaign studying Computer Science + Education. This repository contains my answer to the 
Elevator Back-end Coding Challenge: Simulating an Elevator. Here is a brief breakdown of the features included:

- Uses parallel vectors to store destination requests (for "people" inside the elevator) and pickup requests (for "people" outside the elevator)
- Uses the Scanner class to allow a user to interact with this elevator simulation (so it's not hard-coded)
- Uses an algorithm that keeps the elevator moving in one direction until there are no more requests left, before moving in the other direction (which is more efficient than constantly going up and down)
- Incorporated multiple "Thread" objects, which simulates moving the elevator to the next floor after a certain time period and also allows you to make elevator requests while it is running
- Basic validation, such as handling invalid elevator floor numbers and duplicate requests
