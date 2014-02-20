/*
Copyright 2013 Cory Dissinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

package com.cd.reddit;

/**
 * Does this class need anything else? Could exception handling be better?
 * 
 * @author <a href="https://github.com/corydissinger">Cory Dissinger</a>
 */
public class RedditException extends Exception{
	private static final long serialVersionUID = 6159115388677496357L;

	public RedditException(String message) {
		super(message);
	}	
	
	public RedditException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
