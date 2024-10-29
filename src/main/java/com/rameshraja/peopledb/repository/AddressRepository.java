package com.rameshraja.peopledb.repository;

import com.rameshraja.peopledb.annotation.SQL;
import com.rameshraja.peopledb.model.Address;
import com.rameshraja.peopledb.model.CrudOperation;
import com.rameshraja.peopledb.model.Region;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRepository extends CrudRepository<Address>{

    public static final String SAVE_ADDRESS_SQL = """
            INSERT INTO ADDRESSES(STREET_ADDRESS,ADDRESS2,CITY,STATE,POSTCODE,COUNTRY,COUNTY,REGION)
            VALUES(?,?,?,?,?,?,?,?);
            """;
    public static final String FOUND_BY_ID_ADDRESS_SQL= """
            SELECT ID,STREET_ADDRESS,ADDRESS2,CITY,STATE,POSTCODE,COUNTRY,COUNTY,REGION
            FROM ADDRESSES
            WHERE ID=?;
            """;
    public static final String COUNT_SQL = "SELECT COUNT(*) FROM ADDRESSES;";

    public static final String UPDATE_ADDRESS_SQL ="UPDATE ADDRESSES SET STREET_ADDRESS=?,ADDRESS2=?,CITY=?,STATE=?,POSTCODE=?,COUNTRY=?,COUNTY=?,REGION=? WHERE ID=?;";

    public static final String DELETE_ADDRESS_SQL="DELETE FROM ADDRESSES WHERE ID=?;";

    public AddressRepository(Connection connection){
        super(connection);
    }

    @Override
    @SQL(operationType = CrudOperation.FIND_BY_ID,value =FOUND_BY_ID_ADDRESS_SQL)
    @SQL(operationType = CrudOperation.COUNT,value = COUNT_SQL)
    @SQL(operationType = CrudOperation.DELETE_ONE,value = DELETE_ADDRESS_SQL)
    Address extractEntityFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("ID");
        String streetAddress = rs.getString("STREET_ADDRESS");
        String address2 = rs.getString("ADDRESS2");
        String city = rs.getString("CITY");
        String state = rs.getString("STATE");
        String postcode = rs.getString("POSTCODE");
        String country = rs.getString("COUNTRY");
        String county = rs.getString("COUNTY");
        String region = rs.getString("REGION").toUpperCase();
        return new Address(id,streetAddress,address2,city,state,postcode,country,county, Region.valueOf(region));
    }

    @Override
    @SQL(operationType = CrudOperation.SAVE,value = SAVE_ADDRESS_SQL)
    void mapForSave(Address entity, PreparedStatement ps) throws SQLException {
        ps.setString(1,entity.getStreetAddress());
        ps.setString(2,entity.getAddress2());
        ps.setString(3,entity.getCity());
        ps.setString(4,entity.getState());
        ps.setString(5,entity.getPostalCode());
        ps.setString(6,entity.getCountry());
        ps.setString(7,entity.getCounty());
        ps.setString(8,entity.getRegion().toString());
    }

    @Override
    @SQL(operationType = CrudOperation.UPDATE,value = UPDATE_ADDRESS_SQL)
    void mapForUpdate(Address entity, PreparedStatement ps) throws SQLException {
        ps.setString(1,entity.getStreetAddress());
        ps.setString(2,entity.getAddress2());
        ps.setString(3,entity.getCity());
        ps.setString(4,entity.getState());
        ps.setString(5,entity.getPostalCode());
        ps.setString(6,entity.getCountry());
        ps.setString(7,entity.getCounty());
        ps.setString(8,entity.getRegion().toString());
    }

    public void update(Address address){
        super.update(address,9);
    }
}
