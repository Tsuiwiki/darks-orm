/**
 * 
 * Copyright 2014 The Darks ORM Project (Liu lihua)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package darks.orm.web.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SpringRequestHolder implements RequestHolder
{
    
    public static final String RequestKey = "request";
    
    public static final String SessionKey = "session";
    
    @Override
    public HttpServletRequest getRequest()
    {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return (HttpServletRequest)attributes.resolveReference(RequestKey);
    }
    
    @Override
    public HttpSession getSession()
    {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return (HttpSession)attributes.resolveReference(SessionKey);
    }
    
}