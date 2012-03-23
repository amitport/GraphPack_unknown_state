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
package graphpack.local;

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.Extensions;
import graphpack.INode;
import graphpack.NodeLocation;
import graphpack.local.persistence.IEdgeStore;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.parsing.java.IParser;
import graphpack.taskprocessing.ITask;
import graphpack.taskprocessing.ITaskManager;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javax.annotation.Nullable;

public class Node implements INode {
	Extensions extensions;
	String serviceName, clientName, nodeName;
	IEdgeStore edgeStore;
	IParser parser;
	NodeLocation location;
	ITaskManager taskManager;
	@Inject
	public Node(@Nullable Extensions extensions, @Assisted("serviceName") String serviceName, @Assisted("clientName") String clientName, @Assisted("nodeName") String nodeName, IEdgeStore edgeStore, IParser parser,ITaskManager taskManager){
		this.extensions = extensions;
		this.serviceName = serviceName;
		this.clientName = clientName;
		this.nodeName = nodeName;
		this.edgeStore = edgeStore;
		this.parser = parser;
		this.location = new NodeLocation(serviceName,clientName,nodeName);
		this.taskManager = taskManager;
	}
	
	@Override
	public void addTask(String taskName, Class<? extends ITask> task){
		taskManager.addTask(clientName, nodeName, taskName, this, extensions, task);
	}
	@Override
	public void callTask(String taskName, Object... params){
		taskManager.callTask(clientName, nodeName, taskName, params);
	}
	
	@Override
	public NodeLocation location(){return location;}
	
	@Override
	public String test() {
		return "node-test";
	}
	
	@Override
	public String toString() {
		return serviceName+"."+clientName+"."+nodeName;
	}

	@Override
	public List<Edge> getOutgoingEdges() {
		return edgeStore.getOutgoingEdges();
	}

	@Override
	public void addOutgoingEdge(NodeLocation target, Payload payload) {
		edgeStore.addOutgoingEdge(serviceName,clientName,nodeName,target,payload);
	}

	@Override
	public ResultSet traverse(Matcher matcher) {
		if (matcher == null) return null;
		System.out.println("yey" + matcher);
		ResultSet $ = new ResultSet();
		for (Edge e : getOutgoingEdges()) {
			Matcher newMatcher = matcher.cont(e.serializableEdge);
			if (newMatcher.canTake()) {
				ResultSet rs = newMatcher.take();
				$ = ResultSet.union($,rs);
			}
			if (newMatcher.canCont()) {
				$ = ResultSet.union($,e.target.traverse(newMatcher));
			}
		}
		return $;
	}

	@Override
	public ResultSet traverse(String path, Object... params) {
		return traverse(parser.parsePath(path, params));
	}
	
//	public Object writeReplace() throws ObjectStreamException 
//	{return location();}

	//TODO!!!
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((location == null) ? 0 : location.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (obj instanceof INode){
//			INode other = (INode) obj;
//			if (location == null) {
//				if (other.location() != null)
//					return false;
//			} else if (!location.equals(other.location()))
//				return false;
//			return true;
//		}
//		if (obj instanceof NodeLocation){
//			NodeLocation other = (NodeLocation) obj;
//			if (location == null) {
//				if (other != null)
//					return false;
//			} else if (!location.equals(other))
//				return false;
//			return true;
//		}
//		return false;
//	}
	
//	public static class SerializedNode implements Serializable {
//		private static final long serialVersionUID = -4212331017263734613L;
//		NodeLocation location;
//		public SerializedNode(NodeLocation location){
//			this.location = location;
//		}
//		
//		Object readResolve() throws ObjectStreamException
//		{return null;}
//	}
}
