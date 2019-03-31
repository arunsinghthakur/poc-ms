package com.joker.servicesecond;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RefreshScope
@RestController
@RequestMapping("/api/servicesecond")
public class DemoResource {

	private final static Logger logger = LoggerFactory.getLogger(DemoResource.class);

	@Autowired
	private TaskExecutor threadPoolTaskExecutor;

	@Value("${service2: Default Second Service}")
	private String message;

	@GetMapping("/dummy")
	@HystrixCommand(fallbackMethod = "fallBack", commandKey = "joker", groupKey = "joker-group")
	public String getDummyMsg() throws Exception {
		logger.info("Inside service second.......");
		return message;
	}

	public String fallBack() {
		logger.info("This is fallback method.");
		return "This is dummy fallback message";
	}

	@GetMapping("/dummy/async")
	@Async("threadPoolTaskExecutor")
	public CompletableFuture<String> getDummyMsgAsync() throws Exception {
		logger.info("Inside service second async. Start.......");

		Runnable r = () -> {
			logger.info("Inside runnable task START---------");
			try {
				Thread.sleep(10000);
				logger.info(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info(Thread.currentThread().getName() + ". Async task complete........");
		};
		CompletableFuture.runAsync(r, threadPoolTaskExecutor);
		return CompletableFuture.completedFuture("Request is processing asyn by service second");

	}
}
