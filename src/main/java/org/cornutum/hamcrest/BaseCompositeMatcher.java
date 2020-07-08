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
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for a Matcher that applies a sequence of Matchers to a single object.
 * <P/>
 * To define a "deep match" between two instances of type <CODE>T</CODE>, create a subclass that
 * uses {@link #expectThat(String,Function,Function) expectThat()} to add
 * Matchers that compare instances property-by-property.
 */
public abstract class BaseCompositeMatcher<T> extends BaseMatcher<T>
  {
  private final T expected;
  private CompositeMatcher compositeMatcher;
  private List<Matcher<? super T>> matchers;

  /**
   * Applies a sequence of Matchers to a single object.
   */
  private class CompositeMatcher
    {
    private final Object matched;
    private Matcher<? super T> mismatch;
    
    /**
     * Creates a new CompositeMatcher instance.
     */
    @SuppressWarnings("unchecked")
    public CompositeMatcher( Object object)
      {
      matched = object;

      if( (expected == null) != (object == null))
        {
        mismatch =
          expected == null
          ? new IsNull<T>()
          : new IsNot<T>( new IsNull<T>());
        }
      else if( (mismatch = getTypeMismatch( object)) == null)
        {
        T actual = (T) object;
        mismatch =
          matchers.stream()
          .filter( m -> !m.matches( actual))
          .findFirst()
          .orElse( null);
        }
      }

    /**
     * Returns the object matched by this CompositeMatcher.
     */
    public Object getMatched()
      {
      return matched;
      }

    /**
     * Returns the first Matcher not satisfied by the {@link #getMatched matched} object.
     */
    public Optional<Matcher<? super T>> getMismatch()
      {
      return Optional.ofNullable( mismatch);
      }

    public String toString()
      {
      return String.format( "%s[%s]", getClass().getSimpleName(), BaseCompositeMatcher.this.getClass().getSimpleName());
      }
    }
   
  /**
   * Creates a new BaseCompositeMatcher instance.
   */
  protected BaseCompositeMatcher( T expected)
    {
    this.expected = expected;
    this.matchers = new ArrayList<Matcher<? super T>>();
    }

  public boolean matches( Object actual)
    {
    return !getCompositeMatcher( actual).getMismatch().isPresent();
    }

  public void describeTo( Description description)
    {
    description .appendText( String.valueOf( expected));
    getMismatch().ifPresent( m -> { description.appendText( " matching "); m.describeTo( description); });
    }

  public void describeMismatch( Object actual, Description description)
    {
    getCompositeMatcher( actual).getMismatch().ifPresent( m -> m.describeMismatch( actual, description));
    }

  /**
   * If the given object is incompatible with the expected type, returns a Matcher that describes the mismatch.
   * Otherwise, return null;
   */
  protected Matcher<? super T> getTypeMismatch( Object object)
    {
    // By default, the expected type is undefined. Successful cast of the given object is assumed.
    return null;
    }

  /**
   * Adds the Matcher supplied for the expected object to the matchers applied by this Matcher.
   */
  protected void expectThat( Function<T,Matcher<? super T>> matcherSupplier)
    {
    matchers.add( matcherSupplier.apply( expected));
    }

  /**
   * Adds a {@link MatchesFunction} matcher for the expected object to the matchers applied by this Matcher.
   */
  protected <R> void expectThat( String functionName, Function<T,R> function, Function<R,Matcher<R>> resultMatcherSupplier)
    {
    matchers.add( new MatchesFunction<T,R>( functionName, function, expected, resultMatcherSupplier));
    }

  /**
   * Decorates another Matcher supplier to provide a more expressive interface.
   */
  protected Function<T,Matcher<? super T>> matches( Function<T,Matcher<? super T>> matcherSupplier)
    {
    return matcherSupplier;
    }

  /**
   * Returns a new {@link MatchesFunction.Supplier} that supplies a {@link MatchesFunction} matcher using
   * the given function.
   */
  protected <R> MatchesFunction.Supplier<T,R> valueOf( String functionName, Function<T,R> function)
    {
    return new MatchesFunction.Supplier<>( functionName, function);
    }

  /**
   * Returns a new {@link ContainsMembers.Supplier} that supplies a {@link ContainsMembers} matcher using
   * the given member Matcher supplier.
   */
  protected static <T,S extends Iterable<T>> ContainsMembers.Supplier<T,S> containsMembersMatching( Function<T,Matcher<T>> memberMatcherSupplier)
    {
    return new ContainsMembers.Supplier<>( memberMatcherSupplier);
    }

  /**
   * Returns a new {@link ContainsElements.Supplier} that supplies a {@link ContainsElements} matcher using
   * the given element Matcher supplier.
   */
  protected static <T> ContainsElements.Supplier<T> containsElementsMatching( Function<T,Matcher<T>> elementMatcherSupplier)
    {
    return new ContainsElements.Supplier<>( elementMatcherSupplier);
    }

  /**
   * Returns a new {@link VisitsMembers.Supplier} that supplies a {@link VisitsMembers} matcher using
   * the given member Matcher supplier.
   */
  protected static <T> VisitsMembers.Supplier<T> visitsMembersMatching( Function<T,Matcher<T>> memberMatcherSupplier)
    {
    return new VisitsMembers.Supplier<>( memberMatcherSupplier);
    }

  /**
   * Returns a new {@link ListsMembers.Supplier} that supplies a {@link ListsMembers} matcher using
   * the given member Matcher supplier.
   */
  protected static <T,S extends Iterable<T>> ListsMembers.Supplier<T,S> listsMembersMatching( Function<T,Matcher<T>> memberMatcherSupplier)
    {
    return new ListsMembers.Supplier<>( memberMatcherSupplier);
    }

  /**
   * Returns a new {@link ListsMatching.Supplier} that supplies a {@link ListsMatching} matcher using
   * the given member Matcher supplier.
   */
  protected static <T,S extends Iterable<T>> ListsMatching.Supplier<T,S> listsMatching( Function<T,Matcher<T>> memberMatcherSupplier)
    {
    return new ListsMatching.Supplier<>( memberMatcherSupplier);
    }

  /**
   * Returns a new {@link ListsElements.Supplier} that supplies a {@link ListsElements} matcher using
   * the given element Matcher supplier.
   */
  protected static <T> ListsElements.Supplier<T> listsElementsMatching( Function<T,Matcher<T>> elementMatcherSupplier)
    {
    return new ListsElements.Supplier<>( elementMatcherSupplier);
    }

  /**
   * Returns a new {@link VisitsList.Supplier} that supplies a {@link VisitsList} matcher using
   * the given member Matcher supplier.
   */
  protected static <T> VisitsList.Supplier<T> visitsListMatching( Function<T,Matcher<T>> memberMatcherSupplier)
    {
    return new VisitsList.Supplier<>( memberMatcherSupplier);
    }

  /**
   * Returns the CompositeMatcher for the given actual object
   */
  private CompositeMatcher getCompositeMatcher( Object actual)
    {
    if( compositeMatcher == null || compositeMatcher.getMatched() != actual)
      {
      compositeMatcher = new CompositeMatcher( actual);
      }

    return compositeMatcher;
    }

  /**
   * Returns the first Matcher not satisfied by the last invocation of {@link #matches matches()}.
   */
  private Optional<Matcher<? super T>> getMismatch()
    {
    return
      compositeMatcher == null
      ? Optional.empty()
      : compositeMatcher.getMismatch();
    }

  public String toString()
    {
    return String.format( "%s[]", getClass().getSimpleName());
    }
  }
