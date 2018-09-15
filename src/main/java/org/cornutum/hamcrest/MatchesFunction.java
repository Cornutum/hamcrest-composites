//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.CompositeUtils.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Applies a Matcher to compare values derived by a function.
 * <P/>
 * To match an expected (source) object of type <CODE>T</CODE> with another (matched) object, compares
 * the values of type <CODE>R</CODE> derived using a specified function. To compare function values,
 * uses a Matcher derived from the source object using a specified "result Matcher supplier" function.
 */
public class MatchesFunction<T,R> extends BaseMatcher<T>
  {
  private final String functionName;
  private final Function<T,R> function;
  private final Class<T> sourceClass;
  private final Matcher<R> resultMatcher;
  private FunctionMatcher functionMatcher;

  /**
   * Matches values derived by the function.
   */
  private class FunctionMatcher
    {
    private final Object matched;
    private final StringBuilder mismatch;
    
    /**
     * Creates a new FunctionMatcher instance.
     */
    @SuppressWarnings("unchecked")
    public FunctionMatcher( Object object)
      {
      matched = object;
      mismatch = new StringBuilder();

      R actualResult;
      if( object == null)
        {
        mismatch.append( getFunctionName() + " can't be derived from a null object");
        }
      else if( !sourceClass.isInstance( object))
        {
        mismatch.append( getFunctionName() + " can't be derived from an object of class=" + object.getClass());
        }
      else if( !resultMatcher.matches( (actualResult = function.apply( (T) object))))
        {
        mismatch.append( mismatchFor( resultMatcher, actualResult));
        }
      }

    /**
     * Returns the object matched by this FunctionMatcher.
     */
    public Object getMatched()
      {
      return matched;
      }

    /**
     * Returns a description of any mismatch between the expected function result and the {@link #getMatched matched} object.
     */
    public Optional<String> getMismatch()
      {
      return
        mismatch.length() > 0
        ? Optional.of( mismatch.toString())
        : Optional.empty();
      }
    }

  /**
   * Builds and supplies a {@link MatchesFunction} matcher for a specified source object.
   */
  public static class Supplier<T,R> implements Function<T,Matcher<T>>
    {
    private String functionName;
    private Function<T,R> function;
    private Function<R,Matcher<R>> resultMatcherSupplier;
    
    /**
     * Creates a new Supplier that supplies a {@link MatchesFunction} matcher using
     * the given function.
     */
    public Supplier( String functionName, Function<T,R> function)
      {
      this.functionName = functionName;
      this.function = function;
      }

    /**
     * Changes the result Matcher supplier function for the {@link MatchesFunction} matcher supplied.
     */
    public Supplier<T,R> matches( Function<R,Matcher<R>> resultMatcherSupplier)
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

  /**
   * Defines a fluent interface for building a {@link MatchesFunction} matcher.
   */
  public static class Builder<T,R>
    {
    private String functionName;
    private Function<T,R> function;
    private T source;
    
    /**
     * Creates a new Builder to build a {@link MatchesFunction} matcher for the given source object.
     */
    public Builder( T source)
      {
      this.source = source;
      }

    /**
     * Changes the function used by the {@link MatchesFunction} matcher.
     */
    public Builder<T,R> byValueOf( String functionName, Function<T,R> function)
      {
      this.functionName = functionName;
      this.function = function;
      return this;
      }

    /**
     * Changes the result Matcher supplier function used by the {@link MatchesFunction} matcher
     * and returns the specified {@link MatchesFunction} matcher.
     */
    public Matcher<T> matches( Function<R,Matcher<R>> resultMatcherSupplier)
      {
      return new MatchesFunction<T,R>( functionName, function, source, resultMatcherSupplier);
      }
    }
   
  /**
   * Creates a new MatchesFunction instance.
   */
  @SuppressWarnings("unchecked")
  public MatchesFunction( String functionName, Function<T,R> function, T source, Function<R,Matcher<R>> resultMatcherSupplier)
    {
    if( source == null)
      {
      throw new IllegalArgumentException( "Source object must be non-null");
      }
    
    this.functionName = functionName;
    this.function = function;
    sourceClass = (Class<T>) source.getClass();

    R sourceResult = function.apply( source);
    resultMatcher =
      sourceResult == null
      ? new IsNull<R>()
      : resultMatcherSupplier.apply( sourceResult);
    }

  /**
   * Returns the function name for this matcher.
   */
  public String getFunctionName()
    {
    return functionName;
    }

  public boolean matches( Object actual)
    {
    return !getFunctionMatcher( actual).getMismatch().isPresent();
    }

  public void describeTo( Description description)
    {
    description
      .appendText( getFunctionName())
      .appendText( "=")
      .appendDescriptionOf( resultMatcher);
    }

  public void describeMismatch( Object actual, Description description)
    {
    getFunctionMatcher( actual).getMismatch().ifPresent( mismatch -> description.appendText( mismatch));
    }

  /**
   * Returns the FunctionMatcher for the given actual object
   */
  private FunctionMatcher getFunctionMatcher( Object actual)
    {
    if( functionMatcher == null || functionMatcher.getMatched() != actual)
      {
      functionMatcher = new FunctionMatcher( actual);
      }

    return functionMatcher;
    }
  }
