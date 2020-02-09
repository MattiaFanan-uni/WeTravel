package com.gruppo3.wetravel;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

@Entity
public class PartakeUserRelation {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "username",
            entityColumn = "owner"
    )
    public Partake partake;
}
