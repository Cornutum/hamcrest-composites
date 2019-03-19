//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.function.Function;


/**
 * Matches an array containing a specified sequence of elements (in order).
 * <P/>
 * Optionally, given a "element Matcher supplier" function, applies additional detailed match conditions for
 * array elements.  First, for each element of the expected array, an element-specific Matcher is derived by the
 * given supplier function.  Then each element of a matched array must satisfy the element-specific
 * Matcher returned for its counterpart in the given expected array.
 */
public class ListsElements<T> extends BaseMatcher<T[]>
  {
  private final ListsMembers<T> listsMembers;
  
  /**
   * Builds and supplies a {@link ListsElements} matcher for a specified source array.
   */
  public static class Supplier<T> implements Function<T[],Matcher<T[]>>
    {
    private Function<T,Matcher<T>> elementMatcherSupplier;
    
    /**
     * Creates a new ListsElementsSupplier that supplies a {@link ListsElements} matcher using
     * the given element Matcher supplier.
     */
    public Supplier( Function<T,Matcher<T>> elementMatcherSupplier)
      {
      this.elementMatcherSupplier = elementMatcherSupplier;
      }

    /**
     * Returns the {@link ListsElements} matcher supplied for the given source array.
     */
    public Matcher<T[]> apply( T[] source)
      {
      return new ListsElements<T>( source, elementMatcherSupplier);
      }
    }
 
  /**
   * Creates a new ListsElements instance.
   */
  public ListsElements( T[] expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new ListsElements instance that adds an additional match condition: each element of a
   * matched array must satisfy the Matcher returned by the given supplier for its 
   * counterpart in the given expected array.
   */
  public ListsElements( T[] expected, Function<T,Matcher<T>> elementMatcherSupplier)
    {
    Iterable<T> members = expected == null? null : Arrays.asList( expected);
    listsMembers = new ListsMembers<T>( members, elementMatcherSupplier);
    }

  public boolean matches( Object actual)
    {
    return listsMembers.matches( actual);
    }

  public void describeTo( Description description)
    {
    listsMembers.describeTo( description);
    }

  public void describeMismatch( Object actual, Description description)
    {
    listsMembers.describeMismatch( actual, description);
    }

  public String toString()
    {
    return String.format( "%s[]", getClass().getSimpleName());
    }
  }
