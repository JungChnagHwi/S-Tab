package com.sixb.note.entity;

import com.sixb.note.entity.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Node("User")
public class User extends BaseTimeEntity {

    @Id
    @Property("userId")
    private long userId;

    @Property("nickname")
    private String nickname;

    @Property("profileImg")
    private String profileImg;

    @Relationship(type = "Join")
    private List<Space> spaces;

    @Relationship(type = "Like")
    private List<Folder> folders;

    @Relationship(type = "Like")
    private List<Note> notes;

    @Relationship(type = "Like")
    private List<Page> pages;
}