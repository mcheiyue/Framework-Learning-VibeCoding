package com.example.demo.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTests {

	@Test
	void shouldSerializeSuccessAndCoreFieldsForSuccessResult() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Result<String> result = Result.success("hello");

		JsonNode node = mapper.readTree(mapper.writeValueAsString(result));
		assertTrue(node.has("code"));
		assertTrue(node.has("message"));
		assertTrue(node.has("data"));
		assertTrue(node.has("timestamp"));
		assertTrue(node.has("traceId"));
		assertTrue(node.has("success"));

		assertEquals(ResultCode.SUCCESS.getCode(), node.get("code").asInt());
		assertEquals(ResultCode.SUCCESS.getMessage(), node.get("message").asText());
		assertEquals("hello", node.get("data").asText());
		assertTrue(node.get("timestamp").asLong() > 0);
		assertEquals("", node.get("traceId").asText());
		assertTrue(node.get("success").asBoolean());
	}

	@Test
	void shouldSerializeSuccessFalseForErrorResult() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Result<Void> result = Result.error(ResultCode.BAD_REQUEST);

		JsonNode node = mapper.readTree(mapper.writeValueAsString(result));
		assertFalse(node.get("success").asBoolean());
	}
}
