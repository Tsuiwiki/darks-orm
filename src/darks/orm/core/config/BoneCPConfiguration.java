package darks.orm.core.config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class BoneCPConfiguration extends JdbcConfiguration
{
    
    private BoneCPConfig boneCPConfig = null;
    
    private BoneCPDataSource ds = null;
    
    private int idleConnectionTestPeriod = 60;
    
    private int idleMaxAge = 240;
    
    private int partitionCount = 3;
    
    private int maxConnectionsPerPartition = 30;
    
    private int minConnectionsPerPartition = 10;
    
    private int acquireIncrement = 5;
    
    private int releaseHelperThreads = 3;
    
    private int statementsCacheSize = 0;
    
    private int statementReleaseHelperThreads = 3;
    
    private volatile boolean isInited = false;
    
    private Lock lock = new ReentrantLock();
    
    public BoneCPConfiguration()
    {
        
    }
    
    public DataSource getDataSource()
        throws ClassNotFoundException
    {
        if (!isInited)
        {
            lock.lock();
            try
            {
                if (!isInited)
                {
                    Class.forName(getDriver());
                    boneCPConfig = new BoneCPConfig();
                    boneCPConfig.setJdbcUrl(getUrl());
                    boneCPConfig.setUsername(getUsername());
                    boneCPConfig.setPassword(getPassword());
                    // ����ÿ60�������ݿ��еĿ���������
                    boneCPConfig.setIdleConnectionTestPeriod(idleConnectionTestPeriod, TimeUnit.MINUTES);
                    // �������ӿ���ʱ��
                    boneCPConfig.setIdleMaxAge(idleMaxAge, TimeUnit.MINUTES);
                    boneCPConfig.setPartitionCount(partitionCount);
                    // ����ÿ�������е����������
                    boneCPConfig.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
                    // ����ÿ�������е���С������
                    boneCPConfig.setMinConnectionsPerPartition(minConnectionsPerPartition);
                    // �����ӳ��е����Ӻľ���ʱ�� BoneCPһ��ͬʱ��ȡ��������
                    boneCPConfig.setAcquireIncrement(acquireIncrement);
                    // �����ͷŴ���
                    boneCPConfig.setReleaseHelperThreads(releaseHelperThreads);
                    ds = new BoneCPDataSource(boneCPConfig);
                    isInited = true;
                }
            }
            finally
            {
                lock.unlock();
            }
        }
        return ds;
    }
    
    @Override
    public void destroy()
    {
        lock.lock();
        try
        {
            if (ds != null)
            {
                ds.close();
            }
            isInited = false;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public boolean isInited()
    {
        return isInited;
    }
    
    public BoneCPConfig getBoneCPConfig()
    {
        return boneCPConfig;
    }
    
    public void setBoneCPConfig(BoneCPConfig boneCPConfig)
    {
        this.boneCPConfig = boneCPConfig;
    }
    
    public int getIdleConnectionTestPeriod()
    {
        return idleConnectionTestPeriod;
    }
    
    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod)
    {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }
    
    public int getIdleMaxAge()
    {
        return idleMaxAge;
    }
    
    public void setIdleMaxAge(int idleMaxAge)
    {
        this.idleMaxAge = idleMaxAge;
    }
    
    public int getPartitionCount()
    {
        return partitionCount;
    }
    
    public void setPartitionCount(int partitionCount)
    {
        this.partitionCount = partitionCount;
    }
    
    public int getMaxConnectionsPerPartition()
    {
        return maxConnectionsPerPartition;
    }
    
    public void setMaxConnectionsPerPartition(int maxConnectionsPerPartition)
    {
        this.maxConnectionsPerPartition = maxConnectionsPerPartition;
    }
    
    public int getMinConnectionsPerPartition()
    {
        return minConnectionsPerPartition;
    }
    
    public void setMinConnectionsPerPartition(int minConnectionsPerPartition)
    {
        this.minConnectionsPerPartition = minConnectionsPerPartition;
    }
    
    public int getAcquireIncrement()
    {
        return acquireIncrement;
    }
    
    public void setAcquireIncrement(int acquireIncrement)
    {
        this.acquireIncrement = acquireIncrement;
    }
    
    public int getReleaseHelperThreads()
    {
        return releaseHelperThreads;
    }
    
    public void setReleaseHelperThreads(int releaseHelperThreads)
    {
        this.releaseHelperThreads = releaseHelperThreads;
    }
    
    public int getStatementsCacheSize()
    {
        return statementsCacheSize;
    }
    
    public void setStatementsCacheSize(int statementsCacheSize)
    {
        this.statementsCacheSize = statementsCacheSize;
    }
    
    public int getStatementReleaseHelperThreads()
    {
        return statementReleaseHelperThreads;
    }
    
    public void setStatementReleaseHelperThreads(int statementReleaseHelperThreads)
    {
        this.statementReleaseHelperThreads = statementReleaseHelperThreads;
    }
    
}
