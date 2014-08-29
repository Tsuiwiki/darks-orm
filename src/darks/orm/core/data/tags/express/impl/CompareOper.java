package darks.orm.core.data.tags.express.impl;

import java.util.LinkedList;

import darks.orm.core.data.tags.express.ExpressException;
import darks.orm.core.data.tags.express.ExpressParam;
import darks.orm.core.data.tags.express.RPNOper;

/**
 * �߼����������
 * @author lihua.llh
 *
 */
public abstract class CompareOper implements RPNOper
{
	
	private String name;

	public CompareOper(String name)
	{
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void compute(LinkedList<Object> stack, Object param) throws ExpressException
	{
		if (stack.size() < 2)
		{
			throw new ExpressException(name + " oper Need two values. Which only has " + stack);
		}
		Object val2 = (Object) stack.pop();
		Object val1 = (Object) stack.pop();
		Object result = null;
		ExpressParam expParam = (ExpressParam) param;
		if ((val1 instanceof String))
		{
			val1 = expParam.getParam((String) val1);
		}
		if ((val2 instanceof String))
		{
			val2 = expParam.getParam((String) val2);
		}
		if (((val1 instanceof String) || val1 == null) 
					&& ((val2 instanceof String) || val2 == null))
		{
			result = compute((String) val1, (String) val2);
			stack.push(result);
		}
		else if ((val1 instanceof Integer) && (val2 instanceof Integer))
		{
			result = compute((Integer) val1, (Integer) val2);
			stack.push(result);
		}
		else if ((val1 instanceof Long) && (val2 instanceof Long))
		{
			result = compute((Long) val1, (Long) val2);
			stack.push(result);
		}
		else
		{
			throw new ExpressException("Invalid data type when compute RPN express." 
					+ val1 + " " + name + " " + val2);
		}
	}

	/**
	 * ����
	 * @param val1 Դ������
	 * @param val2 Ŀ�������
	 * @return ������
	 */
	public abstract Boolean compute(String src, String desc);

	/**
	 * ����
	 * @param val1 Դ������
	 * @param val2 Ŀ�������
	 * @return ������
	 */
	public abstract Boolean compute(int src, int desc);

	/**
	 * ����
	 * @param val1 Դ������
	 * @param val2 Ŀ�������
	 * @return ������
	 */
	public abstract Boolean compute(long src, long desc);
}
