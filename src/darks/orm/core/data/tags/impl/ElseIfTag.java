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

package darks.orm.core.data.tags.impl;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import darks.orm.core.data.tags.AbstractTag;
import darks.orm.core.data.tags.express.ExpressParam;
import darks.orm.core.data.tags.express.RPNCompute;
import darks.orm.core.data.tags.express.RPNParser;
import darks.orm.core.data.xml.InterfaceMethodData;

public class ElseIfTag extends AbstractTag
{

	String condition;
	
	LinkedList<String> conditionList;
	
	static RPNCompute compute = new RPNCompute();
	
	public ElseIfTag()
	{
	}
	
	public ElseIfTag(AbstractTag prevTag)
	{
		super(prevTag);
	}

	@Override
	public boolean parseElement(Element el) throws Exception
	{
		condition = el.attributeValue("test").trim();
		boolean suc = !"".equals(condition);
		if (suc)
		{
			RPNParser parser = new RPNParser();
			conditionList = parser.parse(condition);
		}
		return suc && conditionList != null;
	}
	
	@Override
	public Object computeSql(StringBuilder sqlBuf, List<Object> params, InterfaceMethodData data, Object prevValue) throws Exception
	{
		if (prevValue != null && (prevValue instanceof Boolean))
		{
			if ((Boolean)prevValue)
			{
				return prevValue;
			}
		}
		ExpressParam expParam = new ExpressParam(params, data);
		boolean ret = (Boolean)compute.compute(conditionList, expParam);
		if (ret)
		{
			super.computeSql(sqlBuf, params, data, prevValue);
		}
		return ret;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	@Override
	public String toString()
	{
		return "IfTag [condition=" + condition + ", childrenTags=" + childrenTags + "]";
	}

	
	
}
