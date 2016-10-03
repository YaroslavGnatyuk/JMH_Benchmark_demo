/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.stream.IntStream;

@State(Scope.Thread)
public class MyBenchmark {
    private List<Integer> arrayListOfNumbers;
    private List<Integer> linkedListOfNumbers;

    @Setup
       public void init() {
           arrayListOfNumbers= new ArrayList<>();
           addNumbers(arrayListOfNumbers);

           linkedListOfNumbers = new LinkedList<>();
           addNumbers(linkedListOfNumbers);
       }

       private void addNumbers(List<Integer> container) {
           IntStream.range(0, 1_000_000)
                   .forEach(container::add);
       }

        @Benchmark
        public int slowSumOfSquares() {
            return linkedListOfNumbers.parallelStream()
                                      .map(x -> x * x)
                                      .reduce(0, (acc, x) -> acc + x);
        }

        @Benchmark
        public int serialSlowSumOfSquares() {
            return linkedListOfNumbers.stream()
                                      .map(x -> x * x)
                                      .reduce(0, (acc, x) -> acc + x);
        }

        @Benchmark
        public int intermediateSumOfSquares() {
            return arrayListOfNumbers.parallelStream()
                                     .map(x -> x * x)
                                     .reduce(0, (acc, x) -> acc + x);
        }

        @Benchmark
        public int serialIntermediateSumOfSquares() {
            return arrayListOfNumbers.stream()
                                     .map(x -> x * x)
                                     .reduce(0, (acc, x) -> acc + x);
        }

        @Benchmark
        public int fastSumOfSquares() {
            return arrayListOfNumbers.parallelStream()
                                     .mapToInt(x -> x * x)
                                     .sum();
        }

        @Benchmark
        public int serialFastSumOfSquares() {
            return arrayListOfNumbers.stream()
                               .mapToInt(x -> x * x)
                               .sum();
        }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName()).threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .warmupIterations(5)
                .shouldDoGC(true)
                .jvmArgs("-server")
                .build();
        new Runner(options).run();
    }
}
