package ru.hse.kuzyaka.phonebook;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    private static int getCommand() {
        int res;
        System.out.println("ENTER COMMAND CODE:");
        try {
            res = SCANNER.nextInt();
        } catch (InputMismatchException e) {
            SCANNER.nextLine();
            res = -1;
        }
        return res;
    }


    private static String getString(String attribute) {
        System.out.println("ENTER " + attribute + ":");
        return SCANNER.next();
    }

    private static void showCommands() {
        System.out.println("0 - EXIT\n" +
                "1 - ADD NEW RECORD\n" +
                "2 - FIND PHONE NUMBERS BY NAME\n" +
                "3 - FIND PERSONS BY PHONE NUMBER\n" +
                "4 - DELETE RECORD\n" +
                "5 - CHANGE NAME IN RECORD\n" +
                "6 - CHANGE PHONE NUMBER IN RECORD\n" +
                "7 - PRINT ALL RECORDS");
    }


    public static void main(String[] args) {
        int commandId;
        PhoneBook phoneBook = new PhoneBook();
        showCommands();

        do {
            String name;
            String number;
            String oldName;
            String oldNumber;
            String newName;
            String newNumber;
            commandId = getCommand();
            switch (commandId) {
                case 1:
                    name = getString("NAME");
                    number = getString("NUMBER");
                    if (phoneBook.addRecord(name, number)) {
                        System.out.println("RECORD SUCCESSFULLY ADDED");
                    } else {
                        System.out.println("RECORD ALREADY EXISTED");
                    }
                    break;
                case 2:
                    name = getString("NAME");
                    var numbers = phoneBook.getNumbersByPerson(name);
                    if (numbers == null) {
                        System.out.println("PERSON DIDN'T EXIST");
                    } else {
                        numbers.stream().
                                map(PhoneNumber::getNumber).
                                forEach(System.out::println);
                    }
                    break;
                case 3:
                    number = getString("NUMBER");
                    var persons = phoneBook.getPersonsByNumber(number);
                    if (persons == null) {
                        System.out.println("NUMBER DIDN'T EXIST");
                    } else {
                        persons.stream().
                                map(Person::getName).
                                forEach(System.out::println);
                    }
                    break;
                case 4:
                    name = getString("NAME");
                    number = getString("NUMBER");
                    if (phoneBook.deleteRecord(name, number)) {
                        System.out.println("RECORD SUCCESSFULLY DELETED");
                    } else {
                        System.out.println("RECORD DIDN'T EXIST");
                    }
                    break;
                case 5:
                    oldName = getString("NAME");
                    number = getString("NUMBER");
                    newName = getString("NEW NAME");
                    if (phoneBook.changeName(number, oldName, newName)) {
                        System.out.println("NAME SUCCESSFULLY CHANGED");
                    } else {
                        System.out.println("RECORD DIDN'T EXIST");
                    }
                    break;
                case 6:
                    name = getString("NAME");
                    oldNumber = getString("NUMBER");
                    newNumber = getString("NEW NUMBER");
                    if (phoneBook.changeNumber(name, oldNumber, newNumber)) {
                        System.out.println("NUMBER SUCCESSFULLY CHANGED");
                    } else {
                        System.out.println("RECORD DIDN'T EXIST");
                    }
                    break;
                case 7:
                    phoneBook.getAllRecords().forEach(
                            record -> System.out.println(record.getName() + " " + record.getNumber()));
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid command, try again.");
                    break;
            }

        } while (commandId != 0);
    }
}
