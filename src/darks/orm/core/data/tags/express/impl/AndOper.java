package darks.orm.core.data.tags.express.impl;

/**
 * �߼�������
 * @author lihua.llh
 *
 */
public class AndOper extends LogicOper
{

	public AndOper()
	{
		super("AND");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object compute(Boolean src, Boolean dest)
	{
		return src && dest;
	}
}
