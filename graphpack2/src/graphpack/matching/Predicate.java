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

import java.util.Map;

/**
 * Represents a check which return a boolean result
 * @author amitport
 */
public abstract class Predicate {
	
	public abstract <T extends Object> boolean evaluate(Map<String, T> env);
	
	/*** static ***/
	
	public static class Not extends Predicate {
		Predicate inner;

		public Not(Predicate inner) {
			this.inner = inner;
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
				
			return !inner.evaluate(env);
		}

		@Override
		public String toString() {
			return "!" + inner;
		}
	}


	public abstract static class BinaryCombinator<T> extends Predicate {
		T lhs,rhs;
		String op;
		
		BinaryCombinator(T lhs, T rhs, String op){
			this.lhs = lhs;
			this.rhs = rhs;
			this.op = op;
		}
		
		@Override
		public String toString() {
			return "(" + lhs + " " + op + " " + rhs + ")";
		}
	}

	public static class And extends BinaryCombinator<Predicate> {
		public And(Predicate lhs, Predicate rhs) {
			super(lhs,rhs,"and");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return lhs.evaluate(env) && rhs.evaluate(env);
		}
	}

	public static class Or extends BinaryCombinator<Predicate> {
		public Or(Predicate lhs, Predicate rhs) {
			super(lhs,rhs,"or");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return lhs.evaluate(env) || rhs.evaluate(env);
		}
	}

	public static class Equals extends BinaryCombinator<Value> {

		public Equals(Value lhs, Value rhs) {
			super(lhs,rhs,"==");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			Object ol = lhs.get(env);
			Object or = rhs.get(env);
			if (ol==null) return or==null;
			if (ol instanceof Number && or instanceof Number) {
				return Double.compare(((Number) ol).doubleValue(),
									((Number) or).doubleValue()) == 0;
			} else {
				return ol.equals(or);
			}
		}
	}

	abstract public static class Comparison extends BinaryCombinator<Value> {

		public Comparison(Value lhs, Value rhs, String op) {
			super(lhs,rhs,op);
		}

		public int compare(Object lhs, Object rhs) {
			if (lhs instanceof Number && rhs instanceof Number) {
				Double ld = ((Number) lhs).doubleValue();
				Double rd = ((Number) rhs).doubleValue();
				return Double.compare(ld, rd);
			} else {
				throw new RuntimeException("Can't Compare: " + lhs + " with " + rhs);
			}
		}

		abstract public boolean isInCorrectOrder(int compareResult);

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return isInCorrectOrder(compare(lhs.get(env), rhs.get(env)));
		}

		public static class GreaterThan extends Comparison {

			public GreaterThan(Value lhs, Value rhs) {
				super(lhs,rhs,">");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult > 0;
			}
		}

		public static class GreaterOrEqualThan extends Comparison {

			public GreaterOrEqualThan(Value lhs, Value rhs) {
				super(lhs,rhs,">=");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult >= 0;
			}
		}

		public static class LesserThan extends Comparison {
			
			public LesserThan(Value lhs, Value rhs) {
				super(lhs,rhs,"<");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult < 0;
			}
		}

		public static class LesserOrEqualThan extends Comparison {

			public LesserOrEqualThan(Value lhs, Value rhs) {
				super(lhs,rhs,"<=");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult <= 0;
			}
		}
	}
}
