/**
 * Copyright (c) 2016 LARUS Business Automation [http://www.larus-ba.it]
 * <p>
 * This file is part of the "LARUS Integration Framework for Neo4j".
 * <p>
 * The "LARUS Integration Framework for Neo4j" is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created on 31/01/17
 */
package org.neo4j.driver.internal;

import org.neo4j.driver.internal.spi.Connection;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.neo4j.driver.v1.Values.ofValue;
import static org.neo4j.driver.v1.Values.values;

public class DummyStatementResult {

	/**
	 * Hacking way to get a {@link InternalStatementResult}
	 *
	 * @param keys
	 * @param data
	 * @return
	 */
	public static StatementResult build(String[] keys, List<Object[]> data) {

		try {
			Connection connection = mock(Connection.class);
			String statement = "<unknown>";

			InternalStatementResult cursor = new InternalStatementResult(connection, null, new Statement(statement));
			cursor.runResponseCollector().keys(keys);
			cursor.runResponseCollector().done();

			for (Object[] values : data) {
				cursor.pullAllResponseCollector().record(values(values));
			}
			cursor.pullAllResponseCollector().done();

			connection.run(statement, Values.EmptyMap.asMap(ofValue()), cursor.runResponseCollector());
			connection.pullAll(cursor.pullAllResponseCollector());
			connection.flush();

			return cursor;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
