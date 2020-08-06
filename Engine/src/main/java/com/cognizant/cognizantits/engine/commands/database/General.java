/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.commands.database;

import com.cognizant.cognizantits.engine.commands.Command;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.support.Status;
import com.google.common.base.Objects;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 
 */
public class General extends Command {

    public static Connection dbconnection;
    static Statement statement;
    static ResultSet result;
    static ResultSetMetaData resultData;
    static final String DB_CONN_STR = "db.connection.string";
    static final String DB_USER = "db.user";
    static final String DB_PWD = "db.password";
    static final String DB_DRIVER = "db.driver";
    static final int DB_TIME_OUT = 30;
    static final String DB_COMMIT = "db.commit";
    static final Pattern INPUTS = Pattern.compile("([^{]+?)(?=\\})");
    static List<String> colNames = new ArrayList<>();

    public General(CommandControl cc) {
        super(cc);
    }

    public boolean verifyDbConnection() throws ClassNotFoundException, SQLException {
        if (getDriver() != null) {
            Class.forName(getDriver());
            if (getConnectionString() != null && getDBUser() != null && getDBPassword() != null) {
                dbconnection = DriverManager.getConnection(getDataBaseData(DB_CONN_STR), getDataBaseData(DB_USER),
                        getDataBaseData(DB_PWD));
            } else if (getConnectionString() != null) {
                dbconnection = DriverManager.getConnection(getDataBaseData(DB_CONN_STR));
            }
            return (dbconnection != null);
        }
        return false;
    }

    public void executeSelect() throws SQLException {
        initialize();
        result = statement.executeQuery(Data);
        resultData = result.getMetaData();
        populateColumnNames();
    }

    public boolean executeDML() throws SQLException {
        initialize();
        return (statement.executeUpdate(Data) >= 0);
    }

    private void initialize() throws SQLException {
        colNames.clear();
        dbconnection.setAutoCommit(usercancommit());
        statement = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        statement.setQueryTimeout(DB_TIME_OUT);
        resolveVars();
    }

    public boolean closeConnection() throws SQLException {
        if (dbconnection != null && statement != null && result != null) {
            dbconnection.close();
            statement.close();
            result.close();
            return dbconnection.isClosed() && statement.isClosed() && result.isClosed();
        }
        return true;
    }

    public boolean assertDB(String columnName, String condition) {
        boolean isExist = false;
        try {
            result.beforeFirst();
            if (getColumnIndex(columnName) != -1) {
                while (result.next()) {
                    if (Objects.equal(result.getString(columnName), condition)) {
                        isExist = true;
                        break;
                    }
                }
            } else {
                Report.updateTestLog(Action, "Column " + columnName + " doesn't exist", Status.FAIL);
            }
        } catch (SQLException ex) {
            Report.updateTestLog(Action, "Error asserting the value in DB " + ex.getMessage(), Status.FAIL);
            return false;
        }
        return isExist;
    }

    public void storeValue(String input, String condition, boolean isGlobal) {
        String value;
        int rowIndex = 1;
        String[] split = condition.split(",");
        if (split.length > 1) {
            rowIndex = Integer.parseInt(split[1]);
        }
        try {
            if (getColumnIndex(split[0]) != -1) {
                result.first();
                if (result.absolute(rowIndex)) {
                    value = result.getString(split[0]);
                    if (isGlobal) {
                        addGlobalVar(input, value);
                    } else {
                        addVar(input, value);
                    }
                } else {
                    Report.updateTestLog(Action, "Row " + rowIndex + " doesn't exist",
                            Status.FAIL);
                }
            } else {
                Report.updateTestLog(Action, "Column " + split[0] + " doesn't exist ",
                        Status.FAIL);
            }
        } catch (SQLException se) {
            Report.updateTestLog(Action, "Error storing value in variable " + se.getMessage(), Status.FAIL);
        }
    }

    private void resolveVars() {
        Matcher matcher = INPUTS.matcher(Data);
        Set<String> listMatches = new HashSet<>();
        while (matcher.find()) {
            listMatches.add(matcher.group(1));
        }
        listMatches.stream().forEach((s) -> {
            String replace;
            if (s.contains("%")) {
                replace = getVar(s);
            } else {
                String[] sheet = s.split(":");
                replace = userData.getData(sheet[0], sheet[1]);
            }
            if (replace != null) {
                Data = Data.replace("{" + s + "}", "'" + replace + "'");
            }
        });
    }

    public String getConnectionString() {
        return getDataBaseData(DB_CONN_STR);
    }

    public String getDBUser() {
        return getDataBaseData(DB_USER);
    }

    public String getDBPassword() {
        return getDataBaseData(DB_PWD);
    }

    public String getDriver() {
        return getDataBaseData(DB_DRIVER);
    }

    public boolean usercancommit() {
        return getDataBaseData(DB_COMMIT).equalsIgnoreCase("true");
    }

    private void populateColumnNames() throws SQLException {
        int count = resultData.getColumnCount();
        for (int index = 1; index <= count; index++) {
            colNames.add(resultData.getColumnName(index));
        }
    }

    public int getColumnIndex(String columnName) {
        return colNames.indexOf(columnName);
    }

}
