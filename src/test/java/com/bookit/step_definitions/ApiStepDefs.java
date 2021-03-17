package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiStepDefs {

    String token;
    Response response;
    String emailGlobal;
    JsonPath jsonPath;
    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtils.generateToken(email,password);
        emailGlobal = email;

    }


    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        //send get request to retrieve current user information
        String url = ConfigurationReader.get("qa2api.uri")+"/api/users/me";

        response=     given().accept(ContentType.JSON)
                .and()
                .header("Authorization",token)
                .when()
                .get(url);


    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {

        Assert.assertEquals(statusCode,response.statusCode());

    }


    @Then("the information about current user from api and database should match")
    public void the_information_about_current_user_from_api_and_database_should_match() {
        //API -DB
        //get information from database
        String query = "select id,firstname,lastname,role\n" +
                "from users\n" +
                "where email ='"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);
        long expectedId = (long) rowMap.get("id");
        String expectedFirstName = (String) rowMap.get("firstname");
        String expectedLastName = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();

        long actualId = jsonPath.getLong("id");
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //compare API - DB
        Assert.assertEquals(expectedId,actualId);
        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);

        //save api response inside the map
        //using deserialization of but how  THİS İS NOT WORK PROPER RESULT NOT NEEDED
//        Map<String,Object> apiMap =response.as(Map.class);
//        System.out.println("apiMap  = "  + apiMap);

    }

    @Then("UI,API and Database user information must be match")
    public void ui_API_and_Database_user_information_must_be_match() {
        //API and DB
        //get information from database
        String query = "select id,firstname,lastname,role\n" +
                "from users\n" +
                "where email ='"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);
        long expectedId = (long) rowMap.get("id");
        String expectedFirstName = (String) rowMap.get("firstname");
        String expectedLastName = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();

        long actualId = jsonPath.getLong("id");
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //compare API - DB
        Assert.assertEquals(expectedId,actualId);
        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);


        //GET INFORMATION FROM UI
        SelfPage selfPage = new SelfPage();

        String actualUIFullName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        //UI vs DB
        String expectedFullName = expectedFirstName+" "+expectedLastName;

        Assert.assertEquals(expectedFullName,actualUIFullName);
        Assert.assertEquals(expectedRole,actualUIRole);

        //UI vs API
        //Create a fullname for api
        String actualFullName = actualFirstName+" "+actualLastName;

        Assert.assertEquals(actualFullName,actualUIFullName);
        Assert.assertEquals(actualRole,actualUIRole);


    }

    @Given("get the all Information from api")
    public void getTheAllInformationFromApi() { //all info from APİ -->postman


        //get fistName, lastName and role from the API-->postman
        String url = ConfigurationReader.get("qa2api.uri")+"/api/students/me";

        response=     given().accept(ContentType.JSON)
                .and()
                .header("Authorization",token)
                .when()
                .get(url);

        //get information from api assign to json
        jsonPath = response.jsonPath();

        String  firstname = jsonPath.getString("firstName");
        String lastName = jsonPath.getString("lastName");
        String fullName =firstname+" "+lastName;
        System.out.println("fullName = "+ fullName);

        String role= jsonPath.getString("role");
        System.out.println("role = "+ role);

        //get team
        url = ConfigurationReader.get("qa2api.uri")+"/api/teams/my";
        response=     given().accept(ContentType.JSON)
                .and()
                .header("Authorization",token)
                .when()
                .get(url);

        //get information from api assign to json
        jsonPath= response.jsonPath();

        String team =jsonPath.getString("name"); // bunu dene getString  or get
        System.out.println("team " +team);

        //get batch
        url = ConfigurationReader.get("qa2api.uri")+"/api/batches/my";
        response=     given().accept(ContentType.JSON)
                .and()
                .header("Authorization",token)
                .when()
                .get(url);

        //get information from api assign to json
        jsonPath= response.jsonPath();

        String batch =jsonPath.getString("number");
        System.out.println("batch " +batch);

        //get campus

        url = ConfigurationReader.get("qa2api.uri")+"/api/campuses/my";
        response=     given().accept(ContentType.JSON)
                .and()
                .header("Authorization",token)
                .when()
                .get(url);

        //get information from api assign to json
        jsonPath= response.jsonPath();

        String location =jsonPath.getString("location");
        System.out.println("location " +location);


        //get information from UI
        SelfPage selfPage =new SelfPage();
        String UIName =selfPage.name.getText();
        String UIRole =selfPage.role.getText();
        String UITeam =selfPage.team.getText();
        String UIBatch =selfPage.batch.getText().substring(1);
        String UICampus =selfPage.campus.getText();

        System.out.println("UIName = " + UIName);
        System.out.println("UIRole = " + UIRole);
        System.out.println("UITeam = " + UITeam);
        System.out.println("UIBatch = " + UIBatch);
        System.out.println("UICampus = " + UICampus);

        //get information from database---> SQL
        //get the information from data base -->fullName ,role, teamName ,Batch ,campus
        String query2 ="select u.firstname ||' '|| u.lastname as \"fullName\",u.role, t.name, t.batch_number,c.location\n" +
                "from users u left outer join team t\n" +
                "on u.team_id = t.id\n" +
                "inner join campus c\n" +
                "on c.id =t.campus_id\n" +
                "where u.email ='"+emailGlobal+"';";

        Map <String,Object> rowMap =DBUtils.getRowMap(query2);

        System.out.println("rowMap =" + rowMap);
        String fullNameDB = (String) rowMap.get("fullName").toString();
        String roleDB = (String) rowMap.get("role").toString();
        String teamNameDB = (String) rowMap.get("name").toString();
        String batchDB = (String) rowMap.get("batch_number").toString();
        String campusDB =(String) rowMap.get("location").toString();



        //Assert API --DataBase (expected)
      //  Assert.assertEquals(fullNameDB,fullName);


        //Assert UI --API (less Expected)-->DataBase (more expected)
        Assert.assertEquals(fullNameDB,fullName,UIName);
        Assert.assertEquals(roleDB,role,UIRole);
        Assert.assertEquals(teamNameDB,team,UITeam);
        Assert.assertEquals(batchDB,batch,UIBatch);
        Assert.assertEquals(campusDB,location,UICampus);

// a        //assertion TestNG   -->  actual   -  expected
           // assertion Cucumber -->  expected -  actual
    }

}
