package com.rameshraja.peopledb.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {
    @Test
    void testForEquality(){
        Person p1=new Person("p1","smith", ZonedDateTime.now());
        Person p2=new Person("p1","smith", ZonedDateTime.now());
        assertThat(p1).isEqualTo(p2);
    }
    @Test
    void testForInequality(){
        Person p1=new Person("p1","smith",ZonedDateTime.now());
        Person p2=new Person("p2","smith",ZonedDateTime.now());
        assertThat(p1).isNotEqualTo(p2);
    }
}