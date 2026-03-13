package com.kalyan.ingestion_system.util;


import com.kalyan.ingestion_system.exception.InvalidRowException;

public class RowValidator {

    public static void validate(String[] columns) {

        if (columns.length != 4)
            throw new InvalidRowException("Invalid column count");

        if (columns[0].isEmpty())
            throw new InvalidRowException("Name empty");

        double price = Double.parseDouble(columns[1]);

        if (price <= 0)
            throw new InvalidRowException("Invalid price");

        int quantity = Integer.parseInt(columns[2]);

        if (quantity <= 0)
            throw new InvalidRowException("Invalid quantity");

        if (columns[3].isEmpty())
            throw new InvalidRowException("Source empty");
    }
}