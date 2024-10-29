package com.rameshraja.peopledb.repository;

import com.rameshraja.peopledb.annotation.SQL;
import com.rameshraja.peopledb.model.Address;
import com.rameshraja.peopledb.model.CrudOperation;
import com.rameshraja.peopledb.model.Person;
import com.rameshraja.peopledb.model.Region;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PeopleRepository extends CrudRepository<Person> {
    /**
     * returns the string that represents the sql needed to retrieve one entity
     * The sql must contain one parameter, i.e, "?", that will bind to the entity's ID.
     */
    public static final String SAVE_PERSON_SQL = """
            INSERT INTO PEOPLE(FIRST_NAME,LAST_NAME,DOB,SALARY,EMAIL,HOME_ADDRESS,BUSINESS_ADDRESS,SPOUSE,PARENT)
            VALUES(?,?,?,?,?,?,?,?,?);
            """;
    public static final String FOUND_BY_ID_SQL = """
                    SELECT
                    P.ID AS P_ID,P.FIRST_NAME AS P_FIRST_NAME,P.LAST_NAME AS P_LAST_NAME,P.DOB AS P_DOB,P.SALARY AS P_SALARY,P.HOME_ADDRESS AS P_HOME_ADDRESS,
                    S.ID AS S_ID,S.FIRST_NAME AS S_FIRST_NAME,S.LAST_NAME AS S_LAST_NAME,S.DOB AS S_DOB,S.SALARY AS S_SALARY,S.HOME_ADDRESS AS S_HOME_ADDRESS,
                    C.ID AS C_ID,C.FIRST_NAME AS C_FIRST_NAME,C.LAST_NAME AS C_LAST_NAME,C.DOB AS C_DOB,C.SALARY AS C_SALARY,C.HOME_ADDRESS AS C_HOME_ADDRESS,
                    HOME.ID AS HOME_ID,HOME.STREET_ADDRESS AS HOME_STREET_ADDRESS,HOME.ADDRESS2 AS HOME_ADDRESS2,HOME.CITY AS HOME_CITY,HOME.STATE AS HOME_STATE,HOME.POSTCODE AS HOME_POSTCODE,HOME.COUNTRY AS HOME_COUNTRY,HOME.COUNTY AS HOME_COUNTY,HOME.REGION AS HOME_REGION,
                    BIZ.ID AS BIZ_ID,BIZ.STREET_ADDRESS AS BIZ_STREET_ADDRESS,BIZ.ADDRESS2 AS BIZ_ADDRESS2,BIZ.CITY AS BIZ_CITY,BIZ.STATE AS BIZ_STATE,BIZ.POSTCODE AS BIZ_POSTCODE,BIZ.COUNTRY AS BIZ_COUNTRY,BIZ.COUNTY AS BIZ_COUNTY,BIZ.REGION AS BIZ_REGION
                    FROM PEOPLE AS P
                    LEFT JOIN ADDRESSES AS HOME ON P.HOME_ADDRESS = HOME.ID
                    LEFT JOIN ADDRESSES AS BIZ ON P.BUSINESS_ADDRESS = BIZ.ID
                    LEFT JOIN PEOPLE AS S ON S.ID = P.SPOUSE
                    LEFT JOIN PEOPLE AS C ON P.ID = C.PARENT
                    WHERE P.ID=?;
            """;
    public static final String UPDATE_PERSON_SQL = "UPDATE PEOPLE SET FIRST_NAME=?,LAST_NAME=?,DOB=?,SALARY=? WHERE ID=?;";
    public static final String FIND_ALL_SQL = """
            SELECT P.ID AS P_ID,P.FIRST_NAME AS P_FIRST_NAME,P.LAST_NAME AS P_LAST_NAME,P.DOB AS P_DOB,P.SALARY AS P_SALARY,P.HOME_ADDRESS AS P_HOME_ADDRESS
            FROM PEOPLE AS P
            LIMIT 100;
            """;
    public static final String COUNT_SQL = "SELECT COUNT(*) FROM PEOPLE;";
    public static final String DELETE_PERSON_SQL = "DELETE FROM PEOPLE WHERE ID=?;";
    public static final String DELETE_MULTIPLE_PEOPLE_SQL = "DELETE FROM PEOPLE WHERE ID IN (:ids);";

    private AddressRepository addressRepository = null;

    private Map<String, Integer> aliasColumnIdxMap = new HashMap<>();

    public PeopleRepository(Connection connection) {
        super(connection);
        addressRepository = new AddressRepository(connection);
    }

    @Override
    @SQL(value = FOUND_BY_ID_SQL, operationType = CrudOperation.FIND_BY_ID)
    @SQL(value = FIND_ALL_SQL, operationType = CrudOperation.FIND_ALL)
    @SQL(value = COUNT_SQL, operationType = CrudOperation.COUNT)
    @SQL(value = DELETE_PERSON_SQL, operationType = CrudOperation.DELETE_ONE)
    @SQL(value = DELETE_MULTIPLE_PEOPLE_SQL, operationType = CrudOperation.DELETE_MANY)
    Person extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Person finalPerson = null;
        // resultset contains the left join results so one person would exist in multiple rows that's why we used do while to get particular person's children
        // one person can have multiple children
        do {
            // extract the person aka main person
            Person person = extractPerson(rs, "P_").get();

            //if new person exist then update the finalPerson
            if (finalPerson == null) {
                finalPerson = person;
                // if finalPerson value different from currentPerson break the loop
                // go back the previous record
            } else if (!finalPerson.equals(person)) {
                rs.previous();
                break;
            }

            Optional<Person> child = extractPerson(rs, "C_");

            child.ifPresent(finalPerson::addChild);


            Optional<Person> spouse = extractPerson(rs, "S_");
            spouse.ifPresent(finalPerson::setSpouse);

            Address homeAddress = extractAddress(rs, "HOME_");
            finalPerson.setHomeAddress(homeAddress);

            Address bizAddress = extractAddress(rs, "BIZ_");
            finalPerson.setBusinessAddress(bizAddress);
        } while (rs.next());

        return finalPerson;
    }

    private Optional<Person> extractPerson(ResultSet rs, String prefixAlias) throws SQLException {
        Long personID = getValueByAlias(prefixAlias + "ID", rs, Long.class);
        // if the personid is null return the empty optional object
        if (personID == null) return Optional.empty();
        String firstName = rs.getString(prefixAlias + "FIRST_NAME");
        String lastName = rs.getString(prefixAlias + "LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of(rs.getObject(prefixAlias + "DOB", LocalDateTime.class), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal(prefixAlias + "SALARY");

        Person person = new Person(personID, firstName, lastName, dob, salary);

        return Optional.of(person);
    }

    @Override
    @SQL(value = SAVE_PERSON_SQL, operationType = CrudOperation.SAVE)
    void mapForSave(Person person, PreparedStatement ps) throws SQLException {
        ps.setString(1, person.getFirstName());
        ps.setString(2, person.getLastName());
        ps.setObject(3, convertDOBToDateTime(person.getDob()));
        ps.setBigDecimal(4, person.getSalary());
        ps.setString(5, person.getEmail());

        associateAddressWithPerson(ps, person.getHomeAddress(), 6);
        associateAddressWithPerson(ps, person.getBusinessAddress(), 7);

        associateSpouseWithPerson(ps, person.getSpouse(), 8);

        associateParentWithPerson(ps, person.getParent(), 9);
    }

    private static void associateParentWithPerson(PreparedStatement ps, Optional<Person> parent, int index) throws SQLException {
        if (parent.isPresent()) {
            ps.setLong(index, parent.get().getId());
        } else {
            ps.setObject(index, null);
        }
    }

    private void associateSpouseWithPerson(PreparedStatement ps, Optional<Person> spouse, int index) throws SQLException {
        if (spouse.isPresent()) {
            Person spouses = save(spouse.get());
            ps.setLong(index, spouses.getId());
        } else {
            ps.setObject(index, null);
        }
    }

    private void associateAddressWithPerson(PreparedStatement ps, Optional<Address> address, int index) throws SQLException {
        if (address.isPresent()) {
            Address savedAddress = addressRepository.save(address.get());
            ps.setLong(index, savedAddress.getId());
        } else {
            ps.setObject(index, null);
        }
    }

    @Override
    @SQL(value = UPDATE_PERSON_SQL, operationType = CrudOperation.UPDATE)
    void mapForUpdate(Person person, PreparedStatement ps) throws SQLException {
        ps.setString(1, person.getFirstName());
        ps.setString(2, person.getLastName());
        ps.setObject(3, convertDOBToDateTime(person.getDob()));
        ps.setBigDecimal(4, person.getSalary());
        ps.setString(5, person.getEmail());
    }

    private static LocalDateTime convertDOBToDateTime(ZonedDateTime dob) {
        return dob.withZoneSameInstant(ZoneId.of("+0")).truncatedTo(ChronoUnit.SECONDS).toLocalDateTime();
    }

    private Address extractAddress(ResultSet rs, String aliasPrefix) throws SQLException {
        Long id = getValueByAlias(aliasPrefix + "ID", rs, Long.class);
        if (id == null) return null;
        String streetAddress = getValueByAlias(aliasPrefix + "STREET_ADDRESS", rs, String.class);
        String address2 = getValueByAlias(aliasPrefix + "ADDRESS2", rs, String.class);
        String city = rs.getString(aliasPrefix + "CITY");
        String state = rs.getString(aliasPrefix + "STATE");
        String postcode = rs.getString(aliasPrefix + "POSTCODE");
        String country = rs.getString(aliasPrefix + "COUNTRY");
        String county = rs.getString(aliasPrefix + "COUNTY");
        String region = rs.getString(aliasPrefix + "REGION").toUpperCase();
        return new Address(id, streetAddress, address2, city, state, postcode, country, county, Region.valueOf(region));
    }

    private <T> T getValueByAlias(String alias, ResultSet rs, Class<T> clazz) throws SQLException {
        int foundIdx = getIndexForAlias(alias, rs);
        return foundIdx == 0 ? null : rs.getObject(foundIdx, clazz);
    }

    private int getIndexForAlias(String alias, ResultSet rs) throws SQLException {
        // if value exist for alias then return the particular index
        if (aliasColumnIdxMap.containsKey(alias)) {
            return aliasColumnIdxMap.get(alias);
        }

        int foundIdx = 0;
        // get the total column count
        int columnCount = rs.getMetaData().getColumnCount();
        // loop over until the alias equals to the getMetadata value
        for (int colIdx = 1; colIdx <= columnCount; colIdx++) {
            if (alias.equals(rs.getMetaData().getColumnLabel(colIdx))) {
                foundIdx = colIdx;
                // caching the result
                aliasColumnIdxMap.put(alias, colIdx);
                break;
            }
        }

        return foundIdx;
    }

    @Override
    protected void postSave(Person entity, long id) {
        entity.getChildren()
                .stream()
                .forEach(this::save);
    }

    /**
     * @param person it delegates update functionality to parent class and also passes the index for placing id parameter
     */
    public void update(Person person) {
        super.update(person, 5);
    }
}

