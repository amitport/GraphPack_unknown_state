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

import graphpack.parsing.java.IParser;
import graphpack.remote.IConnectionManager;
import graphpack.remote.RemoteNodeLocator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Singleton;

public class GraphPackClient extends AbstractModule {
	Class<? extends IConnectionManager> connectionManagerClass;
	Class<? extends IParser> parserClass;
	IConnectionManager connectionManager;
	
	public GraphPackClient(Class<? extends IConnectionManager> connectionMangerClass,
			Class<? extends IParser> parserClass) {
		this.connectionManagerClass = connectionMangerClass;
		this.parserClass = parserClass;
	}
	
	protected void init(){
		connectionManager = Guice.createInjector(this).getInstance(IConnectionManager.class);
	}
	
	@Override 
	protected void configure() {
		bind(IConnectionManager.class).to(connectionManagerClass).in(Singleton.class);
		bind(INodeLocator.class).to(RemoteNodeLocator.class).in(Singleton.class);
		bind(IParser.class).to(parserClass).in(Singleton.class);
	}
	
	public Connector connectAs(String sourceService, String sourceClient){
		return new Connector(sourceService,sourceClient);
	}
	
	public class Connector {
		String sourceService, sourceClient;
		public Connector(String sourceService, String sourceClient) {
			this.sourceService = sourceService;
			this.sourceClient = sourceClient;
		}
		
		public IService to(String targetService){
			return connectionManager.connect(sourceService, sourceClient, targetService);
		}
	}
}
