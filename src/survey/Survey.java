package survey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 *
 * @author benjaminfajic
 */
public class Survey {
    static int[] grades = new int[34];
    static int[] coursesArray = new int [200];
    static int courseCounter = 0;
    static String[] averages = new String[34];
    static int studentCounter = 0;
    static int studentCounterForAverage = 0;
    String gradesLine;
    static StringBuilder sb = new StringBuilder();
    
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    
    public void readFile(String fileName) {
        try {
            String outputName = "../survey_calc/" + fileName.substring(0,fileName.length()-3) + "csv";
            FileReader file = new FileReader(fileName);
            FileWriter fileWriter = new FileWriter(outputName);
 
            try(BufferedReader br = new BufferedReader(file)) {
                for(String line; (line = br.readLine()) != null; ) {
                    // process the line.
                    gradesLine = line.substring(10, line.length());
                    for(int i = 0; i < gradesLine.length(); i++) {
                        char character = gradesLine.charAt(i);
                        switch (character) {
                            case 'A':
                                grades[i] = 4;
                                break;
                            case 'B':
                                grades[i] = 3;
                                break;
                            case 'C':
                                grades[i] = 2;
                                break;
                            case 'D':
                                grades[i] = 1;
                                break;
                            case 'E':
                                grades[i] = 0;
                                break;
                            default:
                                grades[i] = -1;
                                break;
                        }
                    }                  
                    writeCsvFile(fileWriter);                  
                }
            }
            studentCounter = 0;
            file.close();
 
        } catch (IOException e) {
            // handle exception
        }
    }
    
    public static void writeCsvFile(FileWriter fileWriter) throws IOException {

        PrintWriter out = new PrintWriter(fileWriter);
        out.print("Student" + Integer.toString(++studentCounter) + ",");
        //Write a new student object list to the CSV file
        for(int i = 0; i < 34; i++){
            if (grades[i] == -1) {
                out.print(" ");
            } else {
                out.print(grades[i]);
            }   
            out.print(COMMA_DELIMITER);
        }
        out.print(NEW_LINE_SEPARATOR);

        out.flush();
    }
    
    public void readCsv(String fileName) throws FileNotFoundException, IOException {
        FileReader file = new FileReader(fileName);
        FileWriter fw = new FileWriter("../survey_calc/Survey average.csv");
        
        try(BufferedReader br = new BufferedReader(file)) {
            for(String line; (line = br.readLine()) != null; ) {
                studentCounterForAverage++;
            }
        }
        
        try {
            double sum;
            double numberOfStudents;
            for(int i = 0; i < 34; i++){
                sum = 0;
                numberOfStudents = 0;
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] cols = line.split(COMMA_DELIMITER);
                    if(!" ".equals(cols[i+1])) {
                        double grade = Double.parseDouble(cols[i+1]);
                        sum += grade;
                        numberOfStudents++;
                    }
                }
                NumberFormat formatter = new DecimalFormat("#0.000");
                double temp = sum/numberOfStudents;
                String average = " ";
                if (!Double.isNaN(temp)) {
                    average = formatter.format(temp);
                }  
                averages[i] = average;
            }
            writeCsvAverage(fw, fileName);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeCsvAverage(FileWriter fileWriter, String fileName) {
        
        PrintWriter out = new PrintWriter(fileWriter);
        String tempCounter = fileName.substring(21, fileName.length()-4);
        sb.append(("CourseNum" + tempCounter  + "," + Integer.toString(studentCounterForAverage) + ","));
        for(int i = 0; i < 34; i++) {
            sb.append(averages[i]);
            sb.append(COMMA_DELIMITER);
        }
        sb.append(NEW_LINE_SEPARATOR);
        out.write(sb.toString());

        out.flush();
        studentCounterForAverage = 0;
    }


    public static void main(String[] args) throws IOException {
        
        Survey survey = new Survey();

        
        // create folder
        File dir = new File("../survey_calc");
        boolean success = dir.mkdir();
        if(!success) {
            //System.out.println("Creating of folder failed.");
        }
        
        // process all .txt files from current folder
        final File folder = new File(System.getProperty("user.dir"));
        for (final File fileEntry : folder.listFiles()) {
            if(fileEntry.getName().endsWith(".txt")) {
                if(fileEntry.getName().startsWith("Course")) {
                    coursesArray[courseCounter++] = Integer.parseInt(fileEntry.getName().substring(6, fileEntry.getName().length()-4));
                    survey.readFile(fileEntry.getName());
                    
                }
            }   
        }
        
        int[] sortedCourses = new int[courseCounter];
        
        for(int i = 0; i < courseCounter; i++) {
            sortedCourses[i] = coursesArray[i];
        }
        
        Arrays.sort(sortedCourses);
        
        for (int i = 0; i < courseCounter; i++) {
            survey.readCsv("../survey_calc/Course" + sortedCourses[i] + ".csv");
        }
    }
}
