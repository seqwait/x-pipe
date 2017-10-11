package com.ctrip.xpipe.redis.core.protocal.cmd.manual;

import com.ctrip.xpipe.api.pool.SimpleObjectPool;
import com.ctrip.xpipe.netty.commands.NettyClient;
import com.ctrip.xpipe.redis.core.protocal.cmd.AbstractSentinelCommand.SentinelAdd;
import com.ctrip.xpipe.redis.core.protocal.cmd.AbstractSentinelCommand.SentinelRemove;
import com.ctrip.xpipe.redis.core.protocal.cmd.AbstractSentinelCommand.Sentinels;
import com.ctrip.xpipe.redis.core.protocal.pojo.Sentinel;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author wenchao.meng
 *
 * Dec 9, 2016
 */
public class SentinelCommandTest extends AbstractCommandTest{
	
	private String host = "localhost";
	
	private int port = 5002;
	
	private String masterName = "mymaster";
	
	private SimpleObjectPool<NettyClient> clientPool;
	
	@Before
	public void beforeSentinelCommandTest() throws Exception{
		clientPool = getXpipeNettyClientKeyedObjectPool().getKeyPool(new InetSocketAddress(host, port)); 
	}

	@Test
	public void testSentinels() throws InterruptedException, ExecutionException, Exception{
		
		List<Sentinel> sentinels = getSentinels();
		logger.info("{}", sentinels);
	}
	
	private List<Sentinel> getSentinels() throws InterruptedException, ExecutionException {
		
		return new Sentinels(clientPool, masterName, scheduled).execute().get();
	}

	@Test
	public void testRemove() throws InterruptedException, ExecutionException{
		
		try{
			new SentinelRemove(clientPool, masterName, scheduled).execute().get();
		}catch(Exception e){
			logger.error("[testRemove]", e);
		}
		
		String addResult = new SentinelAdd(clientPool, masterName, "127.0.0.1", 6379, 3, scheduled).execute().get();
		logger.info("{}", addResult);
		
		String removeResult = new SentinelRemove(clientPool, masterName, scheduled).execute().get();
		logger.info("{}", removeResult);

		new SentinelAdd(clientPool, masterName, "127.0.0.1", 6379, 3, scheduled).execute().get();
		logger.info("{}", addResult);

		logger.info("{}", getSentinels());
	}
	
	@Test
	public void test(){
		
		String result = String.format("%s %s", 1);
		logger.info("{}", result);
	}
}
