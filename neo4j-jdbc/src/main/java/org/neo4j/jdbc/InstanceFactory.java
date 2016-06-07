/*
 *
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
 * Created on 19/4/2016
 *
 */
package org.neo4j.jdbc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author AgileLARUS
 * @since 3.0.0
 */
public class InstanceFactory {

	public static <T extends Loggable> T debug(Class<T> cls, T obj, boolean log) {
		T ret = obj;
		if (log) {
			Constructor constructor = cls.getConstructors()[0];
			Class[] argTypes = constructor.getParameterTypes();
			Object[] args = new Object[argTypes.length];
			for (int i = 0; i < argTypes.length; i++) {
				// TODO: Make something more generic here specially for primitive type and array
				if (!argTypes[i].getName().startsWith("[")) {
					args[i] = null;
				} else {
					args[i] = new int[0];
				}
			}

			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(cls);
			enhancer.setCallback(new LoggerMethodInterceptor(obj));
			ret = (T) enhancer.create(argTypes, args);
		}

		return ret;
	}

	private static class LoggerMethodInterceptor implements MethodInterceptor {

		private Object underlying;

		public LoggerMethodInterceptor(Object obj) {
			this.underlying = obj;
		}

		@Override public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			StringBuffer sb = new StringBuffer().append(underlying.getClass().getName()).append(" - ").append(method.getName()).append("(");
			for (int i = 0; args != null && i < args.length; i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(args[i]);
			}
			sb.append(")");
			System.out.println(sb);
			Object ret = method.invoke(underlying, args);
			if (ret != null) {
				sb.append(" -> ");
				sb.append(ret);
			}
			System.out.println(sb);
			return ret;
		}
	}
}
