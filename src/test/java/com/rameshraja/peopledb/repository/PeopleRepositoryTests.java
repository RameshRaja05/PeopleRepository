package com.rameshraja.peopledb.repository;

import com.rameshraja.peopledb.model.Address;
import com.rameshraja.peopledb.model.Person;
import com.rameshraja.peopledb.model.Region;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PeopleRepositoryTests {
    private final String regex= ".*?\\w{2,3}\\.,(?<fname>\\w+),\\w+,(?<lname>\\w+).*,\\w,(?<email>.*@\\w+\\.\\w+).*,\\w+,(?<dob>\\d+/\\d+/\\d+).*?(?<birthTime>\\d+:\\d+:\\d+\\s\\w+).*,\\d+\\.\\d+,(?<sal>\\d+)";
    private final Pattern pat = Pattern.compile(regex);
    private Connection connection;
    private PeopleRepository peopleRepository;

    @BeforeEach
    void setup() throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/PeopleDB", "rameshraja", "password");
        connection.setAutoCommit(false);
        peopleRepository = new PeopleRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection!=null) {
            connection.close();
        }
    }

    @Test
    void canSaveOnePerson(){
        Person person=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Person savedPerson= peopleRepository.save(person);
        assertThat(savedPerson.getId()).isGreaterThan(0L);
    }

    @Test
    void canSavePersonWithHomeAddress(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Address address= new Address("123 Beale st", "Apt 2B", "Seattle", "WA", "98039", "United States", "Fulton County", Region.WEST);
        ramesh.setHomeAddress(address);

        Person savedPerson=peopleRepository.save(ramesh);
        assertThat(savedPerson.getHomeAddress().get()).isEqualTo(address);
    }

    @Test
    void canSavePersonWithSpouse(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Person jennifer=new Person("Jennifer","lawrence",ZonedDateTime.of(2002,8,15,10,30,0,0, ZoneId.of("-5")));
        ramesh.setSpouse(jennifer);

        Person savedPerson=peopleRepository.save(ramesh);
        assertThat(savedPerson.getSpouse().get()).isEqualTo(jennifer);
    }

    @Test
    void canSaveWithChildren(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        ramesh.addChild(new Person("Roney","Jr",ZonedDateTime.now()));
        ramesh.addChild(new Person("Peter","Jr",ZonedDateTime.now()));
        ramesh.addChild(new Person("jenny","Ramesh",ZonedDateTime.now()));

        Person savedPerson=peopleRepository.save(ramesh);

        savedPerson.getChildren()
                .stream()
                .map(Person::getId)
                .forEach(id->assertThat(id).isGreaterThan(0));
    }

    @Test
    void canSavePersonWithBizAddress(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Address address= new Address("123 Beale st", "Apt 2B", "Seattle", "WA", "98039", "United States", "Fulton County", Region.WEST);
        ramesh.setBusinessAddress(address);

        Person savedPerson=peopleRepository.save(ramesh);
        assertThat(savedPerson.getBusinessAddress().get()).isEqualTo(address);
    }

    @Test
    void canSaveTwoPeople(){
        Person person1=new Person("RameshRaja","P",ZonedDateTime.of(2000,11,23,12,30,0,0,ZoneId.of("-5")));
        Person person2=new Person("Rocky","Kl",ZonedDateTime.of(2001,12,22,12,30,0,0,ZoneId.of("-5")));
        Person savedPerson1=peopleRepository.save(person1);
        Person savedPerson2=peopleRepository.save(person2);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());
    }

    @Test
    void canFindPersonByID(){
        Person savedPerson = peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));
        Person foundPerson=peopleRepository.findByID(savedPerson.getId()).get();
        assertThat(foundPerson).isEqualTo(foundPerson);
    }

    @Test
    void canFindPersonByIDWithHomeAddress(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Address address= new Address("123 Beale st", "Apt 2B", "Seattle", "WA", "98039", "United States", "Fulton County", Region.WEST);
        ramesh.setHomeAddress(address);

        Person savedPerson=peopleRepository.save(ramesh);
        Optional<Person> foundPerson = peopleRepository.findByID(savedPerson.getId());
        assertThat(foundPerson.get().getHomeAddress().get()).isEqualTo(address);
    }

    @Test
    void canFindPersonByIDWithBizAddress(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Address address= new Address("123 Beale st", "Apt 2B", "Seattle", "WA", "98039", "United States", "Fulton County", Region.WEST);
        ramesh.setBusinessAddress(address);

        Person savedPerson=peopleRepository.save(ramesh);
        Optional<Person> foundPerson = peopleRepository.findByID(savedPerson.getId());
        assertThat(foundPerson.get().getBusinessAddress().get()).isEqualTo(address);
    }

    @Test
    void canFindPersonByIDWithSpouse(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        Person jennifer=new Person("Jennifer","lawrence",ZonedDateTime.of(2002,8,15,10,30,0,0, ZoneId.of("-5")));
        ramesh.setSpouse(jennifer);

        Person savedPerson=peopleRepository.save(ramesh);
        Optional<Person> foundPerson=peopleRepository.findByID(savedPerson.getId());
        assertThat(foundPerson.get().getSpouse().get().getId()).isEqualTo(jennifer.getId());
    }

    @Test
    void canFindPersonByIDWithChildren(){
        Person ramesh=new Person("RameshRaja","P", ZonedDateTime.of(2000,11,23,12,30,0,0, ZoneId.of("-5")));
        ramesh.addChild(new Person("Roney","Jr",ZonedDateTime.now()));
        ramesh.addChild(new Person("Peter","Jr",ZonedDateTime.now()));
        ramesh.addChild(new Person("jenny","Ramesh",ZonedDateTime.now()));

        Person savedPerson=peopleRepository.save(ramesh);
        Optional<Person> foundPerson=peopleRepository.findByID(savedPerson.getId());
        Set<String> firstNames=Set.of("Roney","Peter","jenny");
        foundPerson.get().getChildren()
                .stream()
                .map(Person::getFirstName)
                .forEach(fn->assertTrue(firstNames.contains(fn)));
    }

    @Test
    void testPersonNotFound(){
        Optional<Person> foundPerson=peopleRepository.findByID(-1L);
        assertThat(foundPerson).isEmpty();
    }

    @Test
    void canFindAllPeople(){
        peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));
        peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));
        peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));
        peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));
        peopleRepository.save(new Person("test", "person", ZonedDateTime.now()));

        List<Person> peopleList=peopleRepository.findAll();
        assertThat(peopleList.size()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void canCount(){
        long startCount = peopleRepository.count();
        peopleRepository.save(new Person("test","op",ZonedDateTime.now()));
        peopleRepository.save(new Person("tested","op1",ZonedDateTime.now()));
        peopleRepository.save(new Person("testing","op2",ZonedDateTime.now()));
        long endCount=peopleRepository.count();
        assertThat(endCount).isEqualTo(startCount+3);
    }

    @Test
    void canDeletePerson(){
        Person savedPerson = peopleRepository.save(new Person("test", "op", ZonedDateTime.now()));
        long startCount=peopleRepository.count();
        peopleRepository.delete(savedPerson);
        long endCount=peopleRepository.count();
        assertThat(endCount).isEqualTo(startCount-1);
    }

    @Test
    void canDeleteMultiplePeople(){
        Person p1=peopleRepository.save(new Person("test", "op", ZonedDateTime.now()));
        Person p2=peopleRepository.save(new Person("test", "op", ZonedDateTime.now()));
        Person p3=peopleRepository.save(new Person("test", "op", ZonedDateTime.now()));
        long startCount=peopleRepository.count();
        peopleRepository.delete(p1,p2,p3);
        long endCount=peopleRepository.count();
        assertThat(endCount).isEqualTo(startCount-3);
    }

    @Test
    void canUpdate(){
        Person savedPerson=peopleRepository.save(new Person("test","op",ZonedDateTime.now()));
        //Todo this person is salary is set to 0
        Person foundP=peopleRepository.findByID(savedPerson.getId()).get();

        // update the person salary 90000
        savedPerson.setSalary(new BigDecimal("90000.87"));
        //update the person in db
        peopleRepository.update(savedPerson, 5);

        //get the person details from db
        Person foundP2=peopleRepository.findByID(savedPerson.getId()).get();

        assertThat(foundP.getSalary()).isNotEqualByComparingTo(foundP2.getSalary());
    }

    @Test
    @Disabled
    void loadData() throws IOException, SQLException {
        Files.lines(Path.of("C:\\Users\\rames\\Desktop\\Hr5m.csv"))
                .skip(400001)
                .limit(100000)
                .map(pat::matcher)
                .map((mat)->{
                    if(!mat.find()){
                        return new Person("Unknown","k",ZonedDateTime.now());
                    }
                    LocalDate dob=LocalDate.parse(mat.group("dob"), DateTimeFormatter.ofPattern("M/d/yyyy"));
                    LocalTime birthTime=LocalTime.parse(mat.group("birthTime"),DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    Person person=new Person(mat.group("fname"),mat.group("lname"),ZonedDateTime.of(LocalDateTime.of(dob,birthTime),ZoneId.of("+0")));
                    person.setSalary(new BigDecimal(mat.group("sal")));
                    person.setEmail(mat.group("email"));
                    return person;
                })
                .forEach(peopleRepository::save);
        connection.commit();
    }

}
