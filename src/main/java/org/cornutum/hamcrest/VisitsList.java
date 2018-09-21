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

import java.util.Iterator;
import java.util.function.Function;

/**
 * Matches an Iterator that visits a specified sequence of members (in order).
 * <P/>
 * Optionally, given a "member Matcher supplier" function, applies additional detailed match conditions for
 * sequence members.  First, for each member of the expected sequence, a member-specific Matcher is derived by the
 * given supplier function.  Then each member of a matched sequence must satisfy the member-specific
 * Matcher returned for its counterpart in the given expected sequence.
 */
public class VisitsList<T> extends BaseMatcher<Iterator<T>>
  {
  private final ListsMembers<T> listsMembers;
  
  /**
   * Builds and supplies a {@link VisitsList} matcher for a specified source Iterator.
   */
  public static class Supplier<T> implements Function<Iterator<T>,Matcher<Iterator<T>>>
    {
    private Function<T,Matcher<T>> memberMatcherSupplier;
    
    /**
     * Creates a new VisitsListSupplier that supplies a {@link VisitsList} matcher using
     * the given member Matcher supplier.
     */
    public Supplier( Function<T,Matcher<T>> memberMatcherSupplier)
      {
      this.memberMatcherSupplier = memberMatcherSupplier;
      }

    /**
     * Returns the {@link VisitsList} matcher supplied for the given source Iterator.
     */
    public Matcher<Iterator<T>> apply( Iterator<T> source)
      {
      return new VisitsList<T>( source, memberMatcherSupplier);
      }
    }
 
  /**
   * Creates a new VisitsList instance.
   */
  public VisitsList( Iterator<T> expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new VisitsList instance that adds an additional match condition: each member of a
   * matched sequence must satisfy the Matcher returned by the given supplier for its 
   * counterpart in the given expected sequence.
   */
  public VisitsList( Iterator<T> expected, Function<T,Matcher<T>> memberMatcherSupplier)
    {
    Iterable<T> members = expected == null? null : () -> expected;
    listsMembers = new ListsMembers<T>( members, memberMatcherSupplier);
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
  }
