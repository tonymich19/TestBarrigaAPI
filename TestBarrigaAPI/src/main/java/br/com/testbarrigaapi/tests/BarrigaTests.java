package br.com.testbarrigaapi.tests;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.com.testbarrigaapi.BaseTest;

public class BarrigaTests extends BaseTest{

	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;	
	}
}
