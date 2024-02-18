import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class AccountPageTests {

    private String PHPSESSID;
    private String MANTIS_secure_session;
    private String MANTIS_STRING_COOKIE;
    private Map<String, String> cookies = new HashMap<>();

    @BeforeEach
    public void getCookies() {
        Response response = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .body("return=%2Fmantisbt%2Fmy_view_page.php&username=admin&password=admin20&secure_session=on")
                .post("https://academ-it.ru/mantisbt/login.php")
                .andReturn();

        PHPSESSID = response.cookie("PHPSESSID");
        System.out.println("PHPSESSID = " + PHPSESSID);

        MANTIS_secure_session = response.cookie("MANTIS_secure_session");
        System.out.println("MANTIS_secure_session = " + MANTIS_secure_session);

        MANTIS_STRING_COOKIE = response.cookie("MANTIS_STRING_COOKIE");
        System.out.println("MANTIS_STRING_COOKIE = " + MANTIS_STRING_COOKIE);

        cookies.put("PHPSESSID", PHPSESSID);
        cookies.put("MANTIS_secure_session", MANTIS_secure_session);
        cookies.put("MANTIS_STRING_COOKIE", MANTIS_STRING_COOKIE);
    }

    @Test
    public void getAccountPageTest() {
        Response response = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        System.out.println("\nResponse:");
        response.prettyPrint();

        Assertions.assertEquals(200, response.statusCode(), "Unexpected status code");
        Assertions.assertTrue(response.body().asString().contains("Real Name"));
    }

    @Test
    public void updateAccountRealNameTest() {
        Response responseUpdateRealName = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies)
                .body("password_current=&password=&password_confirm=&email=rov55an3014%40mail.ru&realname=unique+real+name")
                .post("https://academ-it.ru/mantisbt/account_update.php")
                .andReturn();

        System.out.println("\nResponse:");
        responseUpdateRealName.prettyPrint();

        Assertions.assertEquals(200, responseUpdateRealName.statusCode(), "Unexpected status code");
        Assertions.assertTrue(responseUpdateRealName.body().asString().contains("Real name successfully updated"));

        Response responseAccountPage = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        Assertions.assertTrue(responseAccountPage.body().asString().contains("name=\"realname\" value=\"unique real name\""));
    }
}
