package com.palantir.hackthon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class BadAgg {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            QueryCoordinator coordinator = new QueryCoordinator(1, sc.nextLine());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String [] command = br.readLine().split(" ");
                if (command[0].equalsIgnoreCase("average")) {
                    System.out.println(coordinator.average(command[1]));
                } else if (command[0].equalsIgnoreCase("top10")) {
                    System.out.println(coordinator.top10(command[1]));
                } else if (command[0].equalsIgnoreCase("rangemax")) {
                    System.out.println(coordinator.rangeMax(command[1], command[2]));
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

