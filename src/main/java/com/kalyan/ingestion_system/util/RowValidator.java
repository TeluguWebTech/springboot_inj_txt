package com.kalyan.ingestion_system.util;


import com.kalyan.ingestion_system.exception.InvalidRowException;

public class RowValidator {

    public static void validate(String[] columns) {

        if (columns.length != 4)
            throw new InvalidRowException("Invalid column count");

        if (columns[0].isEmpty())
            throw new InvalidRowException("Name empty");

    }

}