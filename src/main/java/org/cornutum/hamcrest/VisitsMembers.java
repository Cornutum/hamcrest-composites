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
 * Matches an Iterator that visits a specified collection of members in any order.
 * <P/>
 * Optionally, given a "member Matcher supplier" function, applies additional detailed match conditions for
 * collection members.  First, for each member of the expected collection, a member-specific Matcher is derived by the
 * given supplier function.  Then each member of a matched collection must satisfy the member-specific
 * Matcher returned for its <CODE>equals</CODE>-matching counterpart in the given expected collection.
 */
public class VisitsMembers<T> extends BaseMatcher<Iterator<T>>
  {
  private final ContainsMembers<T> containsMembers;
  
  /**
   * Builds and supplies a {@link VisitsMembers} matcher for a specified source Iterator.
   */
  public static class Supplier<T> implements Function<Iterator<T>,Matcher<Iterator<T>>>
    {
    private Function<T,Matcher<T>> memberMatcherSupplier;
    
    /**
     * Creates a new VisitsMembersSupplier that supplies a {@link VisitsMembers} matcher using
     * the given member Matcher supplier.
     */
    public Supplier( Function<T,Matcher<T>> memberMatcherSupplier)
      {
      this.memberMatcherSupplier = memberMatcherSupplier;
      }

    /**
     * Returns the {@link VisitsMembers} matcher supplied for the given source Iterator.
     */
    public Matcher<Iterator<T>> apply( Iterator<T> source)
      {
      return new VisitsMembers<T>( source, memberMatcherSupplier);
      }
    }
 
  /**
   * Creates a new VisitsMembers instance.
   */
  public VisitsMembers( Iterator<T> expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new VisitsMembers instance that adds an additional match condition: each member of a
   * matched collection must satisfy the Matcher returned by the given supplier for its <CODE>equals</CODE>-matching 
   * counterpart in the given expected collection.
   */
  public VisitsMembers( Iterator<T> expected, Function<T,Matcher<T>> memberMatcherSupplier)
    {
    Iterable<T> members = expected == null? null : () -> expected;
    containsMembers = new ContainsMembers<T>( members, memberMatcherSupplier);
    }

  public boolean matches( Object actual)
    {
    return containsMembers.matches( actual);
    }

  public void describeTo( Description description)
    {
    containsMembers.describeTo( description);
    }

  public void describeMismatch( Object actual, Description description)
    {
    containsMembers.describeMismatch( actual, description);
    }
  }
