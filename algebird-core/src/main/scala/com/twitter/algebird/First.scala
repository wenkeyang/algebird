/*
Copyright 2016 Twitter, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.twitter.algebird

/**
 * First tracks the "most recent" item by the order in which items
 * are seen.
 */
case class First[@specialized(Int, Long, Float, Double) +T](get: T) {
  def +[U >: T](r: First[U]): First[U] = this
}

object First extends FirstInstances {
  def aggregator[T]: FirstAggregator[T] = FirstAggregator()
}

private[algebird] sealed abstract class FirstInstances {
  def firstSemigroup[T] = new Semigroup[T] {
    def plus(l: T, r: T): T = l

    override def sumOption(iter: TraversableOnce[T]): Option[T] =
      if (iter.isEmpty) None else Some(iter.toIterator.next)
  }

  implicit def semigroup[T]: Semigroup[First[T]] = firstSemigroup[First[T]]
}

case class FirstAggregator[T]() extends Aggregator[T, T, T] {
  def prepare(v: T) = v

  val semigroup: Semigroup[T] = First.firstSemigroup[T]

  def present(v: T) = v
}
