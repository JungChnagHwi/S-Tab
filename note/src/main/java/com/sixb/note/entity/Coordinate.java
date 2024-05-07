package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Coordinate")
public class Coordinate {
    private Float x;
    private Float y;
}
