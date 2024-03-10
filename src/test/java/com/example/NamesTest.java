package com.example;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import static org.assertj.core.api.Assertions.assertThat;

public class NamesTest {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new ParameterNamesModule());

	@Test
	void testSerialization() throws JsonProcessingException {
		Names names = new Names("John", "Doe");
		String json = objectMapper.writeValueAsString(names);

		assertThat(json).contains("John").contains("Doe");
	}

	@Test
	void testDeserialization() throws JsonProcessingException{
		/*
		* Deserialization fails with
		* com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Invalid definition for property 'fullName'
		* (of type `com.example.NamesTest$Names`): Could not find creator property with name 'fullName'
		* (known Creator properties: [firstName, lastName])
		*/
		Names names = objectMapper.readValue("{\"firstName\":\"John\",\"lastName\":\"Doe\"}", Names.class);

		assertThat(names.getFirstName()).isEqualTo("John");
		assertThat(names.getLastName()).isEqualTo("Doe");
	}

	@Test
	void testDelegatingDeserialization() throws JsonProcessingException{
		/*
		* Deserialization fails with
		* com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Invalid definition for property 'fullName'
		* (of type `com.example.NamesTest$Names`): Could not find creator property with name 'fullName'
		* (known Creator properties: [firstName, lastName])
		*/
		Names names = objectMapper.readValue("\"John Doe\"", Names.class);

		assertThat(names.getFirstName()).isEqualTo("John");
		assertThat(names.getLastName()).isEqualTo("Doe");
	}

	/*
	* Tests pass when following annotation is added
	* @JsonIgnoreProperties("fullName")
	*/
	public static class Names {

		private final String firstName;

		private final String lastName;

		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		public Names(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
		public static Names fromFullName(String fullName) {
			String[] names = fullName.split("\\s+", 2);
			return new Names(names[0], names[1]);
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
	}
}
