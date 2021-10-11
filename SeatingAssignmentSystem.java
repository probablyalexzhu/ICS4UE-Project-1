/* SeatingAssignmentSystem.java  
 * @author Dhruv, Alex, Suyu
 * @version October 11, 2021
 * Assign students to seats.
 */

import java.util.Collections;
import java.util.ArrayList;

class SeatingAssignmentSystem {

    final String[] groupNames = { "intro", "contest", "web" };
    final int NUM_GRADES = 4;
    final int LOWEST_GRADE = 9;

    public void arrangeStudents(FloorPlanSystem floorPlanSystem, ArrayList<Student> students, int tableSize) {

        // arraylist of group-grade pairs (0-3 is intro, 4-7 is contest, 8-11 is web)
        ArrayList<ArrayList<Student>> studentGroups = new ArrayList<ArrayList<Student>>(NUM_GRADES * groupNames.length);
        for (int i = 0; i < NUM_GRADES * groupNames.length; i++) {
            studentGroups.add(new ArrayList<Student>());
        }

        // put students in
        for (Student student : students) {
            int index = indexOfStringArray(groupNames, student.getGroup()) * NUM_GRADES + student.getGrade()
                    - LOWEST_GRADE;
            studentGroups.get(index).add(student);
        }

        // making tables AND adding students
        // looping through each studentgroup
        for (ArrayList<Student> studentGroup : studentGroups) {

            if (studentGroup.size() > 0) {

                // find how many tables needed
                int numTablesInStudentGroup = studentGroup.size() / tableSize;
                if (studentGroup.size() % tableSize != 0) {
                    numTablesInStudentGroup++;
                }

                // create tables for each studentgroup
                Table[] tablesInStudentGroup = new Table[numTablesInStudentGroup];
                for (int i = 0; i < numTablesInStudentGroup; i++) {
                    String tableName = "";
                    if (!studentGroup.isEmpty()) {
                        tableName = studentGroup.get(0).getGroup() + ": " + studentGroup.get(0).getGrade();
                    }
                    tablesInStudentGroup[i] = new Table(tableSize, tableName);
                }

                // sort students from most to least friends
                Collections.sort(studentGroup, new StudentFriendReverseComparator());

                tablesInStudentGroup = assignTablesInStudentGroup(studentGroup, tablesInStudentGroup);

                for (int i = 0; i < tablesInStudentGroup.length; i++) {
                    floorPlanSystem.addTable(tablesInStudentGroup[i]);
                }
            }
        }

    }

    private Table[] assignTablesInStudentGroup(ArrayList<Student> studentGroup, Table[] tablesInStudentGroup) {

        for (Student currentStudent : studentGroup) {

            boolean foundTable = false;

            // look for friends
            for (Table table : tablesInStudentGroup) {
                if (table.getRemainingCapacity() != 0) {
                    for (int k = 0; k < table.getCapacity() - table.getRemainingCapacity(); k++) {
                        if (isFriends(currentStudent, table.getStudentList()[k]) && !foundTable) {
                            table.addStudent(currentStudent);
                            foundTable = true;
                        }
                    }
                }
            }

            // if no friends, add to table with least members already
            if (!foundTable) {
                int mostCapacity = 0;
                int tableIndex = 0;
                for (int j = 0; j < tablesInStudentGroup.length; j++) {
                    if (tablesInStudentGroup[j].getRemainingCapacity() > mostCapacity) {
                        tableIndex = j;
                    }
                }
                tablesInStudentGroup[tableIndex].addStudent(currentStudent);
            }

        }

        return tablesInStudentGroup;
    }

    /**
     * Finds the index of a string in a string array
     * 
     * @param arr    the string array
     * @param string the string
     * @return the index of the string in the string array, or -1 if the string is
     *         not in the string array
     */
    private int indexOfStringArray(String[] arr, String string) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(string)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines if two students are friends
     * 
     * @param student1 the first student
     * @param student2 the second student
     * @return if student1 is a friend of student2, or if student2 is a friend of
     *         student1
     */
    private boolean isFriends(Student student1, Student student2) {
        return isFriendOf(student1, student2) || isFriendOf(student2, student1);
    }

    /**
     * Determines if a student is a friend of another student
     * 
     * @param student the student whose friend list to check
     * @param friend  the student to check
     * @return whether or not 'friend' is in the friend list of 'student'
     */
    private boolean isFriendOf(Student student, Student friend) {
        for (int i = 0; i < student.getFriends().length; i++) {
            if (friend.getId() == student.getFriends()[i]) {
                return true;
            }
        }
        return false;
    }

}
