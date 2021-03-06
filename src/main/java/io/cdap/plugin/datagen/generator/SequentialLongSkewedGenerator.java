/*
 * Copyright © 2020 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.datagen.generator;

import io.cdap.plugin.datagen.GeneratorContext;

/**
 * Generates longs in a regular sequence, but sometimes randomly generating a smaller subset of values.
 *
 * Skew percentage of the time, the number will be randomly chosen between the skew min and max.
 * The rest of the time, the number will be generated by adding a constant number to the current number.
 */
public class SequentialLongSkewedGenerator extends RandomGenerator<Long> {
  private final Config config;
  private final SequentialLongGenerator sequence;

  public SequentialLongSkewedGenerator(Config config) {
    this.config = config;
    this.sequence = new SequentialLongGenerator(config);
  }

  @Override
  public void initialize(GeneratorContext context) {
    super.initialize(context);
    sequence.initialize(context);
  }

  @Override
  public Long generate() {
    long val = sequence.generate();
    if (random.nextInt(100) < config.skewChance) {
      return config.skewMin + randomService.nextLong(config.skewMax - config.skewMin + 1);
    }
    return val;
  }

  /**
   * Config.
   */
  public static class Config extends SequentialLongGenerator.Config {
    private long skewMin;
    private long skewMax;
    private int skewChance;

    public Config() {
      this(1, 1, 0, 10, 10);
    }

    public Config(long start, long step, long skewMin, long skewMax, int skewChance) {
      super(start, step);
      this.skewMin = skewMin;
      this.skewMax = skewMax;
      this.skewChance = skewChance;
    }
  }
}
