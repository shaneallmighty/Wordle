/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package main.java.com.shane;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Scanner;
import java.util.Random;
import static java.awt.Color.*;

/**
 *
 * @author Shane O'Callaghan
 */
public class Lingo {
   static String filePath = "textFiles/WordList.txt";
   static String randomWord = RandomWordReader.getRandomWord(filePath);
   static String testWoord = randomWord; //testWoord is de string die geraden moet worden

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                    // Load custom font
                    Font customTitleFont = loadCustomFont("fonts/burbank-big-black.ttf", 75f);
                    Font customAnswerFont = loadCustomFont("fonts/bright-sunday-sans-serif.ttf", 25f);
//                    Font customLabelFont = loadCustomFont("", 20f);

                    //Variables
                    String invoer;
                    ArrayList<String> invoerLijst = new ArrayList<String>();
                    char[] correctLetters = new char[25];
                    int guess = 0;
                    int numberOfLabels = 5;
                    int maxGuesses = 5;

                    //colors for printing to console
                    final String BG_GREEN = "\u001B[42m";
                    final String BG_YELLOW = "\u001B[43m";
                    final String BG_RED = "\u001B[31m";
                    final String RESET = "\u001B[0m";

                    //Gui objecten
                    JFrame frame = new JFrame();
                    JPanel title_panel = new JPanel();
                    JLabel textfieldTitel = new JLabel();

                    JPanel game_panel = new JPanel();
                    JPanel[] guessRows = new JPanel[maxGuesses]; // Array to store each row of guesses
                    JLabel[][] labels = new JLabel[maxGuesses][numberOfLabels]; // 2D array to store labels

                    JPanel answer_panel = new JPanel();
                    JLabel textfieldAnswer = new JLabel();

                    //Gui instellingen
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(600,600);
                    frame.getContentPane().setBackground(new Color(50,50,50));
                    frame.setLayout(new BorderLayout());
                    frame.setVisible(true);
                    frame.setLocationRelativeTo(null);

                    textfieldTitel.setBackground(new Color(25,25,25));
                    textfieldTitel.setForeground(GREEN);
                    textfieldTitel.setFont(customTitleFont);
                    textfieldTitel.setHorizontalAlignment(JLabel.CENTER);
                    textfieldTitel.setText("WORDLE!");
                    textfieldTitel.setOpaque(true);

                    textfieldAnswer.setBackground(new Color(25,25,25));
                    textfieldAnswer.setForeground(GREEN);
                    textfieldAnswer.setFont(customAnswerFont);
                    textfieldAnswer.setHorizontalAlignment(JLabel.CENTER);
                    textfieldAnswer.setText("............");

                    title_panel.setLayout(new BorderLayout());
                    title_panel.setBounds(0,0,600,100);

                    game_panel.setLayout(new GridLayout(maxGuesses, numberOfLabels));

                    answer_panel.setLayout(new BorderLayout());
                    answer_panel.setBackground(new Color(0,0,0));
                    answer_panel.setBounds(0,0,600,100);

                    title_panel.add(textfieldTitel);
                    answer_panel.add(textfieldAnswer);
                    frame.add(title_panel,BorderLayout.NORTH);
                    frame.add(answer_panel,BorderLayout.SOUTH);
                    frame.add(game_panel,BorderLayout.CENTER);

                    // Create labels and add them to the game panel
                    Border border = BorderFactory.createLineBorder(Color.BLACK, 3); // Black border with thickness of 3
                    for (int i = 0; i < maxGuesses; i++) {
                        guessRows[i] = new JPanel();
                        guessRows[i].setLayout(new GridLayout(1, numberOfLabels)); // Single row of labels
                        for (int j = 0; j < numberOfLabels; j++) {
                            labels[i][j] = new JLabel(""); // Initialize with empty text
                            labels[i][j].setPreferredSize(new Dimension(60, 60)); // Set preferred size
                            labels[i][j].setOpaque(true);
                            labels[i][j].setBackground(Color.LIGHT_GRAY); // Set a background color to see the size
                            labels[i][j].setHorizontalAlignment(JLabel.CENTER); // Center align text
                            labels[i][j].setBorder(border); // Set border for each label
                            labels[i][j].setFont(new Font("Monospaced",Font.BOLD,20));
                            guessRows[i].add(labels[i][j]); // Add label to the current row
                        }
                        game_panel.add(guessRows[i]); // Add the row to the game panel
                    }


                    while (true) {
                        //Invoer opvragen en opslaan
                        invoer = showCustomInputDialog(frame, "Enter word:", guess);

                        //Als er niets wordt gedaan
                        if (invoer == null) {
                            System.out.println("Input was cancelled.");
                            break;
                        }

                        //Controleer of het woord 5 letters heeft
                        if (anLetters(invoer) != 5 && invoer.length() != 5) {
                            System.out.println(BG_RED + "  The word must contain 5 letters! " + RESET);
                            textfieldAnswer.setText(" The word must contain 5 letters! ");
                            continue; // skip the rest of the loop if the input is not 5
                        }else {
                            //Set text to show how many guesses remain
                            textfieldAnswer.setText(4 - guess + " guesses left");
                            if (guess == 3 ){
                                textfieldAnswer.setText(4 - guess + " guess left");
                            }
                        }

                        // Validate input characters as letters
                        boolean validInput = true;
                        for (int i = 0; i < invoer.length(); i++) {
                            if (!Character.isLetter(invoer.charAt(i))) {
                                validInput = false;
                                break;
                            }
                        }
                        if (!validInput) {
                            textfieldAnswer.setText("Please only use letters!");
                            continue;
                        }

                        // Check if word is in dictionary
                        if (!isInDictionary(invoer)) {
                                System.out.println(BG_RED + invoer + " is NOT in the dictionary" + RESET);
                                textfieldAnswer.setText(invoer + " is NOT in the dictionary");
                                continue;
                        }


                        //List to keep track of matched indices
                        List<Integer> matchedIndices = new ArrayList<>();
                        char[] usedLetters = new char[25];
                        boolean[] yellowFlags = new boolean[5];

                        // Count occurrences of each character in the target word
                        int[] testWoordCharCounts = new int[26];
                        for (char c : testWoord.toCharArray()) {
                            testWoordCharCounts[c - 'a']++;
                        }

                        // Loop for the letter checks
                        for (int i = 0; i < 5; i++) {

                            char inputChar = invoer.charAt(i);

                            if (invoer.charAt(i) == testWoord.charAt(i)) {
                                // Correct letter in the correct position (GREEN)
                                labels[guess][i].setText(String.valueOf(invoer.charAt(i))); // Set the text
                                labels[guess][i].setBackground(Color.GREEN);
                                matchedIndices.add(i);
                                testWoordCharCounts[inputChar - 'a']--;
                                System.out.print(BG_GREEN + invoer.substring(i, i + 1) + RESET);
                            } else if (testWoord.indexOf(inputChar) != -1 && testWoordCharCounts[inputChar - 'a'] > 0) {
                                // Letter is in the word but not in the correct position (YELLOW)
                                boolean alreadyGreen = false;
                                // Check if this letter is already used as green
                                for (int j = 0; j < 5; j++) {
                                    if (invoer.charAt(j) == testWoord.charAt(j) && invoer.charAt(j) == inputChar) {
                                        alreadyGreen = true;
                                        break;
                                    }
                                }
                                if (!alreadyGreen) {
                                    labels[guess][i].setText(String.valueOf(invoer.charAt(i))); // Set the text
                                    labels[guess][i].setBackground(Color.YELLOW);
                                    testWoordCharCounts[inputChar - 'a']--;
                                    System.out.print(BG_YELLOW + invoer.substring(i, i + 1) + RESET);
                                } else {
                                    labels[guess][i].setText(String.valueOf(invoer.charAt(i))); // Set the text
                                    labels[guess][i].setBackground(Color.LIGHT_GRAY); // Mark as incorrect If it was marked as green somewhere else
                                }
                            } else {
                                // Letter is not in the word at all
                                labels[guess][i].setText(String.valueOf(invoer.charAt(i))); // Set the text
                                labels[guess][i].setBackground(Color.LIGHT_GRAY); // Mark as incorrect
                                System.out.print(invoer.substring(i, i + 1));
                            }
                        }

                        System.out.println();
                        guess++; // Tel een guess
                        invoerLijst.add(invoer);  //voeg de invoer toe aan de arraylist

                        if (guess > 4 && !invoer.equals(testWoord)) {
                            System.out.println("GAME OVER" + "\n" + "The correct word was: " + BG_RED + testWoord + RESET);
                            textfieldAnswer.setText("GAME OVER  The correct word was: " + testWoord);
                            textfieldAnswer.setForeground(RED);
                        }

                        //Invoer komt overeen met testWoord
                        if (invoer.equals(testWoord)) {
                            System.out.println("\n" + "Correct congratulations!");
                            textfieldAnswer.setText("Correct congratulations!");
                            break;
                        }
                    }
                });
    }
   public static boolean isLetter ( char letter){
            return (letter >= 'a' && letter <= 'z' || letter >= 'A' && letter <= 'Z');
        }

    public static int anLetters (String invoer){
            char[] letters = invoer.toCharArray();
            int anLetters = 0;

            for (char letter : letters) {
                if (isLetter(letter)) {
                    anLetters++;
                }
            }
            return anLetters;
        }

    public class RandomWordReader {
        public static String getRandomWord(String resourcePath) {
            // Use the class loader to load the resource
            InputStream inputStream = RandomWordReader.class.getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                System.out.println("Resource file not found: " + resourcePath);
                return "Resource file not found.";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                List<String> words = new ArrayList<>();
                String line;

                // Read each line and add words to the list
                while ((line = reader.readLine()) != null) {
                    String[] lineWords = line.split("\\s+"); // Split by any whitespace character
                    for (String word : lineWords) {
                        if (!word.isEmpty()) { // Exclude empty strings
                            words.add(word);
                        }
                    }
                }
                // Check if the file contains any words
                if (words.isEmpty()) {
                    return "File is empty or does not contain any words.";
                }

                // Generate a random index
                Random random = new Random();
                int randomIndex = random.nextInt(words.size());
                // Retrieve the random word
                return words.get(randomIndex);

            } catch (IOException e) {
                e.printStackTrace();
                return "An error occurred while reading the file.";
            }
        }
    }

    public static String showCustomInputDialog(JFrame parent, String message, int guesses) {

        // Check if the number of guesses has reached or exceeded the maximum limit
        int maxGuesses = 5; // Define the maximum number of guesses allowed
        if (guesses >= maxGuesses) {
            return null; // Return null to indicate no input was taken and frame will be disposed
        }

        final String[] userInput = new String[1]; // Array to hold user input

        JDialog dialog = new JDialog(parent, "Input", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parent);

        // Calculate position to the left of the parent
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        Dimension dialogSize = dialog.getSize();
        int x = parentLocation.x - dialogSize.width; // Position to the left
        int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2; // Center vertically

        // Adjust the dialog location
        dialog.setLocation(x, y);

        // Create a panel for the input components with SpringLayout
        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        // Create a label to display the message
        JLabel label = new JLabel(message, JLabel.CENTER);
        panel.add(label);

        // Create a text field for user input with a custom size
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 30)); // Set preferred size for the input field
        panel.add(textField);

        // Create buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Action listener for OK button
        okButton.addActionListener(e -> {
            userInput[0] = textField.getText(); // Set the user input
            dialog.dispose(); // Close the dialog
        });

        // Action listener for Cancel button
        cancelButton.addActionListener(e -> {
            userInput[0] = null; // Indicate cancellation
            dialog.dispose(); // Close the dialog
            parent.dispose();
        });

        // Add buttons to the panel
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Bind Enter key to OK button action
        InputMap inputMap = textField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = textField.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "okButtonAction");
        actionMap.put("okButtonAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButton.doClick(); // Simulate a button click
            }
        });

        // Position components using SpringLayout
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, label, -10, SpringLayout.EAST, panel);

        layout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, textField, 10, SpringLayout.SOUTH, label);
        layout.putConstraint(SpringLayout.EAST, textField, -10, SpringLayout.EAST, panel);

        layout.putConstraint(SpringLayout.SOUTH, buttonPanel, -10, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, panel);

         // Show dialog and wait for user input
        dialog.setVisible(true);

        return userInput[0]; // Return the user input
    }

    public static boolean isInDictionary(String invoer) {
        // Load the dictionary from the resources folder
        InputStream dictionaryStream = Lingo.class.getClassLoader().getResourceAsStream("textFiles/DictionaryEN.txt");

        if (dictionaryStream == null) {
            System.out.println("Dictionary file not found.");
            return false; // Or handle the error as needed
        }

        try (Scanner dictionary = new Scanner(dictionaryStream)) {
            // Check each line in the dictionary
            while (dictionary.hasNextLine()) {
                String line = dictionary.nextLine().trim();
                if (line.equalsIgnoreCase(invoer)) { // Use equalsIgnoreCase for case-insensitive comparison
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Font loadCustomFont(String fontResourcePath, float size) {
        try {
            // Use the class loader to load the font resource as an InputStream
            InputStream fontStream = Lingo.class.getClassLoader().getResourceAsStream(fontResourcePath);
            if (fontStream == null) {
                throw new IOException("Font resource not found: " + fontResourcePath);
            }

            // Create the font from the InputStream
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);

            // Return the font with the specified size
            return customFont.deriveFont(size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();

            // Fallback font in case of failure
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

}


