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
package graphpack;


import graphpack.Edge.Payload;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.taskprocessing.ITask;

//import java.io.Serializable;
import java.util.List;

public interface INode/* extends Serializable */{
	String test();
	List<Edge> getOutgoingEdges();
	void addOutgoingEdge(NodeLocation target, Payload payload);
	ResultSet traverse(String path, Object... params);
	ResultSet traverse(Matcher matcher);
	NodeLocation location();
	void addTask(String taskName, Class<? extends ITask> task);
	void callTask(String taskName, Object... params);
}
