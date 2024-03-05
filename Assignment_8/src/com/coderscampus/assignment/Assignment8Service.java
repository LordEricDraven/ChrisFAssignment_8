package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Assignment8Service {

	private final Assignment8 assignment = new Assignment8();
	private final List<Integer> numbersList = new ArrayList<>();
	
	private final ExecutorService singleThread = Executors.newSingleThreadExecutor();
	private final ExecutorService cachedThread = Executors.newCachedThreadPool();
	
	public void getAllNumbers() {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			CompletableFuture<Void> future = CompletableFuture.supplyAsync(assignment::getNumbers, cachedThread)
							 .thenAcceptAsync(numbersList::addAll, singleThread);
			futures.add(future);
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
						 .thenRunAsync(this::CountAndPrint)
						 .join();
	}
	
	private void CountAndPrint() {
		System.out.println("All numbers fetched. Total: " + numbersList.size());
		Map<Integer, Long> occurences = CountOccurences(numbersList);
		occurences.forEach((number, count) -> {
			System.out.println("Number: " + number + ", Count: " + count);
		});
	}
	
	private Map<Integer, Long> CountOccurences(List<Integer> numbers){
		return numbers.stream()
					  .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
	}
}