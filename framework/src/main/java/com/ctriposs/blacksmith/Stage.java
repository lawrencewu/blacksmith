package com.ctriposs.blacksmith;

/**
 * A stage is a step in the benchmark process. E.g. of stages are starting cache wrapper, warmup, run actual test etc.
 *
 * @author bulldog
 */
public interface Stage extends Cloneable{
   Stage clone();
}
