//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.CompositeUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;

import org.hamcrest.Matcher;

/**
 * Defines methods to create composite matchers.
 */
public final class Composites
  {
  /**
   * Creates a new Composites instance.
   */
  private Composites()
    {
    // Static methods only
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Iterable<? extends T> expected)
    {
    return new ContainsMembers<T>( expected);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterable<? extends T> expected)
    {
    return new ContainsMembers<T>( expected, memberMatcherSupplier);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> containsMembers( T... expected)
    {
    return containsMembers( Arrays.asList( expected));
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, T... expected)
    {
    return containsMembers( memberMatcherSupplier, Arrays.asList( expected));
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Iterator<T> expected)
    {
    Iterable<T> iterable = () -> expected;
    return containsMembers( iterable);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterator<T> expected)
    {
    Iterable<T> iterable = () -> expected;
    return containsMembers( memberMatcherSupplier, iterable);
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order.
   */
  @SafeVarargs
  public static <T> Matcher<T[]> containsElements( T... expected)
    {
    return new ContainsElements<T>( expected);
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order, with
   * an additional match condition: each element of a matched array must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected array.
   */
  @SafeVarargs
  public static <T> Matcher<T[]> containsElements( Function<T,Matcher<T>> elementMatcherSupplier, T... expected)
    {
    return new ContainsElements<T>( expected, elementMatcherSupplier);
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order.
   */
  public static <T> Matcher<T[]> containsElements( Iterable<? extends T> expected)
    {
    return new ContainsElements<T>( toArray( expected));
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order, with
   * an additional match condition: each element of a matched array must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
   */
  public static <T> Matcher<T[]> containsElements( Function<T,Matcher<T>> elementMatcherSupplier, Iterable<? extends T> expected)
    {
    return new ContainsElements<T>( toArray( expected), elementMatcherSupplier);
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order.
   */
  public static <T> Matcher<T[]> containsElements( Iterator<T> expected)
    {
    return new ContainsElements<T>( toArray( () -> expected));
    }

  /**
   * Returns a Matcher for an array containing the given collection of elements in any order, with
   * an additional match condition: each element of a matched array must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
   */
  public static <T> Matcher<T[]> containsElements( Function<T,Matcher<T>> elementMatcherSupplier, Iterator<T> expected)
    {
    return new ContainsElements<T>( toArray( () -> expected), elementMatcherSupplier);
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order.
   */
  @SafeVarargs
  public static <T> Matcher<Iterator<T>> visitsMembers( T... expected)
    {
    return new VisitsMembers<T>( Arrays.asList( expected).iterator());
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order, with
   * an additional match condition: each member of a matched collection must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
   */
  @SafeVarargs
  public static <T> Matcher<Iterator<T>> visitsMembers( Function<T,Matcher<T>> memberMatcherSupplier, T... expected)
    {
    return new VisitsMembers<T>( Arrays.asList( expected).iterator(), memberMatcherSupplier);
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order.
   */
  public static <T> Matcher<Iterator<T>> visitsMembers( Iterable<? extends T> expected)
    {
    List<T> members = streamFor( expected).collect( toList());
    return new VisitsMembers<T>( members.iterator());
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order, with
   * an additional match condition: each member of a matched collection must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
   */
  public static <T> Matcher<Iterator<T>> visitsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterable<? extends T> expected)
    {
    List<T> members = streamFor( expected).collect( toList());
    return new VisitsMembers<T>( members.iterator(), memberMatcherSupplier);
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order.
   */
  public static <T> Matcher<Iterator<T>> visitsMembers( Iterator<T> expected)
    {
    return new VisitsMembers<T>( expected);
    }

  /**
   * Returns a Matcher for an Iterator that visits the given collection of members in any order, with
   * an additional match condition: each member of a matched collection must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
   */
  public static <T> Matcher<Iterator<T>> visitsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterator<T> expected)
    {
    return new VisitsMembers<T>( expected, memberMatcherSupplier);
    }

  /**
   * Returns a Matcher that compares values of the given function, using a result Matcher returned by the given supplier.
   */
  public static <T,R> Matcher<T> matchesFunction( String functionName, Function<T,R> function, T source, Function<R,Matcher<R>> resultMatcherSupplier)
    {
    return new MatchesFunction<T,R>( functionName, function, source, resultMatcherSupplier);
    }

  /**
   * To create a more expressive reference, especially for a constructor expression, simply returns the given Matcher value.
   */
  public static <T> Matcher<T> matches( Matcher<T> matcherExpression)
    {
    return matcherExpression;
    }

  /**
   * Returns a new {@link MatchesFunction.Builder} for the given expected object.
   */
  public static <T,R> MatchesFunction.Builder<T,R> comparedTo( T expected)
    {
    return new MatchesFunction.Builder<>( expected);
    }
}
