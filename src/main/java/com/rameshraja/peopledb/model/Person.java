package com.rameshraja.peopledb.model;

import com.rameshraja.peopledb.annotation.ID;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Person{
    @ID
    private Long id;

    private String firstName;
    private String lastName;
    private ZonedDateTime dob;
    private BigDecimal salary = new BigDecimal("0");
    private String email;
    private Optional<Address> homeAddress=Optional.empty();
    private Optional<Address> businessAddress=Optional.empty();
    private Optional<Person> spouse=Optional.empty();
    private Set<Person> children=new HashSet<>();
    private Optional<Person> parent=Optional.empty();

    public Person(String firstName, String lastName, ZonedDateTime dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public Person(Long id, String firstName, String lastName, ZonedDateTime dob, BigDecimal salary) {
        this(firstName, lastName, dob);
        this.setId(id);
        this.salary = salary;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ZonedDateTime getDob() {
        return dob;
    }

    public void setDob(ZonedDateTime dob) {
        this.dob = dob;
    }


    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(id, person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(dob.withZoneSameInstant(ZoneId.of("+0")).truncatedTo(ChronoUnit.SECONDS), person.dob.withZoneSameInstant(ZoneId.of("+0")).truncatedTo(ChronoUnit.SECONDS)) && Objects.equals(salary, person.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, dob, salary);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +'\''+
                ", email="+email + '\''+
                ", Salary="+salary.toString()+
                '}';
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = Optional.ofNullable(homeAddress);
    }

    public void setHomeAddress(Optional<Address> homeAddress){
        this.homeAddress=homeAddress;
    }

    public Optional<Address> getHomeAddress() {
        return homeAddress;
    }

    public void setBusinessAddress(Optional<Address> businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setBusinessAddress(Address businessAddress){
        this.businessAddress=Optional.ofNullable(businessAddress);
    }

    public Optional<Address> getBusinessAddress() {
        return businessAddress;
    }

    public void setSpouse(Person spouse) {
        this.spouse = Optional.ofNullable(spouse);
    }

    public Optional<Person> getSpouse() {
        return spouse;
    }

    public void addChild(Person child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void setParent(Person parent) {
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<Person> getParent() {
        return parent;
    }

    public Set<Person> getChildren(){
        return this.children;
    }
}
