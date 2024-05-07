package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Path")
public class Path {
    private String penType;
    private Float strokeWidth;
    private String color;
    private List<Coordinate> coordinates;
}
