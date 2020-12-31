package com.imburse.assignment;

import org.testng.Assert;
import org.testng.annotations.*;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


public class ApiTest {
	
	// TODO: Shift these values out to a .properties / or xml file
	private String bearerTemplate = "Bearer %s";
	private String bearerToken = "";
	private String contentTypeValue = "application/json";
	private String contentTypeKey = "Content-Type";
	private String authorization = "Authorization";
	private String xaccountId = "x-account-id";
	private String orderRefKey = "orderRef";
	private String orderRefValue = "";
	
	private String instructionRef = "instructionRef";
	private String customerRef = "customerRef";
	private String directionKey = "direction";
	private String directionValue = "DEBIT";
	private String financialInstrumentId = "financialInstrumentId";
	private String amount = "amount";
	private String amountValue = "1.00";
	private String amountBadValue = "-1.33";
	private String currency = "currency";
	private String country = "country";
	private String settledByDate = "settledByDate";
	private String settledByDateValue = "2029-12-12";
	private String settledByDateBadValue = "202-12-12";
	private String schemedIdKey = "schemeId";
	private String schemeIdValue = "654EB81FF7F07F7CF5A1EE3FF6972E90";
	private String schemeIdBadValue = "badSchemeId";
	
	private String instructions = "instructions";
	private String metadata = "metadata";
	private String customerDefaults = "customerDefaults";
	private String xtenantId = "x-tenant-id";
	private String apiLocation = "https://sandbox-api.imbursepayments.com";	
	private String theOrderLocation = "/v1/order-management/";
	private String theOrderLocationDetails = "/v1/order-management/%s";
	private String authenticatorLocation = "/v1/identity/hmac";
	private String theInstructionReferenceLocation = "/v1/order-management/%s/instruction";
	
	private String accountId = "782f1b71-7ca4-4465-917f-68d58ffbec8b";
	private String tenantId = "6423ae63-59b6-4986-a949-c910ac622471";
	private String accountsPublicKey = "7934d5e6-260c-46d5-9309-e72a59cb90cd";
	private String accountsPrivateKey = "aWRpTN9tRsf2EyM8rcvz7bohO/fAg6IF+daZ1JYE9AM=";
	private String lengthErrorCode = "ORDER_REF_LENGTH_OUT_OF_RANGE";
	private String badOrderReference = "eyJhbGciOiJ@~";
	private String badInstructionReference = "bGc~iOiJ@~";
	private String badCustomerReference = "fff~jjjGc~iOiJ@~";
	private String referenceErrorCode = "ORDER_REF_CONTAINS_INVALID_CHARACTERS";
	private String instructionReferenceErrorCode = "INSTRUCTION_REF_CONTAINS_INVALID_CHARACTERS";
	private String customerReferenceErrorCode = "CUSTOMER_REF_CONTAINS_INVALID_CHARACTERS";
	private String instructionReferenceLengthErrorCode = "INSTRUCTION_REF_LENGTH_OUT_OF_RANGE";
	private String customerReferenceLengthErrorCode = "CUSTOMER_REF_LENGTH_OUT_OF_RANGE";
	private String referenceExistsErrorCode = "ORDER_ALREADY_EXISTS";
	private String amountErrorCode = "AMOUNT_OUT_OF_RANGE";
	private String malformedCurrencyError = "CURRENCY_LENGTH_OUT_OF_RANGE";
	private String malformedCountryError = "COUNTRY_LENGTH_OUT_OF_RANGE";
	private String sizeableMetaKey = "EEEEEEEEEUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUURRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOO";
	private String metaDataKeyErrorCode = "METADATA_KEY_SIZE_OUT_OF_RANGE";
	private String settleByDateErrorCode = "SETTLED_BY_DATE_IS_INVALID";
	private String schemeIdErrorCode = "SCHEME_ID_NOT_RECOGNISED";
	private String testCurrency = "EUR";
	private String malformedCurrency = "GBPEUR";
	private String malformedCountry = "GBPUSAIE";
	private String testCountry = "IE";
	private String testCountryAnother = "FR";
	private String hmac = "getit";
	private String hmacToken = "Hmac %s";
	private String accessToken = "accessToken";
	
	
	@BeforeClass
	public void authenticateToRetrieveBearerToken() {
		// The following code is a converted version of the Hmac example
		// code supplied in the Test Spec for this assignment.
		byte[] privateKeyBytes = Base64.decodeBase64(accountsPrivateKey);
		String bodySignature = "";
		long timestamp = (new Date()).getTime() / 1000;
		long nonce = timestamp;
		String unsignedSignature = accountsPublicKey + ":" + String.valueOf(nonce) + ":" + String.valueOf(timestamp) + ":" + bodySignature;
		byte[] utf8Signature = null;
		
		try {
			utf8Signature = unsignedSignature.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			
		}
		byte[] hashedSignature = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, privateKeyBytes).doFinal(utf8Signature);
		String signedSignature = new String(Base64.encodeBase64(hashedSignature));      
		hmac = accountsPublicKey + ":" + nonce + ":" + timestamp + ":" + signedSignature;		
				 
		// Get access token for API calls. 
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, String.format(hmacToken, hmac)))
				.header(new Header(contentTypeKey, contentTypeValue));
		Response response = request.post(authenticatorLocation);
		bearerToken = String.format(bearerTemplate, response.jsonPath().getString(accessToken));
		
		// Generate an Order Ref ID for use with the test cases.
		orderRefValue = String.valueOf(System.currentTimeMillis());
	}
	
	@AfterClass
	public void reportingUpdate() {
		System.out.println("----------------" + "\n" + "----------------" + "\n"  + "----------------" + "\n" +"---------------- Surefire Report located at: com.imburse.assignment/target/site/surefire-report.html" + "\n" + "----------------"  + "\n" + "----------------"  + "\n");
		System.out.println("----------------" + "\n" + "----------------" + "\n"  + "----------------" + "\n" +"---------------- Additional Reports at: com.imburse.assignment/target/surefire-reports" + "\n" + "----------------"  + "\n" + "----------------"  + "\n" + "----------------");		
	}
	
	@Test(priority=1)
	public void createOrderWithValidOrderReferenceData() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		requestParams.put(orderRefKey, orderRefValue);
		requestParams.put(instructions, authArray);
		authParam.put(testCurrency, testCountry);
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		// Verify we get a 201 response on Order Creation
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(201);	
	}
	
	@Test(priority=2)
	public void createOrderWithReferenceGreaterThanFiftyChars() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		// Create a large Order Ref
		requestParams.put(orderRefKey, orderRefValue + orderRefValue + orderRefValue + orderRefValue + orderRefValue);

		requestParams.put(instructions, authArray);
		authParam.put(testCurrency, testCountry);
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(theOrderLocation);
		
		// Assert that correct Error Code is thrown
		response.then().assertThat().statusCode(400);		
		Assert.assertEquals(response.getBody().asString().contains(lengthErrorCode), true, "Error code ORDER_REF_LENGTH_OUT_OF_RANGE not present in JSON response");		
	}
	
	@Test(priority=3)
	public void createOrderWithBadOrderReferenceData() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		// Add a ~ into an Order Ref
		requestParams.put(orderRefKey, badOrderReference);
		
		requestParams.put(instructions, authArray);
		authParam.put(testCurrency, testCountry);
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(400);
		
		// Assert that correct Error Code is thrown
		Assert.assertEquals(response.getBody().asString().contains(referenceErrorCode), true, "Error code ORDER_REF_CONTAINS_INVALID_CHARACTERS not present in JSON response");	
	}

	@Test(priority=4)
	public void createOrderWithInUseOrderReference() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		// Use existing order ref
		requestParams.put(orderRefKey, orderRefValue);
		
		requestParams.put(instructions, authArray);
		authParam.put(testCurrency, testCountry);
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(referenceExistsErrorCode), true, "Error code ORDER_ALREADY_EXISTS not present in JSON response");			
	}

	@Test(priority=5)
	public void createOrderWithMetaDataKeyGreaterThanHundredChars() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		requestParams.put(orderRefKey, String.valueOf(System.currentTimeMillis()));
		requestParams.put(instructions, authArray);
		
		// Add a large Metadata key 
		authParam.put(sizeableMetaKey, testCountry);
		
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(metaDataKeyErrorCode), true, "Error code METADATA_KEY_SIZE_OUT_OF_RANGE not present in JSON response");					
	}
	
	@Test(priority=6)
	public void createOrderWithMetaDataValueGreaterThanHundredChars() {
		// TODO: Need to find out if there is a Bug  here. I was able to create
		// an order using Postman, where the metadata value belonging to a key
		// was greater than 100 characters. Note: the Key range
		// seems like it should be less than 64, so the Spec may be old?
	}	
	
	@Test(priority=7)
	public void createOrderWithMetaDataDuplicateKey() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		String localOrderRef = String.valueOf(System.currentTimeMillis());
		requestParams.put(orderRefKey, localOrderRef);
		requestParams.put(instructions, authArray);
		
		// Duplicate the key
		authParam.put(testCurrency, testCountry);
		authParam.put(testCurrency, testCountryAnother);
		
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		// A 201 comes back, after adding a duplicate key
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(201);
		
		// Send a new request to check if the duplicate was dropped
		RequestSpecification newRequest = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue));
		
		Response newResponse = newRequest.get(String.format(theOrderLocationDetails, localOrderRef));	
		Assert.assertEquals(newResponse.getBody().asString().contains(testCountry), false, "A duplicate key 'EUR' was added to the order.");							
	}		
	
	@Test(priority=8)
	public void createOrderWithMetaDataNullKey() {
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParam = new JSONObject();
		
		String localOrderRef = String.valueOf(System.currentTimeMillis());
		requestParams.put(orderRefKey, localOrderRef);
		requestParams.put(instructions, authArray);
		
		// Add the null key to the metadata object
		authParam.put(testCurrency, JSONObject.NULL);
		
		requestParams.put(metadata, authParam);
		requestParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		// A 201 results
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(201);
		
		// Check if the key was dropped, metadata should be empty
		RequestSpecification newRequest = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue));
		Response newResponse = newRequest.get(String.format(theOrderLocationDetails, localOrderRef));	
		Map<String, String> metaData = newResponse.jsonPath().getMap(metadata);	
		Assert.assertEquals(metaData.isEmpty(), true, "The KEY EUR was added with a null value!");							
	}
	
	@Test(priority=9)
	public void createOrderWithInstructions() {
		//  With instructions, a 202 response results
		JSONObject requestParams = new JSONObject();
		JSONArray authArray = new JSONArray();
		JSONObject authParams = new JSONObject();
		
		String localOrderRefValue = String.valueOf(System.currentTimeMillis());
		authParams.put(orderRefKey, localOrderRefValue);
	
		requestParams.put(instructionRef, localOrderRefValue+localOrderRefValue);
		requestParams.put(customerRef, localOrderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		// Add the instructions
		authArray.put(requestParams);
		authParams.put(instructions, authArray);
		
		authParams.put(metadata, new JSONObject());
		authParams.put(customerDefaults, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(authParams.toString());
		
		Response response = request.post(theOrderLocation);
		response.then().assertThat().statusCode(202);		
	}
	
	// --------------------------------------------------------------------
	
	@Test(priority=10)
	public void createInstructionWithValidInstructionReference() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue+orderRefValue);
		requestParams.put(customerRef, orderRefValue+orderRefValue+orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(201);			
	}		
	
	@Test(priority=11)
	public void createInstructionWithBadInstructionReferenceData() {
		JSONObject requestParams = new JSONObject();
	
		// Add in a ~ to the instruction ref
		requestParams.put(instructionRef, badInstructionReference);
		
		requestParams.put(customerRef, orderRefValue+orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(instructionReferenceErrorCode), true, "Error code INSTRUCTION_REF_CONTAINS_INVALID_CHARACTERS not present in JSON response");		

	}
	
	@Test(priority=12)
	public void createInstructionWithInstructionRefGreaterThanFiftyChars() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue+orderRefValue+orderRefValue+orderRefValue+orderRefValue+orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(instructionReferenceLengthErrorCode), true, "Error code INSTRUCTION_REF_LENGTH_OUT_OF_RANGE not present in JSON response");		
	}	
	
	@Test(priority=13)
	public void createInstructionWithBadCustomerReference() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		
		// Add in a ~
		requestParams.put(customerRef, badCustomerReference);
		
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(customerReferenceErrorCode), true, "Error code CUSTOMER_REF_CONTAINS_INVALID_CHARACTERS not present in JSON response");	
	}

	@Test(priority=14)
	public void createInstructionWithCustomerRefGreaterThanFiftyChars() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue+orderRefValue+orderRefValue+orderRefValue+orderRefValue+orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(customerReferenceLengthErrorCode), true, "Error code CUSTOMER_REF_LENGTH_OUT_OF_RANGE not present in JSON response");		
	}
	
	@Test(priority=15)
	public void createInstructionWithBadAmount() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		
		// Put in a negative amount
		requestParams.put(amount, amountBadValue);
		
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(amountErrorCode), true, "Error code AMOUNT_OUT_OF_RANGE not present in JSON response");				
	}	
	
	@Test(priority=16)
	public void createInstructionWithMalformedCurrency() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		
		// Add a malformed currency 
		requestParams.put(currency, malformedCurrency);
		
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(malformedCurrencyError), true, "Error code CURRENCY_LENGTH_OUT_OF_RANGE not present in JSON response");		
	}

	@Test(priority=17)
	public void createInstructionWithNonExistantCountry() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		
		// Tie together a few countries
		requestParams.put(country, malformedCountry);
		
		requestParams.put(settledByDate, settledByDateValue);
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(malformedCountryError), true, "Error code COUNTRY_LENGTH_OUT_OF_RANGE not present in JSON response");				
	}
	
	@Test(priority=18)
	public void createInstructionWithBadSettleByDate() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		
		// Mess up the date format
		requestParams.put(settledByDate, settledByDateBadValue);
		
		requestParams.put(schemedIdKey, schemeIdValue);
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(settleByDateErrorCode), true, "Error code SETTLED_BY_DATE_IS_INVALID not present in JSON response");						
	}

	@Test(priority=19)
	public void createInstructionWithBadSchemeId() {
		JSONObject requestParams = new JSONObject();
	
		requestParams.put(instructionRef, orderRefValue);
		requestParams.put(customerRef, orderRefValue);
		requestParams.put(directionKey, directionValue);
		requestParams.put(financialInstrumentId, "");
		requestParams.put(amount, amountValue);
		requestParams.put(currency, testCurrency);
		requestParams.put(country, testCountry);
		requestParams.put(settledByDate, settledByDateValue);
		
		// Add an incorrect scheme ID
		requestParams.put(schemedIdKey, schemeIdBadValue);
		
		requestParams.put(metadata, new JSONObject());
		
		RequestSpecification request = RestAssured.given()
				.baseUri(apiLocation)
				.header(new Header(authorization, bearerToken))
				.header(new Header(xaccountId, accountId))
				.header(new Header(xtenantId, tenantId))
				.header(new Header(contentTypeKey, contentTypeValue))
				.body(requestParams.toString());
		
		Response response = request.post(String.format(theInstructionReferenceLocation, orderRefValue));
		response.then().assertThat().statusCode(400);
		Assert.assertEquals(response.getBody().asString().contains(schemeIdErrorCode), true, "Error code SCHEME_ID_NOT_RECOGNISED not present in JSON response");								
	}
}
