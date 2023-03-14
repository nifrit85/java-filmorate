package ru.yandex.practicum.filmorate.storage.query;

public class QueryForUsers {
    public static final String SELECT_BY_ID =
            "select * from USERS where USER_ID = ? ";

    public static final String SELECT_ALL =
            "select * from USERS ";

    public static final String UPDATE =
            "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ? ";

    public static final String SELECT_FRIENDSHIP =
            "select * from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ? ";

    public static final String INSERT_FRIENDSHIP =
            "insert into FRIENDSHIP (USER_ID,FRIEND_ID,IS_CONFIRMED) values (?,?,false) ";

    public static final String UPDATE_FRIENDSHIP =
            "update FRIENDSHIP set USER_ID = ?, FRIEND_ID = ?, IS_CONFIRMED = ? where FRIENDSHIP_ID = ? ";

    public static final String DELETE_FRIENDSHIP =
            "delete from FRIENDSHIP where FRIENDSHIP_ID = ? ";
    public static final String USER_ID_IN =
            "USER_ID in ";

    public static final String SELECT_FRIEND =
            "( select FRIEND_ID  from FRIENDSHIP where USER_ID = ? UNION select USER_ID from FRIENDSHIP where FRIEND_ID = ? and IS_CONFIRMED = true) ";
}
