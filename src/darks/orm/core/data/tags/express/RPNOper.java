package darks.orm.core.data.tags.express;

import java.util.LinkedList;

/**
 * �沨��ʽ������ӿ�
 * @author lihua.llh
 *
 */
public interface RPNOper
{

    /**
     * ���������������������ջ��
     * @param stack ������ջ
     * @param param ����
     * @throws ExpressException
     */
	public void compute(LinkedList<Object> stack, Object param) throws ExpressException;
}
