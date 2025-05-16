import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FinalProject113 {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        boolean isRunning = true;

        EmployeeManagement employeeManagement = new EmployeeManagement(); //belvia
        AttendanceTracking attendanceTracking = new AttendanceTracking();
        PayrollManagement payrollManagement = new PayrollManagement();
        LeaveManagement leaveManagement = new LeaveManagement();

        employeeManagement.loadEmployeesFromFile();
        attendanceTracking.loadAttendanceFromFile();
        leaveManagement.loadLeaveRequestsFromFile();

        String userRole = authenticateLogin(scnr);
        if (userRole == null) {
            System.out.println("Invalid login credentials. Exiting system.");
            scnr.close();
            return;
        }

        while (isRunning) { //belvia
            System.out.println("\nWelcome to the Employee Attendance and Payroll System");
            if (userRole.equals("manager")) {
                System.out.println("1. Employee Management");
                System.out.println("2. Attendance Tracking");
                System.out.println("3. Payroll Management");
                System.out.println("4. Leave Management");
                System.out.println("5. Exit");
            } else if (userRole.equals("employee")) {
                System.out.println("1. Employee Check-in");
                System.out.println("2. Attendance Tracking");
                System.out.println("3. Leave Management");
                System.out.println("4. Exit");
            }

            System.out.print("Select an option: ");
            try {
                int choice = Integer.parseInt(scnr.nextLine()); // Safe input handling 

                if (userRole.equals("manager")) { //belvia
                    switch (choice) {
                        case 1:
                            employeeManagement.manageEmployees(scnr);
                            break;
                        case 2:
                            attendanceTracking.trackAttendance(scnr, userRole);
                            break;
                        case 3:
                            payrollManagement.processPayroll(scnr);
                            break;
                        case 4:
                            leaveManagement.manageLeave(scnr);
                            break;
                        case 5:
                            System.out.println("Exiting the system...");
                            isRunning = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                } else if (userRole.equals("employee")) {
                    switch (choice) {
                        case 1:
                            attendanceTracking.employeeCheckIn(scnr);  // Added Employee Check-in option
                            break;
                        case 2:
                            attendanceTracking.trackAttendance(scnr, userRole);
                            break;
                        case 3:
                            leaveManagement.manageLeaveEmployee(scnr, userRole);
                            break;
                        case 4:
                            System.out.println("Exiting the system...");
                            isRunning = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }
            } catch (NumberFormatException e) { // esther
                System.out.println("Invalid input. Please enter a number.");
            }
        }


        employeeManagement.saveEmployeesToFile();
        attendanceTracking.saveAttendanceToFile();
        leaveManagement.saveLeaveRequestsToFile();

        scnr.close();
    }

    public static String authenticateLogin(Scanner scnr) {
        String[][] credentials = {
                {"manager", "manager123"}, // Manager credentials
                {"employee", "employee123"} // Employee credentials
        };

        System.out.println("Enter username: ");
        String username = scnr.nextLine();

        System.out.println("Enter password: ");
        String password = scnr.nextLine();

        for (String[] credential : credentials) {
            if (username.equals(credential[0]) && password.equals(credential[1])) {
                return credential[0];
            }
        }
        return null;
    }
}

// EmployeeManagement class
class EmployeeManagement { // esther
    private List<Employee> employees;

    public EmployeeManagement() {
        employees = new ArrayList<>();
    }

    public void manageEmployees(Scanner scnr) {
        System.out.println("Employee Management - Managing employees...");
        System.out.println("1. Add Employee");
        System.out.println("2. List Employees");
        System.out.print("Select an option: ");
        int choice = Integer.parseInt(scnr.nextLine());

        switch (choice) {
            case 1: addEmployee(scnr); break;
            case 2: listEmployees(); break;
            default: System.out.println("Invalid option."); break;
        }
    }

    private void addEmployee(Scanner scnr) {
        System.out.print("Enter employee name: ");
        String name = scnr.nextLine();
        System.out.print("Enter role (manager/employee): ");
        String role = scnr.nextLine();
        System.out.print("Enter salary: ");
        double salary = Double.parseDouble(scnr.nextLine());

        employees.add(new Employee(name, role, salary));
        System.out.println("Employee added successfully.");
    }

    private void listEmployees() {
        System.out.println("Employee List:");
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
 
    public List<Employee> getEmployees() { //esther
        return employees;
    }

    // Load employees from file
    public void loadEmployeesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String role = data[1];
                double salary = Double.parseDouble(data[2]);
                employees.add(new Employee(name, role, salary));
            }
        } catch (IOException e) {
            System.out.println("Error loading employee data: " + e.getMessage());
        }
    }

    // Save employees to file
    public void saveEmployeesToFile() { //tracy
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt"))) {
            for (Employee employee : employees) {
                writer.write(employee.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving employee data: " + e.getMessage());
        }
    }
}

// Employee class
class Employee { //tracy
    String name;
    String role;
    double salary;

    public Employee(String name, String role, double salary) {
        this.name = name;
        this.role = role;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return name + "," + role + "," + salary;
    }
}

// AttendanceTracking class
class AttendanceTracking {
    private List<String> attendance;
    private Map<String, LocalDateTime> checkInTimes;

    public AttendanceTracking() {
        attendance = new ArrayList<>();
        checkInTimes = new HashMap<>();
    }

    public void trackAttendance(Scanner scnr, String userRole) {
        System.out.println("Attendance Tracking - Tracking attendance...");
        String employeeName = userRole;
        if(!userRole.equalsIgnoreCase("employee")){
            System.out.print("Enter employee name to track attendance: ");
            userRole = scnr.nextLine();
        }
        attendance.add(employeeName + " - Present on all days.");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Day " + i + " present");
        }

    }

    public void employeeCheckIn(Scanner scnr) { // tracy
        System.out.print("Enter your name for check-in: ");
        String employeeName = scnr.nextLine();

        if (!checkInTimes.containsKey(employeeName)) {
            LocalDateTime checkInTime = LocalDateTime.now();
            checkInTimes.put(employeeName, checkInTime);
            System.out.println("Check-in successful for " + employeeName + " at " + checkInTime);
        } else {
            System.out.println("You are already checked in.");
        }
    }

    // Load attendance from file
    public void loadAttendanceFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                attendance.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading attendance data: " + e.getMessage());
        }
    }

    // Save attendance to file
    public void saveAttendanceToFile() { // Renee
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("attendance.txt"))) {
            for (String record : attendance) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance data: " + e.getMessage());
        }
    }

    // Manager can view employee check-in times
    public void viewCheckInTimes() {
        System.out.println("\n--- Employee Check-in Times ---");
        for (Map.Entry<String, LocalDateTime> entry : checkInTimes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// PayrollManagement class
class PayrollManagement {
    public void processPayroll(Scanner scnr) {
        System.out.println("Payroll Management - Processing payroll...");
        try {
            System.out.print("Enter the number of employees: ");
            int numberOfEmployees = Integer.parseInt(scnr.nextLine());

            double fixedSalary = 3000.00;

            for (int i = 1; i <= numberOfEmployees; i++) {
                System.out.println("Processing Monthly Payroll for Employee " + i);

                System.out.print("Enter employee name: ");
                String employeeName = scnr.nextLine();

                System.out.println(employeeName + ", your Monthly salary is: " + fixedSalary);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }
}

// LeaveManagement class
class LeaveManagement { // renee
    private List<String> leaveRequests;

    public LeaveManagement() {
        leaveRequests = new ArrayList<>();
    }

    public void manageLeave(Scanner scnr) {
        System.out.println("Leave Management - Managing leave requests...");
        try {
            System.out.print("Enter the number of leave requests to process: ");
            int numberOfRequests = Integer.parseInt(scnr.nextLine());

            for (int i = 1; i <= numberOfRequests; i++) {
                System.out.print("Enter employee name: ");
                String employeeName = scnr.nextLine();

                System.out.print("Enter leave type (Sick or Vacation): ");
                String leaveType = scnr.nextLine();

                System.out.print("Enter number of leave days: ");
                int leaveDays = Integer.parseInt(scnr.nextLine());

                leaveRequests.add(employeeName + " - " + leaveType + " leave for " + leaveDays + " days.");
                System.out.println("Leave Request for " + employeeName + " recorded.");
            }
            System.out.println("All leave requests processed.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    public void manageLeaveEmployee(Scanner scnr, String employeeName) { // renee
        System.out.println("Leave Management - Managing leave requests...");
        try {
            System.out.print("Enter leave type (Sick or Vacation): ");
            String leaveType = scnr.nextLine();

            System.out.print("Enter number of leave days: ");
            int leaveDays = Integer.parseInt(scnr.nextLine());

            leaveRequests.add(employeeName + " - " + leaveType + " leave for " + leaveDays + " days.");
            System.out.println("Leave Request for " + employeeName + " recorded.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    // Load leave requests from file
    public void loadLeaveRequestsFromFile() { //esther
        try (BufferedReader reader = new BufferedReader(new FileReader("leave_requests.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                leaveRequests.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading leave requests: " + e.getMessage());
        }
    }

    // Save leave requests to file
    public void saveLeaveRequestsToFile() { // tracy
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("leave_requests.txt"))) {
            for (String request : leaveRequests) {
                writer.write(request);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving leave requests: " + e.getMessage());
        }
    }
}
