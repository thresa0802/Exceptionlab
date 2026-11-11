import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Custom exception for duplicate student IDs (Unchecked runtime exception)
class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}

public class MarksImporter {
    private static final Logger LOGGER = Logger.getLogger(MarksImporter.class.getName());

    public static void main(String[] args) {
        importMarks("marks.txt");
    }

    public static void importMarks(String filePath) {
        Set<String> seenStudentIds = new HashSet<>();

        // Try-with-resources: automatically closes the BufferedReader when done
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Line must contain exactly two comma-separated values.");
                    }

                    String studentId = parts[0].trim();
                    int mark = Integer.parseInt(parts[1].trim());

                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException("Mark must be between 0 and 100.");
                    }

                    if (seenStudentIds.contains(studentId)) {
                        throw new DuplicateStudentException("Duplicate student ID detected: " + studentId);
                    }

                    seenStudentIds.add(studentId);
                    System.out.println("Accepted Record -> ID: " + studentId + ", Mark: " + mark);

                } catch (IllegalArgumentException | DuplicateStudentException ex) {
                    LOGGER.log(Level.WARNING, "Warning on line " + lineNumber + ": " + ex.getMessage());
                    System.out.println("Warning: Could not process line " + lineNumber + " -> " + ex.getMessage());
                }
            }

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "File not found: " + ex.getMessage());
            System.out.println("Error: The specified marks file could not be found.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "I/O error occurred: " + ex.getMessage());
            System.out.println("Error: An input/output error occurred while reading the file.");
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}