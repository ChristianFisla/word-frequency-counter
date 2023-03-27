package com.fisla.textReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextReader extends JPanel implements ActionListener {

    public static JFrame frame;
    public static JButton addFile;
    public static JTextArea textArea;
    public static JTextArea output;

    public static JComboBox comboBox;
    public static String[] options = {"alice.txt", "moby.txt"};


    // Constructor of JPanel Object
    public TextReader() {

        // Add JButton and set the bounds of where it is located
        add(addFile = new JButton("Add File"));
        addFile.setBounds(new Rectangle(50, 100, 400, 50));

        // Add textarea and specify fond and bounds
        add(output = new JTextArea());
        output.setBounds(500, 75, 250, 350);
        output.setEditable(false);
        output.setFont(new Font("Courier New", Font.PLAIN, 12));

        // Add action command and a listener to the button
        addFile.setActionCommand("add_file");
        addFile.addActionListener(this);

        // Set the layout of the Jpanel
        setLayout(null);

        add(textArea = new JTextArea());

        // Read the chosen file and input the text into the area. start with alice.txt by default
        try {
            textArea.setText(new String(Files.readAllBytes(Paths.get("alice.txt"))));
        } catch (IOException e) {
            System.out.println("Error");
        }

        // Specify settings of text area
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));

        // Make the text area scrollable and specify bounds
        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(new Rectangle(50, 200, 400, 225));

        add(scroll);

        // Create combo box and set appropriate action commands and listeners
        comboBox = new JComboBox(options);
        comboBox.setBounds(new Rectangle(50, 170, 400, 25));

        comboBox.setActionCommand("combo_box_change_state");
        comboBox.addActionListener(this);

        add(comboBox);

    }

    // Desc: takes in a directory and adds all the unique words to a treemap, making sure to keep track of how many times every word occurs
    // Param: takes in the directory of the file to parse in string format
    // Return: N/A
    public static void findWords(String dir) throws IOException {

        // Start the timer and initialize StringBuilder
        long startTime = System.nanoTime();
        StringBuilder file = parseInput(dir);

        // Replace all the characters we don't need
        String tempFile = file.toString().toLowerCase().replaceAll("[-]", "");
        tempFile = file.toString().toLowerCase().replaceAll("[,]", " ");

        // Split the remaining text to get just the words
        StringTokenizer token = new StringTokenizer(tempFile," ,.\n?!:()\"*`;");
        TreeMap<String, Integer> map = new TreeMap<>();

        // Keep looping as long as there are more words to go through
        while (token.hasMoreTokens()) {

            String nextToken = token.nextToken();

            // Handle various cases
            if (nextToken.equals("'")) continue;

            // Handle case
            if (nextToken.endsWith("'s")) nextToken = nextToken.substring(0, nextToken.length() - 2);

            // If the key is not unique simply increment the value
            if (map.containsKey(nextToken)) {
                map.put(nextToken, map.get(nextToken) + 1);
            } else {
                // Add a new token if it is unique
                map.put(nextToken, 1);
            }
        }

        // Sort the TreeMap
        TreeMap<String, Integer> sortedMap = sortTreeMap(map);

        // End the time
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        // Get the output and set it to the text area to be displayed
        String outputText = printTop20(sortedMap) + "\n" + duration / Math.pow(10, 6) + " milliseconds";
        output.setText(outputText);
    }

    // Desc: takes in the directory of a file and combines it into one StringBuilder
    // Param: takes in the directory of the file to parse in string format
    // Return: a StringBuilder of the entire file
    public static StringBuilder parseInput(String dir) throws IOException {

        // Initialize BufferedReader and StringBuilder
        BufferedReader in = new BufferedReader(new FileReader(dir));
        String input = "";
        StringBuilder total = new StringBuilder();

        input = in.readLine();

        // Read line and loop until it is null
        while (input != null) {
            // Add each line to the StringBuilder
            total.append(" ");
            total.append(input);
            input = in.readLine();
        }

        return total;
    }

    // Desc: takes in a Treemap and sorts it by VALUE
    // Param: takes in a TreeMap
    // Return: a sorted TreeMap by VALUE
    public static TreeMap<String, Integer> sortTreeMap(TreeMap<String, Integer> map) {

        // Create a new comparator object that takes in both KEYS in the TreeMap, gets their respective values, and compares both of the values
        Comparator<String> sortByValue = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                Integer v1 = map.get(s1);
                Integer v2 = map.get(s2);

                // Handles the case if both values are equal
                if (v2.compareTo(v1) == 0) {
                    return s2.compareTo(s1);
                }

                return v2.compareTo(v1);
            }
        };

        // Create and transfer to new TreeMap with comparator in the constructor to specify sorting criteria
        TreeMap<String, Integer> sortedMap = new TreeMap<>(sortByValue);
        sortedMap.putAll(map);

        return sortedMap;
    }

    // Desc: takes the top 20 words and their frequencies and formats them into a neatly packed String
    // Param: takes in a sorted(by value) TreeMap
    // Return: a String that formats the top 20 words and their frequencies
    public static String printTop20(TreeMap<String, Integer> sortedMap) {

        StringBuilder output = new StringBuilder();

        int i = 0;

        // Print the sorted TreeMap
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            output.append(String.format("%-17s%16d%n", entry.getKey(), entry.getValue()));
            i++;

            // Stop the loop when 20 words have been reached
            if (i == 20) break;
        }

        return output.toString();
    }

    // Desc: appends a new String to a String array
    // Param: takes in a String to be added to the String array
    // Return: returns a String array with the new element added
    public String[] appendOptionToArray(String append) {

        // Create the new array that is one longer than the original
        String[] array = new String[options.length + 1];

        // Loop through all elemenents and convert them to the new array
        for (int i = 0; i < options.length; i++) {
            array[i] = options[i];
        }

        // Finally, add the last String to the new array
        array[array.length - 1] = append;

        return array;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the action command
        String event = e.getActionCommand();

        // Check which action command has been sent
        if (event.equals("add_file")) {

            // Open file explorer and allow the user to choose
            FileDialog openDialog = new FileDialog(frame, "Open a new file", FileDialog.LOAD);
            openDialog.setVisible(true);
            String fileName = openDialog.getFile();
            String dir = openDialog.getDirectory();

            // If the user has actually chosen a file then add it to the combo box
            if (dir != null && fileName != null) options = appendOptionToArray(dir + fileName);

            // Add the most recent element added to the ComboBox
            comboBox.addItem(options[options.length - 1]);

        } else if (event.equals("combo_box_change_state")) {

            // When the combo box has changed, get the desired file and paste it into the text area
            try {
                textArea.setText(new String(Files.readAllBytes(Paths.get(comboBox.getSelectedItem().toString()))));
            } catch (IOException ex) {
                System.out.println("Error");
            }

            // Find the frequency of the selected file with findWords()
            try {
                findWords(comboBox.getSelectedItem().toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }
    public static void main(String[] args) throws IOException {

        // Initialize JPanel
        JPanel TextReader = new TextReader();

        // Specify frame settings
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(800, 500));
        frame.setLocation(200, 100);

        // Add panel to frame
        frame.add(TextReader);
        frame.pack();
        frame.setVisible(true);

        // Start off by searching alice.txt as default
        findWords("alice.txt");
    }
}