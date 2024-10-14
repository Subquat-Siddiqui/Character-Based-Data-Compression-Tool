package enc;

import java.io.*;
import java.util.Scanner;

/**
 * @author Subquat Siddiqui
 * @since July 1st, 2024
 * this class reads the encrypted text file created by the Encrypter class.
 * It reads the codebook, associating each character with its corresponding binary code.
 * Then, it reconstructs the original message based off of the codebook read,
 * by reading the encrypted message and finding the corresponding character for each
 * binary code. The original text is then outputed into a new file.
 */



public class Decrypter {
    String[] codeBookChars;//codebook for characters used
    String[] codeBookBinary;//codebook for binary patterns of charcaters used
    String line = null;//for reading the codebook
    Scanner input = null;//reading the input file


    public Decrypter(String inputFile, String outputFile){
        /* codeBookBinary will contain all the binary codes read from the codebook, and
        codeBookChars will contain all the corresponding characters of
        said binary codes from the codebook. Binary codes and corresponding
        characters will be associated by having the same index in both arrays */
        codeBookChars = new String[256];
        codeBookBinary = new String[256];

        try {
            input = new Scanner(new File(inputFile));
        }
        catch (IOException ioe) {
            System.out.println("Unsuccessful");
            return;
        }

        /*reading the codebook and updating the values in both
        codeBookChars and codeBookBinary arrays.*/
        readCodeBook();

        PrintWriter writer = openFileForSave(outputFile);
        if(writer == null) return;

        /*reading the encrypted message and associating each binary code
        with its corresponding character, then writing it to the output file.*/
        decodeMessage(writer);

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
     * This method reads the codebook of the input file, updating both
     * codeBookChars and codeBookBinary arrays, and having them corresponded by the same index.
     * After this method, 'line' will be updated to be ready to read and decrypt the encrypted message.
     */
    private void readCodeBook(){
        for (int i = 0; i < codeBookChars.length; i++) {
            line = input.nextLine();

            //reached the end of the codebook
            if(line.equals("---")){
                //updating line to read the encrypted message
                line = input.nextLine();
                break;
            }

            //updating both arrays with it's corresponding character/binary code
            String[] split = line.split("\t");
            codeBookChars[i] = split[0];
            codeBookBinary[i] = split[1];
        }
    }

    /**
     * This method scans each individual binary code of the encrypted message
     * and associates it with the corresponding character to be written to the
     * output file.
     * @param writer the PrintWriter being used to write the decrypted message
     *               to the new output file.
     */
    private void decodeMessage(PrintWriter writer){
        /*creating a scanner to scan each word
        of the final line/encrypted message.*/
        Scanner lineScanner = new Scanner(line);
        String word;

        while(lineScanner.hasNext()){
            word = lineScanner.next();

            /*searching the codeBookBinary array for the matching code,
            then writing the corresponding character for that code
            to the output file.*/
            for (int i = 0; i < codeBookBinary.length; i++) {
                if(codeBookBinary[i] == null) break;//not found

                if(word.equals(codeBookBinary[i])){//found, write to output file
                    writer.write(codeBookChars[i]);
                    break;
                }
            }

        }

    }


    public static void main(String[] args) {
        String inputFile;
        String outputFile;

        if(args.length == 1){//only one argument provided
            inputFile = args[0];
            outputFile = "recovered.txt";
        }
        else if(args.length == 2){//two arguments provided
            inputFile = args[0];
            outputFile = args[1];
        }
        else{//no arguments provided/too many arguments provided
            inputFile = "encrypted.txt";
            outputFile = "recovered.txt";
        }

        new Decrypter(inputFile, outputFile);
    }
}
