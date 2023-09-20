package com.af.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Machine {

    private Long id;
    private String age;


    public Machine(){

    }

    public Machine(Long id, String age) {
        this.id = id;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
