import java.io.PrintStream;
import java.lang.System;
import java.util.ArrayList;
import java.util.Scanner;

public class recovery {
    /* For testing
    public static void main(String args[]) {
        Scanner scn = new Scanner(System.in);
        ArrayList<Integer> result = decodeInput(scn.nextLine(),10);
        System.out.println(result);
        scn.close();
    } */

    // --------------------------------------------------------------------------------------------------------
    //  COMP : uses COMP recovery algorithm to recover Probable Defectives from positive pools
    //      Returns : ArrayList<Integer> - list containing each Probable Defective
    //      Args    : int[] rowTests - array containing each row test result, length n
    //              : int[] colTests - array containing each col test result, length n
    //              : int[] diagTests - array containing each diag test result, lenth n
    //              : int n - integer length of sides of testing matrix (square) i.e. 100 individuals -> n=10
    // --------------------------------------------------------------------------------------------------------
    public static ArrayList<Integer> COMP(int[] rowTests, int[] colTests, int[] diagTests, int n) {
        // Use COMP algorithm to determine defectives
        int I = n*n;
        // The probable defectives
        ArrayList<Integer> PD = new ArrayList<Integer>();
        // The definite non-defectives
        ArrayList<Integer> DND = new ArrayList<Integer>();
        // The definite defectives
        ArrayList<Integer> DD = new ArrayList<Integer>();

        // First determine the DNDs for the rows
        for (int i=0; i<rowTests.length; i++) {
            if (rowTests[i] == 0) {
                // Add entire row to DND list
                for (int x=0; x<rowTests.length; x++) {
                    DND.add(i*n+x);
                }
            }
        }

        // For the columns
        for (int j=0; j<colTests.length; j++) {
            if (colTests[j] == 0) {
                // Add entire col to DND list
                for (int y=0; y<colTests.length; y++) {
                    DND.add(y*n+j);
                }
            }
        }

        // For the diagonals
        // Generate indexing array
        int[][] n_array = new int[n][n];
        int counter = 0;
        for (int a=0; a<n; a++) {
            for (int b=0; b<n; b++) {
                n_array[a][b] = counter;
                counter++;
            }
        }
        for (int k=0; k<diagTests.length; k++) {
            if (diagTests[k] == 0) {
                // Add entire diag to DND list
                for (int c=k; c<I; c+=(n+1)) {
                    // Handle when diagonal wraps
                    if ((c+1) % n == 0) {
                        DND.add(c);
                        // Add one to c to wrap the diagonal by subtracting n for when the loop iterates
                        c -= n;
                    } else {
                        DND.add(c);
                    }
                }
            }
        }
        // Determine definite defectives
        for (int m=0; m<I; m++) {
            if (!DND.contains(m)) {
                // Add one in order to get individual with numbers starting at 1
                PD.add(m+1);
            }
        }
        return PD;
    }
    // ----------------------------------------------------------------------------------------------------------
    // decodeInput : Decodes testing input and returns the result of recovery
    //      Returns : ArrayList<Integer> of Probable Defectives
    //      Args    : String positives - string of positive tests in format (letter)(number) separated by spaces
    //                                   (no leading space, no trailing space)
    //              : int n - integer length of sides of testing matrix (square) i.e. 100 individuals -> n=10
    // ----------------------------------------------------------------------------------------------------------
    public static ArrayList<Integer> decodeInput(String positives, int n) {
        // Split positive tests into items
        String[] spltPositives = positives.split(" ");
        int[] rowTests = new int[n];
        int[] colTests = new int[n];
        int[] diagTests = new int[n];

        // Fill with zeros
        for (int a=0; a<n; a++) {
            rowTests[a] = 0;
            colTests[a] = 0;
            diagTests[a] = 0;
        }
        
        for (int i=0; i<spltPositives.length; i++) {
            if (spltPositives[i].substring(0,1).equals("C")) { // Column test
                colTests[(Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length())))-1] = 1;
            } else if (spltPositives[i].substring(0,1).equals("D")) { // Diag test
                diagTests[(Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length())))-1] = 1;
            } else if (spltPositives[i].substring(0,1).equals("R")) { // Row test
                rowTests[(Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length())))-1] = 1;
            } else {
                // Invalid input
                System.out.println("Invalid input: \""+spltPositives[i]+"\"");
                throw new java.lang.RuntimeException("Invalid test input: first letter should only be C,D,R");
            }
        }
        // Call decode function
        return COMP(rowTests,colTests,diagTests,n);
    }
}