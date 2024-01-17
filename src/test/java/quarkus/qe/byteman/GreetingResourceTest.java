package quarkus.qe.byteman;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.WithByteman;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@WithByteman
class GreetingResourceTest {

    @Test
    @BMUnitConfig(loadDirectory="target/test-classes", debug=true)
    @BMScript(value="check.btm")
    public void testModifyVariable() {
        given()
                .when().get("/modifyVariable")
                .then()
                .statusCode(200)
                .body(is("Hello from Byteman"));
    }

    @Test
    @BMRule(name = "Change value of variable",
            targetClass = "GreetingResource",
            targetMethod = "modifyVariable",
            targetLocation = "READ $response",
            action = "$response = \"Hello from Byteman\""
    )
    public void testModifyVariableRuleByAnnotation() {
        given()
                .when().get("/modifyVariable")
                .then()
                .statusCode(200)
                .body(is("Hello from Byteman"));
    }

    @Test
    @BMUnitConfig(loadDirectory="target/test-classes", debug=true)
    @BMScript(value="check.btm")
    public void testReadVariableThrowError() {
        String path = "/readVariableThrowError";
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(is("Exception from byteman was thrown"));
    }

    @Test
    @BMUnitConfig(loadDirectory="target/test-classes", debug=true)
    @BMScript(value="check2.btm")
    public void testReadBindModify() {
        String path = "/readBindModify";
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(is("Hello from Byteman. Byteman modified boolean value = true"));
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(is("Hello from RESTEasy Reactive. Byteman modified boolean value = false"));
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(is("Hello from Byteman. Byteman modified boolean value = true"));
    }
}