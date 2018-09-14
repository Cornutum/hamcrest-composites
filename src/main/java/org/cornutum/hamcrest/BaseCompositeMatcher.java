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
  private List<Matcher<T>> matchers;

  /**
   * Applies a sequence of Matchers to a single object.
   */
  private class CompositeMatcher
    {
    private final Object matched;
    private final Matcher<T> mismatch;
    
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
      else
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
    public Optional<Matcher<T>> getMismatch()
      {
      return Optional.ofNullable( mismatch);
      }
    }
   
  /**
   * Creates a new BaseCompositeMatcher instance.
   */
  protected BaseCompositeMatcher( T expected)
    {
    this.expected = expected;
    this.matchers = new ArrayList<Matcher<T>>();
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
   * Adds the Matcher supplied for the expected object to the matchers applied by this Matcher.
   */
  protected void expectThat( Function<T,Matcher<T>> matcherSupplier)
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
   * Returns a new {@link MatchesFunctionSupplier} that supplies a {@link MatchesFunction} matcher using
   * the given function.
   */
  protected <R> MatchesFunctionSupplier<T,R> valueOf( String functionName, Function<T,R> function)
    {
    return new MatchesFunctionSupplier<>( functionName, function);
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
  private Optional<Matcher<T>> getMismatch()
    {
    return
      compositeMatcher == null
      ? Optional.empty()
      : compositeMatcher.getMismatch();
    }

  /**
   * Builds and supplies a {@link MatchesFunction} matcher for a specified source object.
   */
  public static class MatchesFunctionSupplier<T,R> implements Function<T,Matcher<T>>
    {
    private String functionName;
    private Function<T,R> function;
    private Function<R,Matcher<R>> resultMatcherSupplier;
    
    /**
     * Creates a new MatchesFunctionSupplier that supplies a {@link MatchesFunction} matcher using
     * the given function.
     */
    public MatchesFunctionSupplier( String functionName, Function<T,R> function)
      {
      this.functionName = functionName;
      this.function = function;
      }

    /**
     * Changes the result Matcher supplier function for the {@link MatchesFunction} matcher supplied.
     */
    public MatchesFunctionSupplier<T,R> matches( Function<R,Matcher<R>> resultMatcherSupplier)
      {
      this.resultMatcherSupplier = resultMatcherSupplier;
      return this;
      }

    /**
     * Returns the {@link MatchesFunction} matcher supplied for the given source object.
     */
    public Matcher<T> apply( T source)
      {
      return new MatchesFunction<T,R>( functionName, function, source, resultMatcherSupplier);
      }
    }
  }
