package com.services.billingservice.service.compliance;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {

        try {


            try {

                // Creating object of ArrayList<Integer>
                ArrayList<Integer>
                        arrlist1 = new ArrayList<Integer>();

                // Populating arrlist1
                arrlist1.add(1);
                arrlist1.add(2);
                arrlist1.add(3);
                arrlist1.add(4);
                arrlist1.add(5);

                // print arrlist1
                System.out.println("ArrayList before "
                        + "removeAll() operation : "
                        + arrlist1);

                // Creating another object of  ArrayList<Integer>
                ArrayList<Integer>
                        arrlist2 = new ArrayList<Integer>();
                arrlist2.add(1);
                arrlist2.add(2);
                arrlist2.add(3);

                // print arrlist2
                System.out.println("Collection Elements"
                        + " to be removed : "
                        + arrlist2);

                // Removing elements from arrlist
                // specified in arrlist2
                // using removeAll() method
                arrlist1.removeAll(arrlist2);

                // print arrlist1
                System.out.println("ArrayList after "
                        + "removeAll() operation : "
                        + arrlist1);
            }

            catch (NullPointerException e) {
                System.out.println("Exception thrown : " + e);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
