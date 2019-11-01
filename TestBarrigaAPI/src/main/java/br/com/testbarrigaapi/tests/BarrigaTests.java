package br.com.testbarrigaapi.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.testbarrigaapi.BaseTest;

public class BarrigaTests extends BaseTest{
	private String TOKEN;
	
	@Before
	public void login(){
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "tony_michael19@hotmail.com");
		login.put("senha", "123456");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
	}
	

	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)		
		;
	}
	@Test
	public void AdicionarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"nova conta\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
				
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"nova alteração\"}")
		.when()
			.put("/contas/38994")
		.then()
			.statusCode(200)
		;
	}
}
