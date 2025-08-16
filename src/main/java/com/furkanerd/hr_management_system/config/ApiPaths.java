package com.furkanerd.hr_management_system.config;

public class ApiPaths {

    public static final String API_VERSION= "/api/v1";

    // DEPARTMENTS
    public static final String DEPARTMENTS= API_VERSION+"/departments";
    public static final String DEPARTMENTS_BY_ID= API_VERSION+"/departments/{id}";

    // POSITIONS
    public static final String  POSITIONS = API_VERSION+"/positions";
    public static final String  POSITIONS_BY_ID = API_VERSION+"/positions/{id}";

    // SALARY
    public static final String SALARIES=  API_VERSION+"/salaries";

    // ATTENDANCE
    public static final String ATTENDANCE =  API_VERSION+"/attendance";

    // LEAVE
    public static final String LEAVES=  API_VERSION+"/leaves";
    public static final String LEAVES_BY_ID = API_VERSION+"/leaves/{id}";
    public static final String LEAVES_APPROVE = LEAVES_BY_ID+"/approve";

    //AUTH
    public static final String AUTH= API_VERSION+"/auth";;
}
