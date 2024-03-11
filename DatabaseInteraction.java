/*
* Course: COMP3005
* Project: Assignment 3, Question 1
* Author: Kinjal Kamboj
* Date Created: March 10, 2024 
*/

import java.util.*;
import java.sql.*;
import java.sql.Date;

public class DatabaseInteraction {

    //Global variable
    static Connection connection;
    public static void main(String[] args){

        //PostgreSQL Database URL, username and password
        String url = "jdbc:postgresql://localhost:5432/COMP3005_A3_Students_DB";
        String username = "postgres";
        String password = "postgres";

        //connect to database
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null){
                System.out.println("Connected to the database!");
            }
            else{
                System.out.println("Failed to connect to the database!");
            }

            selectionLoop(); //start looping to ask user for input
            connection.close();
        }

        catch (SQLException e){
            System.out.println("Connection failed: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Connection failed: ClassNotFoundException");
        }
    }

    // loop for user input
    static void selectionLoop (){
        try (Scanner scanner = new Scanner(System.in)) {
            while (true){
                System.out.println("\nWhat would you like to do? Please enter a number to select an option. And 0 to exit the program.");
                System.out.println("\n(1) Retrieve and display all records from the students table" +
                               "\n(2) Add a new student record" + 
                               "\n(3) Update the email address for a student" +
                               "\n(4) Delete a student record");
                               
                int userInput = scanner.nextInt(); //store user input
                
                if (userInput == 0){
                    System.out.println("Exiting program. Goodbye!");
                    return;
                }

                else{
                    if (userInput == 1){
                        System.out.println("You've selected option 1");
                        getAllStudents();
                    }

                    if (userInput == 2) {
                        System.out.println("You've selected option 2");

                        // ask for user entered values and add to indices from 1 to 4 (?) of the query
                        System.out.println("\nPlease enter the student's first name: ");
                        String firstName = scanner.next();

                        System.out.println("\nPlease enter the student's last name: ");
                        String lastName = scanner.next();

                        System.out.println("\nPlease enter the student's email: ");
                        String studentEmail = scanner.next();

                        System.out.println("\nPlease enter the student's enrollment-date: ");
                        String date = scanner.next();
                        Date convertedDate = Date.valueOf(date); // convert to date format (YYYY-MM-DD)
                        
                        addStudent(firstName, lastName, studentEmail, convertedDate);

                    }

                    if (userInput == 3){
                        System.out.println("You've selected option 3");
                        System.out.println("\nEnter the id of the student whose email you would like to update:");
                        int studentId = scanner.nextInt();

                        System.out.println("\nEnter the new email of this student:");
                        String newStudentEmail = scanner.next();

                        updateStudentEmail(studentId, newStudentEmail);
                    }

                    if (userInput == 4){
                        System.out.println("You've selected option 4");
                        System.out.println("Enter a student id to delete: ");
                        int studentId = scanner.nextInt();
                        deleteStudent(studentId);
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    //display all students in the students table
    static void getAllStudents(){
        try{
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM students ORDER BY student_id ASC"; //select all records in the students table
            ResultSet result = statement.executeQuery(query); //store results after executing query

            //display the results
            System.out.println("\n---RESULTS---");
            while (result.next()){ //loop through all records until afterthe last record is reached
                System.out.println("\nStudent Name: " + result.getString("first_name") + " " + result.getString("last_name") +
                "\nID: " + result.getInt("student_id") + "\nEmail: " + result.getString("email") + 
                "\nEnrollment Date: " + result.getString("enrollment_date"));
            }
        }

        catch (SQLException e){
            System.out.println("Connection failed inside displayStudents: SQLException");
            e.printStackTrace();
        }
    }

    //add student to the students table
    static void addStudent(String first_name, String last_name, String email, Date enrollment_date){
        try{
            //insert user entered column values for each ? in query
            //student_id is not asked as it's auto-incremented
            String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?) ";
            PreparedStatement statement = connection.prepareStatement(query); // prepare query to be edited

            //insert entered values to indices from 1 to 4 (?) of the query
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, email);
            statement.setDate(4, enrollment_date);

            //insert into the database
            int inserted = statement.executeUpdate();

            //check if inserted successfully
            if (inserted > 0){
                System.out.println("The new student has been inserted successfully.");
            }
            else{
                System.out.println("Failed to insert new student.");
            }
        }

        catch (SQLException e){
            System.out.println("Connection failed inside addStudent: SQLException");
            e.printStackTrace();
        }
    }

    //delete student from the students table given student_id
    static void deleteStudent(int student_id){
        try{
            //insert user entered column values for each ? in query
            ////delete the record from the students table whose student_id 
            //matches the one given by the user
            String query = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(query); // prepare statement to edit the query

            statement.setInt(1, student_id); //set the id to be stored in the only index (?)
            int deleted = statement.executeUpdate();

            //check if inserted succcessfully
            if (deleted > 0){
                System.out.println("Student has been successfully deleted.");
            }
            else{
                System.out.println("No student with id: " + student_id + " has been found to delete.");
            }
        }
        catch (SQLException e){
            System.out.println("Connection failed inside deleteStudent: SQLException");
            e.printStackTrace();
        }

        
    }

    //update the student_email column in students table given student_id
    static void updateStudentEmail(int student_id, String new_email){
        try{
            //update the students table, specifically the email column of the record whose student_id
            //matches the one given by the user
            String query = "UPDATE students SET email = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(query); //prepare statement to edit the query

            statement.setInt(2, student_id); //set the id to be stored in the second index (?)
            statement.setString(1, new_email); //set the email to be stored in the first indexc (?)

            int update = statement.executeUpdate();
            //check if inserted successfully
            if (update > 0){
                System.out.println("Email has successfully been updated");
            }
            else{
                System.out.println("No student with the id " + student_id + " given found.");
            }
        }
        catch (SQLException e){
            System.out.println("Connection failed inside updateStudentEmail: SQLException");
            e.printStackTrace();
        }
    }
}