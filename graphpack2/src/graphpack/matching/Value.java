/*******************************************************************************
 * Copyright 2012 Amit Portnoy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package graphpack.matching;

import graphpack.SerializableEdge;

import java.util.Map;

public abstract class Value {
	
	public abstract <T extends Object> Object get(Map<String,T> env);
	
	/*** static ***/
	
	/**
	 * Literal values always return the same value (and ignore the environment) 
	 * @author amitport
	 */
	public static class Literal extends Value {
		Object value;

		public Literal(Object value){
			this.value = value;
		}
		@Override
		public <T extends Object> Object get(Map<String, T> env) {
			return value;
		}
		
		@Override
		public String toString() {
			return "" + value;
		}
	}
	
	/**
	 * Property values expect to find a certain entity in the environment
	 * which have a field of a certain property key. this field is read using reflection
	 * @author amitport
	 */
	public static class Property extends Value {
		private Object entity;
		private String[] propChain;
		public Property(Object entity, String[] propChain) {
			this.entity = entity;
			this.propChain = propChain;
		}

		@Override
		public <T extends Object> Object get(Map<String, T> env) {
			Object $ = null;
			try {
				if (entity instanceof String){
					$ = env.get(entity);
		//			System.out.println(entity + " ==" +$);
				} else {
					$ = entity;
				}
				if ($ instanceof SerializableEdge){
					$ = ((SerializableEdge)$).payload;
				}
				//TODO $ instanceof NodeLocation --> get node properties
				for (int i = 0; i < propChain.length; i++) {
					try {
					$ = $.getClass().getField(propChain[i]).get($);
					} catch (NoSuchFieldException e) {
						if (i==propChain.length-1)
							return null;
						else throw new RuntimeException(e.toString());
					} 
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return $;
		}

		@Override
		public String toString() {
			StringBuffer $ = new StringBuffer();
			$.append(entity);
			for (int i = 0; i < propChain.length; i++) {
				$.append("." + propChain[i]);
			}
			return $.toString();
		}
	}
}
