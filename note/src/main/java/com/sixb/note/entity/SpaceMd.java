package com.sixb.note.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "SpaceMd")
public class SpaceMd {
    @Id
    private String id;
    private String data;
}
