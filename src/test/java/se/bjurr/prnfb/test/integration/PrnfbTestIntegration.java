package se.bjurr.prnfb.test.integration;

import static com.google.common.collect.Lists.newArrayList;
import static com.jayway.restassured.RestAssured.basic;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.config.LogConfig.logConfig;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.equalTo;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.COMMENTED;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;

import org.junit.Before;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.response.Response;

public class PrnfbTestIntegration {
 @Before
 public void before() throws InterruptedException {
  RestAssured.baseURI = "http://localhost:7990/bitbucket/rest/prnfb-admin/1.0";
  RestAssured.authentication = basic("admin", "admin");
  RestAssured.requestSpecification = new RequestSpecBuilder()//
    .log(LogDetail.ALL).build()//
    .auth().preemptive().basic("admin", "admin")//
    .accept(JSON)//
    .contentType(JSON);
  RestAssured.config()
    .logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails().enablePrettyPrinting(true));
  RestAssured.responseSpecification = new ResponseSpecBuilder()//
    .build();

  boolean startedOk = false;
  int waitCount = 0;
  while (!startedOk) {
   try {
    waitCount++;
    if (waitCount > 90) {
     fail("Giving up waiting for server to start!");
    }
    Response response = get("http://localhost:7990/bitbucket/").andReturn();
    if (response.getStatusCode() == 200) {
     startedOk = true;
    }
   } catch (Exception e) {
    System.out.println("Waiting for Bitbucket to start");
    Thread.sleep(1000);
   }
  }
 }

 // @Test
 public void testThatButtonsCanBeStored() {
  ButtonDTO buttonDto = new ButtonDTO();
  buttonDto.setName("name");
  buttonDto.setProjectKey("projectKey");
  buttonDto.setRepositorySlug("repositorySlug");
  buttonDto.setUserLevel(ADMIN);

  String uuid = given()//
    .body(buttonDto)//
    .when()//
    .post("/settings/buttons")//
    .then()//
    .log().all()//
    .extract().body().path("uuid");

  when()//
    .get("/settings/buttons/" + uuid)//
    .then()//
    .log().all()//
    .body("name", equalTo("name"))//
    .body("projectKey", equalTo("projectKey"))//
    .body("repositorySlug", equalTo("repositorySlug"))//
    .body("userLevel", equalTo(ADMIN.name()));
 }

 // @Test
 public void testThatGlobalSettingsCanBeStored() {
  SettingsDataDTO settingsData = new SettingsDataDTO();
  settingsData.setAdminRestriction(ADMIN);
  settingsData.setShouldAcceptAnyCertificate(false);
  settingsData.setKeyStore("keyStore");
  settingsData.setKeyStorePassword("keyStorePassword");
  settingsData.setKeyStoreType("keyStoreType");

  given()//
    .body(settingsData)//
    .when()//
    .post("/settings")//
    .then()//
    .log().all();

  when()//
    .get("/settings")//
    .then()//
    .log().all()//
    .body("shouldAcceptAnyCertificate", equalTo(false))//
    .body("adminRestriction", equalTo(ADMIN.name()))//
    .body("keyStore", equalTo("keyStore"))//
    .body("keyStorePassword", equalTo("keyStorePassword"))//
    .body("keyStoreType", equalTo("keyStoreType"));

  settingsData.setAdminRestriction(EVERYONE);
  settingsData.setShouldAcceptAnyCertificate(true);
  settingsData.setKeyStore("keyStore2");
  settingsData.setKeyStorePassword("keyStorePassword2");
  settingsData.setKeyStoreType("keyStoreType2");

  given()//
    .body(settingsData)//
    .when()//
    .post("/settings")//
    .then()//
    .log().all();

  when()//
    .get("/settings")//
    .then()//
    .log().all()//
    .body("shouldAcceptAnyCertificate", equalTo(true))//
    .body("adminRestriction", equalTo(EVERYONE.name()))//
    .body("keyStore", equalTo("keyStore2"))//
    .body("keyStorePassword", equalTo("keyStorePassword2"))//
    .body("keyStoreType", equalTo("keyStoreType2"));
 }

 // @Test
 public void testThatNotificationsCanBeStored() {
  NotificationDTO notificationDto = new NotificationDTO();
  notificationDto.setName("name");
  notificationDto.setTriggers(newArrayList(COMMENTED.name()));
  notificationDto.setUrl("http://bjurr.se/");

  String uuid = given()//
    .body(notificationDto)//
    .when()//
    .post("/settings/notifications")//
    .then()//
    .log().all()//
    .extract().body().path("uuid");

  when()//
    .get("/settings/notifications/" + uuid)//
    .then()//
    .log().all()//
    .body("name", equalTo("name"))//
    .body("triggers[0]", equalTo(COMMENTED.name()))//
    .body("url", equalTo("http://bjurr.se/"));
 }
}
