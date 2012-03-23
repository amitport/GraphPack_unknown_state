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

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class ResultSet implements Serializable {
	private static final long serialVersionUID = -6513155130201864844L;
	
	final private Multiset<Result> results;
	
	public ResultSet(){
		results = HashMultiset.create();
	}
	
	private ResultSet(Multiset<Result> results){
		this.results = results;
	}
	
	public boolean isEmpty() {
		return results.isEmpty();
	}
	
	public void add(Result r) {
		results.add(r);
	}
	
	public boolean contains(Result r) {
		return results.contains(r);
	}
	
	public Set<Result> elementSet() {
		return results.elementSet();
	}
	
	@Override
	public String toString(){
		return results.toString();
	}
	/*** static ***/

	/**
	 * Product joins two result sets, entities with the same name are merged
	 * if either one is unassigned (if only one of them is assigned we take it's value)
	 * or if they point to the same value (otherwise we disregard the result)
	 * @return the merged ResultSet
	 */
	public static ResultSet product(ResultSet rs1, ResultSet rs2){
		ResultSet $ = new ResultSet();
		for (Result res1 : rs1.results){
			for (Result res2 : rs2.results){
				Result r = Result.merge(res1, res2);
				if (r != null) $.results.add(r);
			}
		}
		return $;		
	}

	/**
	 * Union of all the results in both input result sets
	 * @return the unified result set
	 */
	public static ResultSet union(ResultSet rs1, ResultSet rs2){
		ResultSet $ = new ResultSet();
		$.results.addAll(rs1.results);
		$.results.addAll(rs2.results);
		return $;
	}
	
	public static ResultSet Empty() {
		return new ResultSet(new ImmutableMultiset.Builder<Result>().build());
	}
	
	public static ResultSet Epsilon() {
		ResultSet $ = new ResultSet();
		$.add(new Result());
		return $;
	}
}
