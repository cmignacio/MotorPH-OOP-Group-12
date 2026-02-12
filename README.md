# MotorPH Employee Management App (OOP Version)

## Overview

This project is an Object-Oriented Programming (OOP) implementation of the MotorPH Employee Management System.

The system manages employees, attendance, and payroll processing using core OOP principles such as:

- Abstraction
- Inheritance
- Polymorphism
- Encapsulation

This version refactors the original procedural design into a structured object-oriented architecture.

---

## OOP Implementation Highlights

### Encapsulation
- Employee attributes are declared as `private`.
- Access to data is controlled through public getter and setter methods.
- Internal data structures are protected from direct modification.

### Abstraction
- `Employee` is implemented as an abstract class.
- Payroll behavior is enforced through abstract methods.
- Common employee attributes are defined in the base class.

### Inheritance
- `RegularEmployee` extends `Employee`.
- The design allows future expansion for other employee types.

### Polymorphism
- `computePay()` is overridden in subclasses.
- `PayrollCalculator` processes employees using base class references.

---

## Features

### Employee Management
- Add, update, and delete employees
- Store personal and government information
- Manage salary and allowances

### Attendance Management
- Record attendance
- Compute lateness and overtime

### Payroll System
- Weekly payroll computation
- Overtime calculations (regular and rest day)
- Government deductions:
  - SSS
  - PhilHealth
  - Pag-IBIG
- Withholding tax computation
- Net pay calculation

---

## Data Storage

The application uses CSV files:

- `data/employees.csv`
- `data/attendance.csv`
- `data/users.csv`

---

## How to Run

1. Open the project in NetBeans.
2. Clean and Build the project.
3. Run the application.
4. Login using credentials from `users.csv`.

---

## Technologies Used

- Java
- Maven
- Swing (GUI)
- CSV File Handling

---

## Course Information

Course: Object-Oriented Programming  
Program: BS Information Technology  

---

## Developers

- Ignacio, Charlene Mae De Venecia  
- Balmes, Ronelyn  
- Petrola, Joey  

