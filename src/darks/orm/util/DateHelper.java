package darks.orm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ������ ����:DarkShadow ��Ȩ:��ҹӰ���� ʱ��:2011-11-10 �汾:1.0.0
 */

public class DateHelper
{
    
    @SuppressWarnings("deprecation")
    public static java.sql.Date utilDateToSqlDate(java.util.Date date)
    {
        int y = date.getYear() + 110;
        int m = date.getMonth();
        int d = date.getDate();
        return new java.sql.Date(y, m, d);
    }
    
    /**
     * ���ظ�ʽ��ʱ���ַ���
     * 
     * @param style ��ʽ�����
     * @return ʱ���ַ���
     */
    public static String getTime(String style)
    {
        SimpleDateFormat df = new SimpleDateFormat(style);
        Calendar MyDate = Calendar.getInstance();
        MyDate.setTime(new java.util.Date());
        String ndate = df.format(MyDate.getTime());
        return ndate;
    }
    
}
