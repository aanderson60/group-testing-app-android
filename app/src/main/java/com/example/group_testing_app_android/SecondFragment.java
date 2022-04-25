package com.example.group_testing_app_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.group_testing_app_android.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText GetData = binding.textInputEditText;
                String Data = GetData.getText().toString();
                String toWrite = "";
                try {
                    ArrayList<Integer> Defectives = decodeInput(Data, 10);
                    toWrite = "Possible Defectives: \n";
                    for (int i = 0; i < Defectives.size(); i++) {
                        toWrite = toWrite + Integer.toString(Defectives.get(i)) + " ";
                    }
                    // Case for no defectives
                    if (toWrite.equals("Possible Defectives: \n")) {
                        toWrite += "None";
                    }
                } catch(RuntimeException r) {
                    toWrite = r.getMessage();
                }
                TextView toDo = binding.Output;
                toDo.setText(toWrite);
            }
        });

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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
        String[] spltPositives = positives.toUpperCase().split(" ");
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
            // Input cleansing for if test number greater than n
            if ((Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length()))) > n) {
                throw new java.lang.RuntimeException("Invalid test input: test number greater than n.");
            }
            if (spltPositives[i].charAt(0) == 'C') { // Column test
                colTests[(Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length())))-1] = 1;
            } else if (spltPositives[i].charAt(0) == 'D') { // Diag test
                diagTests[(Integer.parseInt(spltPositives[i].substring(1,spltPositives[i].length())))-1] = 1;
            } else if (spltPositives[i].charAt(0) == 'R') { // Row test
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