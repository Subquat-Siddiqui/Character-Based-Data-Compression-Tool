package enc;

import dataStructure.ListNode;
import dataStructure.TreeNode;

import java.io.*;
import java.util.Scanner;

/**
 * @author Subquat Siddiqui
 * @since July 1st, 2024
 * this class is responsible for reading input text, creating a frequency table
 * based off of that text, then constructing a tree (also the codex)
 * to map characters to binary patterns based on their frequencies and the path you must take in tree
 * to get to that character. It then creates an output file containing the codebook
 * for the characters used in the input file, followed by message written in binary pattern.
 */

public class Encrypter {
    ListNode head;//head of the linked list that will store the roots of the tree's used for creating the codex
    TreeNode root;//root of the codex
    String[] characterTable;//storing characters used in the input file
    int[] freqTable;//recording the frequency of characters used in the input file
    int size = 0;//number of different characters in the input file


    public Encrypter(String inputFile, String outputFile){
        //This row is for all the characters that appear in the file
        characterTable = new String[256];
        //This row is for the frequency of the characters in characterTable, corresponded by the same index
        freqTable = new int[256];

        Scanner input = null;
        try {
            input = new Scanner(new File(inputFile));
        }
        catch (IOException ioe) {
            System.out.println("Unsuccessful");
            return;
        }

        /*format of input file is only one line,
        so everything will be read from 'line'*/
        String line = input.nextLine();

        //creating frequency table for all characters found within the file
        for (int i = 0; i<line.length(); i++){
            addToTable(line.charAt(i));
        }

        /*generating the forest of single node trees and adding
        all of them to a priority list. Each root of the forest contains their own
        character and the frequency of that character*/
        genForest();


        /*creating the final tree (the codex) from
        all the root nodes in the priority list*/
        createTree();


        PrintWriter writer = openFileForSave(outputFile);
        if(writer == null) return;


        /*creating the codebook, and writing the encrypted
        message to the output file.*/
        writeToFile(writer, line);

        writer.close();


    }

    private PrintWriter openFileForSave(String filename) {
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        }
        catch (IOException ioe) {System.out.println("Unsuccessful");}
        return null;
    }

    /**
     * This method writes the codebook, where each line contains
     * a character used in the input file, and it's corresponding
     * binary path/code obtained from the codex on the same line.
     * After the codebook, there is a break of '---', then on a separate line,
     * the encrypted message using the binary paths/code is written.
     * @param writer the PrintWriter being used to write to the output file
     * @param line the message being encrypted
     */
    private void writeToFile(PrintWriter writer, String line){
        /*writing each line of the codebook, by looking for each character
        written in the root of the codex (tree), and writing the path
        it took to get to that character.*/
        for (int i = 0; i < root.text.length() ; i++) {
            writer.println(root.text.charAt(i) + "\t" + getBinaryPath(root.text.charAt(i)));
        }

        //space between codebook and encrypted message
        writer.println("---");

        /*writing the encrypted message by obtaining each character's
        binary path/code from the codex.*/
        for (int i = 0; i < line.length(); i++) {
            writer.print(getBinaryPath(line.charAt(i)) + "\t");
        }

    }

    /**
     * this method updates the frequency table for the message obtained from the input
     * file, which is then later used for creating the codex (stored as a tree).
     * It updates the values of both characterTable and freqTable arrays,
     * as they are both being used as separate rows for the frequency table.
     * The first row is for what characters appear in the message, and the
     * second row is for the frequency of that character in the same message.
     * @param c the character being added/updated in the table
     */
    private void addToTable(char c){

        String s = "" + c;

        //checking if character already exists or new character needs to be added to table.
        for (int i = 0; i<characterTable.length; i++) {
            //if character already exists, then frequency of that character needs to be increased.
            if (s.equals(characterTable[i])) {
                freqTable[i]++;
                break;
            }
            //else if space for a new character has been found, add it to the table.
            else if (characterTable[i] == null) {
                characterTable[i] = s;
                freqTable[i]++;
                size++;
                break;
            }
        }
    }

    /**
     * This method takes all the characters and corresponding frequencies from
     * the frequency table, and creates an array of single Node trees: each
     * containing its own character and frequency.
     * Each TreeNode from that array is then added to a priority Linked List, where each
     * ListNode contains the root of a TreeNode.
     * The priority Linked List is sorted by frequency, with smaller frequency at the front,
     * and larger frequency at the back.
     */
    private void genForest(){
        TreeNode[] forest = new TreeNode[size];

        for (int i = 0; i < size; i++) {
            forest[i] = new TreeNode(null, characterTable[i], freqTable[i], null);
            //adding to priority list
            addToList(forest[i]);
        }

    }


    /**
     * This method creates the final codex (tree) by using
     * the TreeNodes inside the priority Linked List. TreeNodes with
     * the lowest frequencies will be situated near the bottom of the tree,
     * while TreeNodes with the highest frequencies will be located near the top of the tree,
     * making them easier to access.
     */
    private void createTree() {
        //loop will stop when head.next == null, meaning only the final tree is left in the list
        while(head != null && head.next != null){
            //extracting the trees with the 2 lowest frequencies in the priority list
            ListNode firstNode = removeFront();
            ListNode secondNode = removeFront();

            //gathering texts from both first and second Nodes to use as label for new root node being created
            String firstText = firstNode.tree.text;
            String secondText = "";
            if(secondNode != null) secondText = secondNode.tree.text;
            String totalText = firstText + secondText;

            //gathering the frequency from the first and second Nodes to add together for the new root node
            int firstFrequency = firstNode.tree.frequency;
            int secondFrequency = 0;
            if(secondNode != null) secondFrequency = secondNode.tree.frequency;
            int totalFrequency = firstFrequency + secondFrequency;

            /*creating the new root node, with it pointing to the first and second nodes that were
            extracted from the list, then inserting it into the priority list*/
            TreeNode combinedTree = new TreeNode(firstNode.tree, totalText, totalFrequency, secondNode.tree);
            addToList(combinedTree);
        }

        //the root will now point to the final tree remaining
        root = head.tree;

    }

    /**
     * This method takes the root of a tree and adds it to a priority Linked List,
     * where each ListNode contains the root of a tree. The Linked List
     * is sorted by frequency, and prioritizes roots with smaller
     * frequencies and places them at the front of the list to be accessed
     * later when creating the codex.
     * @param t the TreeNode being added to the priority Linked List
     */
    private void addToList(TreeNode t){
        if(head == null){//if starting a new list
            head = new ListNode(t, null);
            return;
        }

        ListNode p = head;//firstPointer
        ListNode q = null;//secondPointer

        //going through the list, checking where TreeNode t should be added
        while(p != null){

            /*if new tree being added has smaller or equal frequency than the tree of ListNode
            in front of it.*/
            if(t.compareTo(p.tree) >= 0 && p.equals(head)){//if the new node being added is also becoming the head
                q = new ListNode(t, p);
                head = q;
                return;
            }
            else if(t.compareTo(p.tree) >= 0){//new node being added isn't becoming the head
                q.next = new ListNode(t, p);
                return;
            }

            //if new tree being added comes last in the list by frequency
            if(p.next == null){
                p.next = new ListNode(t, null);
                return;
            }
            q = p;
            p = p.next;

        }
    }


    /**
     * This method removes the ListNode at the front of the list
     * @return the ListNode being removed
     */
    private ListNode removeFront() {
        ListNode p = head;
        if(head == null) return null;
        head = head.next;
        return p;
    }

    /**
     * Method traverses through the codex (tree), and
     * creates a String of the path it traversed through the tree.
     * '0' meant it went left and '1' meant it went right.
     * @param c the character of which the binary path is being returned for
     * @return the binary path of parameter 'c', as a String
     */
    private String getBinaryPath(char c){
        String s = c + "";
        String path = "";

        TreeNode p = root;

        //loop will stop once p lands at the TreeNode designated specifically for that character
        while(!p.text.equals(s)){
            if(p.left.text.contains(s)){
                p = p.left;
                path = path + "0";
            }
            else if(p.right.text.contains(s)){
                p = p.right;
                path = path + "1";
            }

        }

        return path;
    }



    public static void main(String[] args) {
        String inputFile;
        String outputFile;

        if(args.length == 1){//only one argument provided, the name of the input file
            inputFile = args[0];
            outputFile = "encrypted.txt";
        }
        else if(args.length == 2){//two arguments provided, names of the input file and output file
            inputFile = args[0];
            outputFile = args[1];
        }
        else{//no arguments provided/too many arguments provided, using default names for input and output files
            inputFile = "testing.txt";
            outputFile = "encrypted.txt";
        }

        new Encrypter(inputFile, outputFile);
    }
}
