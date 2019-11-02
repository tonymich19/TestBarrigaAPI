package br.com.testbarrigaapi.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import io.restassured.internal.ResponseSpecificationImpl.HamcrestAssertionClosure;

import java.util.HashMap;
import java.util.Map;





import org.hamcrest.Matchers;
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
	
	@Test
	public void NaoDeveAdicionarContaRepetida() {
		String nome = given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"nova alteração\"}")
		.when()
			.get("/contas")
		.then()
			.extract().path("nome[0]").toString()		
		;
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"" + nome + "\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void validarCamposObrigatorios() {
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void naoDeveInserirMovimentacaoDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("02/02/2079");
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void naoDeveContaComMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/contas/38994")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveCalcularSaldo() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 38994}.saldo", is("100.00"))
			.log().all()
		;
	}
	
	private Movimentacao getMovimentacaoValida(){
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(38994);
		//mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descrição da movimentação");
		mov.setEnvolvido("Envolvido na movimentação");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2010");
		mov.setData_pagamento("01/01/2015");		
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}
}
