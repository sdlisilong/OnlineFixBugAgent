package com.handle;

public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Print print = new Print();

        while (true) {
            Thread.sleep(2000);
            print.print();
        }
    }
}
