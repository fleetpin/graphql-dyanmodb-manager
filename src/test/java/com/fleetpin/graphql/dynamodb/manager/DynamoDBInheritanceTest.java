/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.fleetpin.graphql.dynamodb.manager;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

public class DynamoDBInheritanceTest extends DynamoDBBase {

	@ParameterizedTest
	@EnumSource(DatabaseType.class)
	public void testSimplePutGetDelete(final DatabaseType dbType) throws InterruptedException, ExecutionException {
		final var db = createTestDatabase(dbType, "test");

		db.put(new NameTable("garry")).get();
		db.put(new AgeTable("19")).get();

		var entries = db.query(BaseTable.class).get();

		Assertions.assertEquals(2, entries.size());
		entries.sort(Comparator.comparing(t -> t.getClass().getSimpleName()));
		Assertions.assertEquals("19", ((AgeTable)entries.get(0)).getAge());
		Assertions.assertEquals("garry", ((NameTable)entries.get(1)).getName());
	}




	@TableName("base")
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({ 
		@Type(value = NameTable.class, name = "name"), 
		@Type(value = AgeTable.class, name = "age")
	})
	static abstract class BaseTable extends Table {
	}

	static class NameTable extends BaseTable {
		String name;

		public NameTable() {
		}
		public NameTable(String name) {
			this.name = name;
		}


		public String getName() {
			return name;
		}
	}
	static class AgeTable extends BaseTable {
		String name;

		public AgeTable() {
		}
		public AgeTable(String age) {
			this.name = age;
		}


		public String getAge() {
			return name;
		}
	}
}
